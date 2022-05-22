#version 450 core

// in vec3 position;
layout(location = 0) in vec3 position;

out vec4 vertexColor;

uniform mat4 model = mat4(1);
uniform vec4 materialColor = vec4(1, 1, 0, 1);

layout (std140) uniform CAMERA_INFO
{
    mat4 view;
    mat4 projection;
    vec3 eyePos;
    vec3 ambientColor;
};



void main() {
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
    vertexColor = materialColor;
}