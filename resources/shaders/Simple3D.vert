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
uniform vec4 materialColor = vec4(0.7, 0.7, 0.7, 1);
uniform mat4 model = mat4(1);

layout (std140) uniform CAMERA_INFO
{
    mat4 view;
    mat4 projection;
    vec3 eyePos;
    vec3 ambientColor;
};


void main() {
    vs_out.debug = debug;
    vs_out.color = materialColor;
    // geom shader
    gl_Position = view * model * vec4(position, 1.0);
    mat3 normalMatrix = mat3(transpose(inverse(view * model)));
    vs_out.normal = normalize(vec4(normalMatrix * normal, 1.0).xyz);

}