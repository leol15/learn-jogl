#version 150 core

in vec3 position;
in vec3 normal;
in vec2 uv;

out vec3 vertexColor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    vertexColor = vec3(0.8, 0.8, 0.8);
    // vertexColor = vec3(0, 0, position.y);
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
}