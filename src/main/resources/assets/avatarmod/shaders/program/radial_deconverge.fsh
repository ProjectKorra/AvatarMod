#version 110

uniform sampler2D DiffuseSampler;
varying vec2 texCoord;

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

    vec3 finalColor = vec3(0.0, 0.0, 0.0);

    vec3 rgbCoordModify = vec3(3.0, 0.0, 0.0);

    for (int i = 0; i < 3; i++) {
        vec2 offset = vec2(0.0, rgbCoordModify[i]);
        float modifiedCoord = toRectangular(toPolar(texCoord) + offset);
        finalColor[i] = texture2D(DiffuseSampler, modifiedCoord)[i];
    }

    gl_FragColor = finalColor;
}