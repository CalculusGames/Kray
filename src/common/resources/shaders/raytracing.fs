#version 330

in vec2 fragTexCoord;
in vec4 fragColor;
in vec3 fragPosition;
in vec3 fragNormal;

out vec4 finalColor;

uniform sampler2D texture0;
uniform sampler2D texture1;
uniform sampler2D texture2;
uniform sampler2D texture3;
uniform sampler2D texture4;
uniform sampler2D texture5;
uniform vec4 colDiffuse;
uniform vec3 viewPos;

// Material texture flags
uniform bool useAlbedoMap = true;
uniform bool useMetalnessMap = false;
uniform bool useNormalMap = false;
uniform bool useRoughnessMap = false;
uniform bool useAOMap = false;
uniform bool useEmissionMap = false;

uniform int maxBounces;
uniform float reflectivity;
uniform float roughness;
uniform float metalness;
uniform bool enableReflections;
uniform bool enableShadows;

#define MAX_LIGHTS 64
#define LIGHT_DIRECTIONAL 0
#define LIGHT_POINT 1
#define LIGHT_SPOT 2

struct Light {
	int enabled;
	int type;
	vec3 position;
	vec3 target;
	vec4 color;
	float intensity;
	float radius;      // For point lights
	float cutoff;      // For spot lights (cosine of angle)
};

uniform Light lights[MAX_LIGHTS];
uniform vec4 ambient;

uniform bool hasGroundPlane = true;
uniform float groundPlaneY = 0.0;
uniform vec4 groundPlaneColor = vec4(0.3, 0.3, 0.3, 1.0);


vec3 applyNormalMap(vec3 normal, vec3 tangentNormal) {
	// create TBN matrix (tangent, bitangent, normal)
	vec3 Q1 = dFdx(fragPosition);
	vec3 Q2 = dFdy(fragPosition);
	vec2 st1 = dFdx(fragTexCoord);
	vec2 st2 = dFdy(fragTexCoord);

	vec3 N = normalize(normal);
	vec3 T = normalize(Q1 * st2.t - Q2 * st1.t);
	vec3 B = -normalize(cross(N, T));
	mat3 TBN = mat3(T, B, N);

	return normalize(TBN * tangentNormal);
}

struct MaterialProperties {
	vec4 albedo;
	float metalness;
	float roughness;
	float ao;
	vec3 emission;
	vec3 normal;
};

MaterialProperties sampleMaterial(vec2 uv, vec3 worldNormal) {
	MaterialProperties mat;

	// albedo
	if (useAlbedoMap) {
		mat.albedo = texture(texture0, uv) * colDiffuse * fragColor;
	} else {
		mat.albedo = colDiffuse * fragColor;
	}

	// metalness
	if (useMetalnessMap) {
		mat.metalness = texture(texture1, uv).r;
	} else {
		mat.metalness = metalness;
	}

	// roughness
	if (useRoughnessMap) {
		mat.roughness = texture(texture3, uv).r;
	} else {
		mat.roughness = roughness;
	}

	// ambient Occlusion
	if (useAOMap) {
		mat.ao = texture(texture4, uv).r;
	} else {
		mat.ao = 1.0;
	}

	// emission
	if (useEmissionMap) {
		mat.emission = texture(texture5, uv).rgb;
	} else {
		mat.emission = vec3(0.0);
	}

	// normal mapping
	if (useNormalMap) {
		vec3 tangentNormal = texture(texture2, uv).xyz * 2.0 - 1.0;
		mat.normal = applyNormalMap(worldNormal, tangentNormal);
	} else {
		mat.normal = worldNormal;
	}

	return mat;
}

