#version 450 core

in vec3 surfacePos;
in vec3 surfaceNormal;

out vec4 fragColor;

struct point_light {
    vec3 position;
    vec3 intensity;
    vec3 attenuation;
};
uniform float specularHardness = 5;
uniform vec4 surfaceColor = vec4(1);

layout (std140) uniform ALL_THE_LIGHTS
{
    point_light PL[1];
};

layout (std140) uniform ViewAndProjection
{
    mat4 view;
    mat4 projection;
    vec3 eyePos;
};


void calculatePointLight(in int index, out vec3 color);
// L: normalized direction vector to Light
// C: color vector
void getDiffuse(in vec3 L, in vec3 C, out vec3 diffuse);
void getSpecular(in vec3 L, in vec3 E, in vec3 C, out vec3 specular);

void main() {
    // fragColor = vec4(1, 1, 1, 1.0);
    vec3 pl1 = vec3(0);
    calculatePointLight(0, pl1);
    fragColor = vec4(pl1, 1);
    // debug
    // fragColor = vec4(surfaceNormal.xyz, 1);
}


void calculatePointLight(in int index, out vec3 color) {
    vec3 L = PL[index].position - surfacePos;
    float lightDistance = length(L);
    L = L / lightDistance;

    // is facing light?
    if (dot(L, surfaceNormal) < 0) { return; }

    // distance fall off
    float distanceFallOff = 1 / dot(PL[index].attenuation, vec3(pow(lightDistance, 2), lightDistance, 1));

    // TODO ambinent light
    vec3 ambinent = surfaceColor.rgb * 0.1;
    // diffuse light
    vec3 diffuse = vec3(0);
    getDiffuse(L, PL[index].intensity, diffuse);
    diffuse = diffuse * surfaceColor.rgb * distanceFallOff;
    // specular
    vec3 E = normalize(eyePos - surfacePos);
    vec3 specular = vec3(0);
    getSpecular(L, E, PL[index].intensity, specular);
    specular = specular * surfaceColor.rgb * distanceFallOff;
    diffuse = vec3(0, 0, 0);
    // combine
    // color = ambinent + diffuse + specular;
    color = specular;
}

// L: normalized direction vector to Light
// C: light color
void getDiffuse(in vec3 L, in vec3 C, out vec3 diffuse) {
    float NdotL = max(0, dot(surfaceNormal, L));
    diffuse = NdotL * C;
}

void getSpecular(in vec3 L, in vec3 E, in vec3 C, out vec3 specular) {
    vec3 halfVec = normalize(normalize(L) + normalize(E));
    float NdotH = max(0, dot(surfaceNormal, halfVec));
    specular = C * pow(NdotH, specularHardness);
}
