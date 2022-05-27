#version 450 core

// for IDE support, copy those header into the shader file
// all directions should be normalized (L, N, E)
void capColor(inout vec3 color);
void capColor(inout vec4 color);
void getDiffuse(in vec3 L, in vec3 N, in vec3 C, out vec3 diffuse);
void getSpecular(in vec3 L, in vec3 E, in vec3 N, in vec3 C, in float materialSpecularness, out vec3 specular);

void capColor(inout vec3 color) {
    clamp(color, vec3(0, 0, 0), vec3(1, 1, 1));
}

void capColor(inout vec4 color) {
    clamp(color, vec4(0, 0, 0, 0), vec4(1, 1, 1, 1));
}


// L: normalized direction vector to Light
// N: surface normal
// C: light color
void getDiffuse(in vec3 L, in vec3 N, in vec3 C, out vec3 diffuse) {
    float NdotL = max(0, dot(N, L));
    diffuse = NdotL * C;
}


// L: normalized direction vector to Light
// E: eye direction
// N: surface normal
// C: light color
// materialSpecularness
void getSpecular(in vec3 L, in vec3 E, in vec3 N,
        in vec3 C, in float materialSpecularness, out vec3 specular) {
    if (materialSpecularness == 0) {
        // no specular
        specular = vec3(0);
        return;
    }
    vec3 halfVec = normalize(L + E);
    float NdotH = max(0, dot(N, halfVec));
    specular = C * pow(NdotH, materialSpecularness);
}

