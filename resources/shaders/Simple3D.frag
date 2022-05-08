#version 450 core

in vec4 fs_color;

out vec4 fragColor;

void main() {
    fragColor = fs_color;
    // fragColor = vec4(0.7, 0.7, 0.7, 1.0);
}
