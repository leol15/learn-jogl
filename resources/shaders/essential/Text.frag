#version 450 core

in vec2 textureCoord;
in vec4 colorFromVert;

uniform sampler2D texImage;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture(texImage, textureCoord);
    fragColor = colorFromVert * textureColor;
}