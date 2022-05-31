#version 450 core

in vec2 textureCoord;
in vec4 in_fragColor;

uniform sampler2D texImage;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture(texImage, textureCoord);
    if (textureColor.a == 0) {
        discard;
    }
    fragColor = in_fragColor * textureColor;
}