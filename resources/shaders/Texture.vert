#version 150 core

in vec3 position;
in vec2 texcoord;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

out vec2 textureCoord;

void main() {
    textureCoord = texcoord;
    mat4 mvp = projection * view * model;
    gl_Position = mvp * vec4(position, 1.0);
    // gl_Position = vec4(0.5, 0.5, 0, 1.0);
    // gl_Position = vec4(position, 1.0);
}
