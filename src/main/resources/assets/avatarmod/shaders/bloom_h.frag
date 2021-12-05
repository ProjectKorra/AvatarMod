#version 120

const float[] gauss = float[](0.197448, 0.174697, 0.120999, 0.065602, 0.02784, 0.009246, 0.002403, 0.000489);

uniform sampler2D texture;
uniform float frag_width;

varying vec2 tex_coord;

void main(){
	vec4 final_color = vec4(0.0);
	for(int i = 0; i < gauss.length()*2-1; i ++){
		int place = i-gauss.length()+1;
		final_color += gauss[int(abs(float(place)))]*texture2D(texture, tex_coord + vec2(frag_width*place, 0));
	}
	gl_FragColor = final_color;
}