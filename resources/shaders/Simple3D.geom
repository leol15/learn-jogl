#version 450 core

layout (triangles) in;
// 3 "lines", 1 triangle
layout (triangle_strip, max_vertices = 12) out;

in VS_OUT {
    vec3 normal;
    vec4 color;
    int debug;
} gs_in[];

out vec4 fs_color;

uniform mat4 projection = mat4(1);

const float MAGNITUDE = 0.1;
void GenerateLine(int index) {
    fs_color = vec4(1, 1, 0, 0.7);
    gl_Position = projection * gl_in[index].gl_Position;
    EmitVertex();
    gl_Position = projection * (gl_in[index].gl_Position + 
                                vec4(gs_in[index].normal, 0.0) * MAGNITUDE);
    EmitVertex();
    EmitVertex();
    EndPrimitive();
}

void main() {
    if (gs_in[0].debug == 1) {
        GenerateLine(0); // first vertex normal
        GenerateLine(1); // second vertex normal
        GenerateLine(2); // third vertex normal
    }

    fs_color = gs_in[0].color;
    gl_Position = projection * gl_in[0].gl_Position;
    EmitVertex();
    gl_Position = projection * gl_in[1].gl_Position;
    EmitVertex();
    gl_Position = projection * gl_in[2].gl_Position;
    EmitVertex();
    EndPrimitive();
    
}  