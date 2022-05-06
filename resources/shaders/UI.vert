#version 150 core

in vec2 position;

uniform vec4 color = vec4(1);
uniform mat4 UItoGL = mat4(1);

out vec4 vertexColor;

void main() {
    vertexColor = color;
    // gl_Position = vec4(position, 1.0);
    gl_Position = UItoGL * vec4(position, 0, 1.0);
}
