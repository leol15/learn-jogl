#version 450 core

// in vec3 position;
layout(location = 0) in vec3 position;

out vec4 vertColor;

uniform mat4 model = mat4(1);
uniform mat4 view = mat4(1);
uniform mat4 projection = mat4(1);
uniform vec4 color = vec4(1, 1, 0, 1);

void main() {
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
    vertColor = color;
}