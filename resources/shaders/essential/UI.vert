#version 450 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 uv;

uniform mat4 model = mat4(1);
uniform vec4 materialColor = vec4(1);
// extra
uniform mat4 UItoGL = mat4(1);

out vec4 in_fragColor;

void main() {
    in_fragColor = materialColor;
    gl_Position = UItoGL * model * vec4(position, 1.0);
}