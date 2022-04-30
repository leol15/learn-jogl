#version 150 core

in vec3 position;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    // mat4 mvp = projection * view * model;
    // gl_Position = mvp * vec4(position, 1.0);
    gl_Position = vec4(position, 1.0);
}