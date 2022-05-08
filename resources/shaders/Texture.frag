#version 450 core

in vec2 textureCoord;

uniform sampler2D texImage;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture(texImage, textureCoord);
    fragColor = textureColor;
    // fragColor = vec4(1, 1, 1, 1);
}