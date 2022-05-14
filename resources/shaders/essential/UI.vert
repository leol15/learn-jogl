#version 150 core

in vec2 position;

out vec4 vertexColor;

uniform vec4 color = vec4(1);
uniform mat4 UItoGL = mat4(1);

void main() {
    vertexColor = color;
    gl_Position = UItoGL * vec4(position, 0, 1.0);
}
