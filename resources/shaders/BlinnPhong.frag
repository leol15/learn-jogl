#version 450 core

void capColor(inout vec3 color);
void capColor(inout vec4 color);
void getDiffuse(in vec3 L, in vec3 N, in vec3 C, out vec3 diffuse);
void getSpecular(in vec3 L, in vec3 E, in vec3 N, in vec3 C, in float materialSpecularness, out vec3 specular);

in vec3 surfacePos;
in vec3 surfaceNormal;
vec3 N = normalize(surfaceNormal);

out vec4 fragColor;

struct point_light {
    vec3 position;
    vec3 intensity;
    vec3 attenuation;
};
struct dir_light {
    vec3 direction;
    vec3 intensity;
};
struct spot_light {
    vec3 position;
    vec3 intensity;
    vec3 attenuation;
    vec3 direction;
    vec3 angle;
};

uniform float materialSpecularness = 5;
uniform vec4 materialColor = vec4(1);

layout (std140) uniform ALL_THE_LIGHTS
{
    point_light PL[1];
    dir_light DL[1];
    spot_light SL[1];
    vec4 numActiveLights;
};

layout (std140) uniform CAMERA_INFO
{
    mat4 view;
    mat4 projection;
    vec3 eyePos;
    vec3 ambientColor;
};


void calculatePointLight(in int index, out vec3 color);
void calculateDirLight(in int index, out vec3 color);
void calculateSpotLight(in int index, out vec3 color);

void main() {
    // ambinent light
    vec3 ambinent = materialColor.rgb * ambientColor;
    vec3 color = vec3(0);
    vec3 finalColor = vec3(0);
    // point light
    for (int i = 0; i < numActiveLights.x; i++) {
        calculatePointLight(i, color);
        finalColor = finalColor + color;
    }
    // directional light
    for (int i = 0; i < numActiveLights.y; i++) {
        calculateDirLight(i, color);
        finalColor = finalColor + color;
    }
    // spot light
    for (int i = 0; i < numActiveLights.z; i++) {
        calculateSpotLight(i, color);
        finalColor = finalColor + color;
    }

    fragColor = vec4(finalColor + ambinent, 1);
    capColor(fragColor);

    // debug
    // fragColor = vec4(numActiveLights.xyz, 1);
}


// helpers

void calculatePointLight(in int index, out vec3 color) {
    vec3 L = PL[index].position - surfacePos;
    float lightDistance = length(L);
    L = L / lightDistance;

    // is facing light?
    if (dot(L, N) < 0) { return; }
    
    // diffuse light
    vec3 diffuse = vec3(0);
    getDiffuse(L, N, PL[index].intensity, diffuse);
    
    // specular
    vec3 E = normalize(eyePos - surfacePos);
    vec3 specular = vec3(0);
    getSpecular(L, E, N, PL[index].intensity, materialSpecularness, specular);

    // combine
    // distance fall off
    float distanceFallOff = 1 / dot(PL[index].attenuation, vec3(pow(lightDistance, 2), lightDistance, 1));
    color = (diffuse + specular) * materialColor.rgb * distanceFallOff;
    capColor(color);
}

void calculateDirLight(in int index, out vec3 color) {
    // is facing light?
    vec3 L = -DL[index].direction;
    if (dot(L, N) < 0) { return; }

    // diffuse light
    vec3 diffuse = vec3(0);
    getDiffuse(L, N, DL[index].intensity, diffuse);

    // specular
    vec3 E = normalize(eyePos - surfacePos);
    vec3 specular = vec3(0);
    getSpecular(L, E, N, DL[index].intensity, materialSpecularness, specular);

    // combine
    color = (diffuse + specular) * materialColor.rgb;
    capColor(color);
}


void calculateSpotLight(in int index, out vec3 color) {
    vec3 L = SL[index].position - surfacePos;
    float lightDistance = length(L);
    L = L / lightDistance;

    // is facing light?
    if (dot(L, N) < 0) { return; }
    // is within angle?
    if (dot(L, -normalize(SL[index].direction)) < cos(radians(SL[index].angle.x))) {
        return;
    }

    // diffuse light
    vec3 diffuse = vec3(0);
    getDiffuse(L, N, SL[index].intensity, diffuse);

    // specular
    vec3 E = normalize(eyePos - surfacePos);
    vec3 specular = vec3(0);
    getSpecular(L, E, N, SL[index].intensity, materialSpecularness, specular);

    // combine
    // distance fall off
    float distanceFallOff = 1 / dot(SL[index].attenuation, vec3(pow(lightDistance, 2), lightDistance, 1));
    color = (diffuse + specular) * materialColor.rgb * distanceFallOff;
    capColor(color);
}
