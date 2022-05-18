#version 450 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 uv;

uniform mat4 model = mat4(1);
uniform vec4 color = vec4(1);

// camera
layout (std140) uniform ViewAndProjection
{
    mat4 view;
    mat4 projection;
    vec3 eyePos;
};

out vec3 surfacePos;
out vec3 surfaceNormal;
out vec4 surfaceColor;

void main() {
    surfacePos = (model * vec4(position, 1)).xyz;
    surfaceNormal = normalize((model * vec4(normal, 0)).xyz);
    surfaceColor = color;
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
}