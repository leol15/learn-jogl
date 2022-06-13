#version 450 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 uv;

uniform mat4 model = mat4(1);
uniform vec4 materialColor = vec4(0.7, 0.7, 0.7, 1);

// camera
layout (std140) uniform CAMERA_INFO
{
    mat4 view;
    mat4 projection;
    vec3 eyePos;
    vec3 ambientColor;
};

out vec4 in_fragColor;


void main() {
    in_fragColor = materialColor;
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
}