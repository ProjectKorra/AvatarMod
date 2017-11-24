#version 330

uniform sampler2D DiffuseSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 BlurDir;

// Multiplier for each RGB value of the colors
uniform vec3 ColorMult = vec3(1.0, 1.0, 1.0);

// Clamps the x/y values of given coordinate between 0 and 1
// shadertoy is fine with values outside of this range, but for some reason
// this causes huge issues within minecraft
vec2 fixCoordinate(vec2 coord) {
    while (coord.x > 1) coord.x -= 1;
    while (coord.y > 1) coord.y -= 1;
    while (coord.x < 0) coord.x += 1;
    while (coord.y < 0) coord.y += 1;
    return coord;
}

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

    // Convolution kernel used for blurring after has everything been converted to polar coordinates
    // Usage: kernel[x][y]
    mat3 kernel;
    kernel[0][0] = 0.0;
    kernel[0][1] = 0.3;
    kernel[0][2] = 0.0;
    kernel[1][0] = 0.0;
    kernel[1][1] = 0.4;
    kernel[1][2] = 0.0;
    kernel[2][0] = 0.0;
    kernel[2][1] = 0.3;
    kernel[2][2] = 0.0;

    // Blurring is performed by (sortof) averaging pixels value + all pixels around it.
    // Convolution kernel dictates the "weight" of each pixel depending on its position relative
    // to the pixel - for example, top right means kernel[2][1] which is 0; topright no influence
    vec4 sum = vec4(0, 0, 0, 1);

    // Calculate the sum based on all nearby pixels
    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {

            vec2 offsetCoordinate = vec2(i - 1, j - 1) * 0.01;
            float pixelWeight = kernel[i - 1][j - 1];

            vec2 newCoord = toRectangular(toPolar(texCoord) + offsetCoordinate);
            vec4 blurredPixel = texture2D(DiffuseSampler, fixCoordinate(newCoord));
            vec4 unblurredPixel = texture2D(DiffuseSampler, texCoord);

            // Most pixels are a mixed of radial blur and regular
            // More blurWeight means it is more radial blurred
            float blurWeight = toPolar(texCoord).x * 2.0;
            if (blurWeight > 1) blurWeight = 1;

            // Colorize blurredPixel
            // multiply each component of blurredPixel by each component of ColorMult
            blurredPixel *= vec4(ColorMult, 1.0);

            // Perform weighted average and add this nearby pixel's value to sum to be averaged
            vec4 weightedBlur = blurredPixel * blurWeight + unblurredPixel * (1 - blurWeight);
            sum += pixelWeight * weightedBlur;

        }
    }

    gl_FragColor = sum;

}
