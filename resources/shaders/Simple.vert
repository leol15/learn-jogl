#version 450 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 color;

out vec3 vertexColor;

uniform mat4 model = mat4(1);

layout (std140) uniform ViewAndProjection
{
    mat4 view;
    mat4 projection;
    vec3 eyePos;
};

void main() {
    vertexColor = color;
    // vertexColor = vec3(0, 0, position.y);
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
}