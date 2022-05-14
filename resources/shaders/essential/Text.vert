#version 450 core

layout(location = 0) in vec3 position;
layout(location = 2) in vec2 texcoord;

uniform mat4 UItoGL;
uniform vec4 color;

out vec2 textureCoord;
out vec4 colorFromVert;

void main() {
    gl_Position = UItoGL * vec4(position, 1.0);
    colorFromVert = color;
    textureCoord = texcoord;
}
