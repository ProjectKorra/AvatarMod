#version 110

uniform sampler2D DiffuseSampler;
varying vec2 texCoord;

uniform float Time;

float rand(vec2 n) {
	return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float noise(vec2 p){
	vec2 ip = floor(p);
	vec2 u = fract(p);
	u = u*u*(3.0-2.0*u);

	float res = mix(
		mix(rand(ip),rand(ip+vec2(1.0,0.0)),u.x),
		mix(rand(ip+vec2(0.0,1.0)),rand(ip+vec2(1.0,1.0)),u.x),u.y);
	return res*res;
}

vec2 toPolar(vec2 inputt) {
    vec2 cartesian = inputt - vec2(0.5, 0.5);
    float angle = atan(cartesian.y, cartesian.x);
    float dist = sqrt(cartesian.y * cartesian.y + cartesian.x * cartesian.x);
    return vec2(dist, angle);
}

vec2 toRectangular(vec2 inputt) {
    float angle = inputt.y;
    float dist = inputt.x;
    return vec2(dist * cos(angle), dist * sin(angle)) + vec2(0.5, 0.5);
}

void main() {
    vec2 polar = toPolar(texCoord);
    polar.x = polar.x * (1.0 + 0.2 * polar.x*polar.x);
    polar.x *= 0.9;
	gl_FragColor = texture2D(DiffuseSampler, toRectangular(polar));

//	float noise = noise(texCoord * 100.0 + vec2(Time * 1000.0, 0.0));
//	gl_FragColor += vec4(noise, noise, noise, 1.0) * 0.02;

//	fragColor = texture(iChannel0, uv);
}