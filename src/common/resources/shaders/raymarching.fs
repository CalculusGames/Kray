#version 330

in vec2 fragTexCoord;
in vec4 fragColor;
in vec3 fragPosition;
in vec3 fragNormal;

out vec4 finalColor;

uniform vec3 viewEye;

uniform vec4 colDiffuse;
uniform sampler2D texture0;
uniform vec4 tintColor;

uniform vec3 lightDir;
uniform vec4 lightColor;
uniform float lightIntensity;

uniform vec4 bgColorTop;
uniform vec4 bgColorBottom;
uniform float bgGradientStrength;

uniform bool fogEnabled;
uniform vec4 fogColor;
uniform float fogDensity;

uniform bool aoEnabled;
uniform float aoStrength;

uniform bool shadowsEnabled;

vec3 calculateLighting(vec3 pos, vec3 nor, vec3 viewDir, vec3 matCol)
{
	vec3 lig = normalize(lightDir);
	vec3 hal = normalize(lig - viewDir);
	vec3 ref = reflect(viewDir, nor);

	// Lighting components
	float amb = clamp(0.5 + 0.5 * nor.y, 0.0, 1.0);
	float dif = clamp(dot(nor, lig), 0.0, 1.0);
	float bac = clamp(dot(nor, normalize(vec3(-lig.x, 0.0, -lig.z))), 0.0, 1.0) * clamp(1.0 - pos.y, 0.0, 1.0);
	float dom = smoothstep(-0.1, 0.1, ref.y);
	float fre = pow(clamp(1.0 + dot(nor, viewDir), 0.0, 1.0), 2.0);

	// Simple occlusion approximation
	float occ = 1.0;
	if (aoEnabled)
	{
		occ = clamp(1.0 - aoStrength * (1.0 - amb), 0.0, 1.0);
	}

	// Shadow approximation (simplified for mesh rendering)
	if (shadowsEnabled)
	{
		dif *= 0.7 + 0.3 * clamp(dot(nor, lig), 0.0, 1.0);
		dom *= 0.8;
	}

	// Specular
	float spe = pow(clamp(dot(nor, hal), 0.0, 1.0), 16.0) *
	dif *
	(0.04 + 0.96 * pow(clamp(1.0 + dot(hal, viewDir), 0.0, 1.0), 5.0));

	// Combine lighting
	vec3 lin = vec3(0.0);
	lin += lightIntensity * dif * lightColor.rgb;
	lin += 0.40 * amb * vec3(0.40, 0.60, 1.00) * occ;
	lin += 0.50 * dom * vec3(0.40, 0.60, 1.00) * occ;
	lin += 0.50 * bac * vec3(0.25, 0.25, 0.25) * occ;
	lin += 0.25 * fre * vec3(1.00, 1.00, 1.00) * occ;

	vec3 col = matCol * lin;
	col += 10.00 * spe * vec3(1.00, 0.90, 0.70);

	return col;
}

void main()
{
	// Normalize the fragment normal
	vec3 nor = normalize(fragNormal);
	vec3 pos = fragPosition;

	// View direction
	vec3 viewDir = normalize(pos - viewEye);

	// Material color from Raylib material system
	vec4 texelColor = texture(texture0, fragTexCoord);
	vec4 baseColor = texelColor * colDiffuse * fragColor * tintColor;
	vec3 matCol = baseColor.rgb;

	// Calculate lighting
	vec3 col = calculateLighting(pos, nor, viewDir, matCol);

	// Distance from camera for fog
	float dist = length(pos - viewEye);

	// Apply fog
	if (fogEnabled)
	{
		col = mix(col, fogColor.rgb, 1.0 - exp(-fogDensity * dist * dist * dist));
	}

	// Clamp and output
	col = clamp(col, 0.0, 1.0);
	finalColor = vec4(col, baseColor.a);
}
