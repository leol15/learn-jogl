#version 150 core

in vec3 position;
in vec3 normal;
in vec2 uv;

out vec3 vertexColor;

uniform mat4 model = mat4(1);
uniform mat4 view = mat4(1);
uniform mat4 projection = mat4(1);

void main() {
    vertexColor = vec3(0.7, 0.7, 0.7);
    // vertexColor = vec3(0, 0, position.y);
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
}