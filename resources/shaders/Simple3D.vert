#version 450 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 uv;

out VS_OUT {
    vec3 normal;
    vec4 color;
    int debug;
} vs_out;

uniform int debug = 1;
uniform vec4 color = vec4(0.7, 0.7, 0.7, 1);
uniform mat4 model = mat4(1);
// uniform mat4 view = mat4(1);
// uniform mat4 projection = mat4(1);

layout (std140, binding = 0) uniform ViewAndProjection
{
    mat4 view;
    mat4 projection;
};


void main() {
    vs_out.debug = debug;
    vs_out.color = color;
    // geom shader
    gl_Position = view * model * vec4(position, 1.0);
    mat3 normalMatrix = mat3(transpose(inverse(view * model)));
    vs_out.normal = normalize(vec4(normalMatrix * normal, 1.0).xyz);

}