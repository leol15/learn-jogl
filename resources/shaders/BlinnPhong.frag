#version 450 core

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
void capColor(inout vec3 color);
void capColor(inout vec4 color);

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

// L: normalized direction vector to Light
// C: color vector
void getDiffuse(in vec3 L, in vec3 C, out vec3 diffuse);
void getSpecular(in vec3 L, in vec3 E, in vec3 C, out vec3 specular);

void calculatePointLight(in int index, out vec3 color) {
    vec3 L = PL[index].position - surfacePos;
    float lightDistance = length(L);
    L = L / lightDistance;

    // is facing light?
    if (dot(L, N) < 0) { return; }

    // distance fall off
    float distanceFallOff = 1 / dot(PL[index].attenuation, vec3(pow(lightDistance, 2), lightDistance, 1));

    // diffuse light
    vec3 diffuse = vec3(0);
    getDiffuse(L, PL[index].intensity, diffuse);
    diffuse = diffuse * materialColor.rgb * distanceFallOff;
    // specular
    vec3 E = normalize(eyePos - surfacePos);
    vec3 specular = vec3(0);
    getSpecular(L, E, PL[index].intensity, specular);
    specular = specular * materialColor.rgb * distanceFallOff;
    // combine
    color = diffuse + specular;
    capColor(color);
}


void calculateDirLight(in int index, out vec3 color) {
    // is facing light?
    vec3 L = -DL[index].direction;
    if (dot(L, N) < 0) { return; }

    // diffuse light
    vec3 diffuse = vec3(0);
    getDiffuse(L, DL[index].intensity, diffuse);
    diffuse = diffuse * materialColor.rgb;
    // specular
    vec3 E = normalize(eyePos - surfacePos);
    vec3 specular = vec3(0);
    getSpecular(L, E, DL[index].intensity, specular);
    specular = specular * materialColor.rgb;
    // combine
    color = diffuse + specular;
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

    // distance fall off
    float distanceFallOff = 1 / dot(SL[index].attenuation, vec3(pow(lightDistance, 2), lightDistance, 1));

    // diffuse light
    vec3 diffuse = vec3(0);
    getDiffuse(L, SL[index].intensity, diffuse);
    diffuse = diffuse * materialColor.rgb * distanceFallOff;
    // specular
    vec3 E = normalize(eyePos - surfacePos);
    vec3 specular = vec3(0);
    getSpecular(L, E, SL[index].intensity, specular);
    specular = specular * materialColor.rgb * distanceFallOff;
    // combine
    color = diffuse + specular;
    capColor(color);
}


// L: normalized direction vector to Light
// C: light color
void getDiffuse(in vec3 L, in vec3 C, out vec3 diffuse) {
    float NdotL = max(0, dot(N, L));
    diffuse = NdotL * C;
}

void getSpecular(in vec3 L, in vec3 E, in vec3 C, out vec3 specular) {
    if (materialSpecularness == 0) {
        // no specular
        specular = vec3(0);
        return;
    }
    vec3 halfVec = normalize(normalize(L) + normalize(E));
    float NdotH = max(0, dot(N, halfVec));
    specular = C * pow(NdotH, materialSpecularness);
}

void capColor(inout vec3 color) {
    clamp(color, vec3(0, 0, 0), vec3(1, 1, 1));
}
void capColor(inout vec4 color) {
    clamp(color, vec4(0, 0, 0, 0), vec4(1, 1, 1, 1));
}