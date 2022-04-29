#version 150 core

in vec3 vertexColor;

out vec4 fragColor;

void main() {
    fragColor = vec4(vertexColor, 1.0);
    // fragColor = vec4(1.0, 1.0, 1.0, 1.0);
    // fragColor = vec4(vec3(gl_FragCoord.z), 1.0);
}
