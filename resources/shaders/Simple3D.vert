#version 150 core

in vec3 position;
in vec3 normal;
in vec2 uv;

out VS_OUT {
    vec3 normal;
    vec4 color;
    int isDebug;
} vs_out;

uniform int isDebug = 1;
uniform vec4 color = vec4(0.7, 0.7, 0.7, 1);
uniform mat4 model = mat4(1);
uniform mat4 view = mat4(1);
uniform mat4 projection = mat4(1);

void main() {
    vs_out.isDebug = isDebug;
    vs_out.color = color;
    // geom shader
    gl_Position = view * model * vec4(position, 1.0);
    mat3 normalMatrix = mat3(transpose(inverse(view * model)));
    vs_out.normal = normalize(vec4(normalMatrix * normal, 1.0).xyz);

}