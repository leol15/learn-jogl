#version 450 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 color;

out vec4 in_fragColor;

uniform mat4 model = mat4(1);

layout (std140) uniform CAMERA_INFO
{
    mat4 view;
    mat4 projection;
    vec3 eyePos;
    vec3 ambientColor;
};

void main() {
    in_fragColor = vec4(color, 1);
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
}