#version 450 core

out vec4 fragColor;


struct point_light {
    vec3 position;
    vec3 intensity;
    vec3 attenuation;
};

layout (std140) uniform ALL_THE_LIGHTS
{
    point_light PL[1];
};

uniform vec3 eye = vec3(0);
uniform point_light point_lights[3];

void main() {
    fragColor = vec4(1, 1, 1, 1.0);
}
