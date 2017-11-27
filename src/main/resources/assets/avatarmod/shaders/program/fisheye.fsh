#version 120

uniform sampler2D DiffuseSampler;
varying vec2 texCoord;

uniform float Amount = 0.2;
uniform float Zoom = 0.1;

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
    // https://stackoverflow.com/questions/6199636/formulas-for-barrel-pincushion-distortion
    vec2 polar = toPolar(texCoord);
    polar.x = polar.x * (1.0 + Amount * polar.x*polar.x);
    polar.x *= (1 - Zoom);
	gl_FragColor = texture2D(DiffuseSampler, toRectangular(polar));
}