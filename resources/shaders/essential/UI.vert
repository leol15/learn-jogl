#version 150 core

in vec2 position;

out vec4 in_fragColor;

uniform vec4 materialColor = vec4(1);
uniform mat4 UItoGL = mat4(1);

void main() {
    in_fragColor = materialColor;
    gl_Position = UItoGL * vec4(position, 0, 1.0);
}
