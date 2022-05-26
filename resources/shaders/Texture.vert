#version 450 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 uv;

uniform mat4 model;

layout (std140) uniform CAMERA_INFO
{
    mat4 view;
    mat4 projection;
    vec3 eyePos;
    vec3 ambientColor;
};

out vec2 textureCoord;

void main() {
    textureCoord = uv;
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
    // gl_Position = vec4(0.5, 0.5, 0, 1.0);
    // gl_Position = vec4(position, 1.0);
}
