#version 450 core

in vec2 textureCoord;

uniform sampler2D texImage;
uniform vec4 materialColor = vec4(1);

out vec4 fragColor;

void main() {
    vec4 textureColor = texture(texImage, textureCoord);
    fragColor = textureColor * materialColor;
    // fragColor = vec4(textureColor.a, 0, 0, 0.5);
    // fragColor = vec4(1, 1, 1, 1);
}