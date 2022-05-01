#version 150 core

in vec3 position;

uniform vec4 color = vec4(1);
uniform mat4 UItoGL;

out vec4 vertexColor;

void main() {
    vertexColor = color;
    // gl_Position = vec4(position, 1.0);
    gl_Position = UItoGL * vec4(position, 1.0);
}
