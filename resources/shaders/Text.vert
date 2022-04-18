#version 150 core

in vec3 position;
in vec2 texcoord;

uniform mat4 UItoGL;
uniform vec4 color;

out vec2 textureCoord;
out vec4 colorFromVert;

void main() {
    gl_Position = UItoGL * vec4(position, 1.0);
    colorFromVert = color;
    textureCoord = texcoord;
}