vec3 fresnelSchlick(float cosTheta, vec3 F0) {
	return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

// trowbridge-reitz GGX distribution
float distributionGGX(vec3 N, vec3 H, float roughness) {
	float a = roughness * roughness;
	float a2 = a * a;
	float NdotH = max(dot(N, H), 0.0);
	float NdotH2 = NdotH * NdotH;

	float num = a2;
	float denom = (NdotH2 * (a2 - 1.0) + 1.0);
	denom = 3.14159265359 * denom * denom;

	return num / denom;
}

// schlick-geometric shadowing
float geometrySchlickGGX(float NdotV, float roughness) {
	float r = (roughness + 1.0);
	float k = (r * r) / 8.0;

	float num = NdotV;
	float denom = NdotV * (1.0 - k) + k;

	return num / denom;
}

// smith's method for geometry shadowing
float geometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
	float NdotV = max(dot(N, V), 0.0);
	float NdotL = max(dot(N, L), 0.0);
	float ggx2 = geometrySchlickGGX(NdotV, roughness);
	float ggx1 = geometrySchlickGGX(NdotL, roughness);

	return ggx1 * ggx2;
}

struct Ray {
	vec3 origin;
	vec3 direction;
};

struct HitInfo {
	bool hit;
	float t;
	vec3 position;
	vec3 normal;
	vec4 color;
	float reflectivity;
};

HitInfo intersectScene(Ray ray) {
	HitInfo hitInfo;
	hitInfo.hit = false;
	hitInfo.t = 1000000.0;

	if (hasGroundPlane && ray.direction.y < -0.001) {
		float t = (groundPlaneY - ray.origin.y) / ray.direction.y;
		if (t > 0.001 && t < hitInfo.t) {
			hitInfo.hit = true;
			hitInfo.t = t;
			hitInfo.position = ray.origin + ray.direction * t;
			hitInfo.normal = vec3(0.0, 1.0, 0.0);
			hitInfo.color = groundPlaneColor;
			hitInfo.reflectivity = 0.1;
		}
	}

	return hitInfo;
}

bool isInShadow(vec3 position, vec3 lightDir, float lightDistance) {
	if (!enableShadows) return false;

	Ray shadowRay;
	shadowRay.origin = position + fragNormal * 0.001; // bias to prevent self-intersection
	shadowRay.direction = lightDir;

	HitInfo hitInfo = intersectScene(shadowRay);

	return hitInfo.hit && hitInfo.t < lightDistance;
}

vec3 computeLighting(vec3 position, vec3 normal, vec3 viewDir, MaterialProperties mat) {
	vec3 Lo = vec3(0.0);

	vec3 F0 = vec3(0.04);
	F0 = mix(F0, mat.albedo.rgb, mat.metalness);

	for (int i = 0; i < MAX_LIGHTS; i++) {
		if (lights[i].enabled != 1) continue;

		vec3 lightDir;
		float attenuation = 1.0;
		float lightDistance = 1000000.0;

		if (lights[i].type == LIGHT_DIRECTIONAL) {
			lightDir = normalize(lights[i].target - lights[i].position);
			lightDistance = 1000000.0;
		}
		else if (lights[i].type == LIGHT_POINT) {
			vec3 toLight = lights[i].position - position;
			lightDistance = length(toLight);
			lightDir = toLight / lightDistance;

			// attenuation for point lights
			attenuation = lights[i].intensity / (1.0 + lightDistance * lightDistance / (lights[i].radius * lights[i].radius));
		}
		else if (lights[i].type == LIGHT_SPOT) {
			vec3 toLight = lights[i].position - position;
			lightDistance = length(toLight);
			lightDir = toLight / lightDistance;

			vec3 spotDir = normalize(lights[i].target - lights[i].position);
			float theta = dot(lightDir, -spotDir);

			if (theta > lights[i].cutoff) {
				float epsilon = lights[i].cutoff * 0.1;
				attenuation = clamp((theta - lights[i].cutoff) / epsilon, 0.0, 1.0);
				attenuation *= lights[i].intensity / (1.0 + lightDistance * lightDistance / (lights[i].radius * lights[i].radius));
			} else {
				attenuation = 0.0;
			}
		}

		if (attenuation < 0.001) continue;

		bool inShadow = isInShadow(position, lightDir, lightDistance);
		if (inShadow) continue;

		// cook-torrance BRDF
		vec3 H = normalize(viewDir + lightDir);
		float NdotL = max(dot(normal, lightDir), 0.0);

		// calculate per-light radiance
		vec3 radiance = lights[i].color.rgb * lights[i].intensity * attenuation;

		// cook-torrance specular BRDF
		float NDF = distributionGGX(normal, H, mat.roughness);
		float G = geometrySmith(normal, viewDir, lightDir, mat.roughness);
		vec3 F = fresnelSchlick(max(dot(H, viewDir), 0.0), F0);

		vec3 numerator = NDF * G * F;
		float denominator = 4.0 * max(dot(normal, viewDir), 0.0) * NdotL + 0.0001;
		vec3 specular = numerator / denominator;

		// energy conservation
		vec3 kS = F;
		vec3 kD = vec3(1.0) - kS;
		kD *= 1.0 - mat.metalness;

		Lo += (kD * mat.albedo.rgb / 3.14159265359 + specular) * radiance * NdotL;
	}

	vec3 ambient = ambient.rgb * ambient.a * mat.albedo.rgb * mat.ao;

	// emission
	vec3 color = ambient + Lo + mat.emission;

	return color;
}

