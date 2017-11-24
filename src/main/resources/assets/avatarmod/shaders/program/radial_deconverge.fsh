#version 120

uniform sampler2D DiffuseSampler;
varying vec2 texCoord;

// How much to move each component(r,g,b) in the polar space
// In percentage of the screen, for example, 0.1 means 10% of screen
uniform vec3 CoordModifyX;
uniform vec3 CoordModifyY;

vec2 toPolar(vec2 inputt) {
    vec2 cartesian = (inputt - vec2(0.5, 0.5));
    float angle = (atan(cartesian.y, cartesian.x));
    float dist = sqrt(cartesian.y * cartesian.y + cartesian.x * cartesian.x);
    return (vec2(dist, angle));
}

vec2 toRectangular(vec2 inputt) {
    float angle = inputt.y;
    float dist = inputt.x;
    return vec2(dist * cos(angle), dist * sin(angle)) + vec2(0.5, 0.5);
}

void main() {

    vec4 finalColor = vec4(0.0, 0.0, 0.0, 1.0);

    for (int i = 0; i < 3; i++) {
        vec2 offset = vec2(CoordModifyX[i], CoordModifyY[i]);
        vec2 modifiedCoord = toRectangular(toPolar(texCoord) + offset);
        finalColor[i] = texture2D(DiffuseSampler, modifiedCoord)[i];
    }

    gl_FragColor = finalColor;

}