vec3 traceRay(Ray ray, MaterialProperties baseMat) {
	vec3 color = vec3(0.0);
	vec3 throughput = vec3(1.0);

	vec3 currentPos = fragPosition;
	vec3 currentNormal = baseMat.normal;
	MaterialProperties currentMat = baseMat;

	for (int bounce = 0; bounce <= maxBounces; bounce++) {
		vec3 viewDir = normalize(viewPos - currentPos);
		vec3 lighting = computeLighting(currentPos, currentNormal, viewDir, currentMat);

		if (bounce == 0) {
			color += throughput * lighting;
		} else {
			float currentReflectivity = mix(reflectivity, 0.9, currentMat.metalness);
			color += throughput * lighting * currentReflectivity;
		}

		float currentReflectivity = mix(reflectivity, 0.9, currentMat.metalness);
		if (!enableReflections || bounce >= maxBounces || currentReflectivity < 0.01) break;

		// create reflection ray
		vec3 reflectDir = reflect(ray.direction, currentNormal);
		Ray reflectRay;
		reflectRay.origin = currentPos + currentNormal * 0.001;
		reflectRay.direction = reflectDir;

		HitInfo hitInfo = intersectScene(reflectRay);

		if (!hitInfo.hit) {
			// hit sky/environment
			vec3 skyColor = mix(vec3(0.5, 0.7, 1.0), vec3(0.3, 0.5, 0.8), reflectDir.y * 0.5 + 0.5);
			color += throughput * skyColor * currentReflectivity;
			break;
		}

		throughput *= currentReflectivity;
		currentPos = hitInfo.position;
		currentNormal = hitInfo.normal;

		// apply material at hit point
		currentMat.albedo = hitInfo.color;
		currentMat.metalness = 0.0;
		currentMat.roughness = 0.5;
		currentMat.ao = 1.0;
		currentMat.emission = vec3(0.0);
		currentMat.normal = hitInfo.normal;
		ray.direction = reflectDir;
	}

	return color;
}

void main()
{
	// sample material properties from textures
	MaterialProperties mat = sampleMaterial(fragTexCoord, normalize(fragNormal));

	Ray primaryRay;
	primaryRay.origin = fragPosition;
	primaryRay.direction = normalize(fragPosition - viewPos);

	vec3 color = traceRay(primaryRay, mat);

	// gamma correction
	color = pow(color, vec3(1.0/2.2));
	color = clamp(color, 0.0, 1.0);

	finalColor = vec4(color, mat.albedo.a);
}
