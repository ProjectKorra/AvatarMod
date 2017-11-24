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

    mat3 kernelRegular;
    kernelRegular[0][0] = 0.0;
    kernelRegular[0][1] = 0.0;
    kernelRegular[0][2] = 0.0;
    kernelRegular[1][0] = 0.0;
    kernelRegular[1][1] = 1.0;
    kernelRegular[1][2] = 0.0;
    kernelRegular[2][0] = 0.0;
    kernelRegular[2][1] = 0.0;
    kernelRegular[2][2] = 0.0;

    mat3 kernelHoriz;
    kernelHoriz[0][0] = 0.0;
    kernelHoriz[0][1] = 0.3;
    kernelHoriz[0][2] = 0.0;
    kernelHoriz[1][0] = 0.0;
    kernelHoriz[1][1] = 0.4;
    kernelHoriz[1][2] = 0.0;
    kernelHoriz[2][0] = 0.0;
    kernelHoriz[2][1] = 0.3;
    kernelHoriz[2][2] = 0.0;

    mat3 kernel = kernelHoriz;
// 	mat3 kernel = kernelRegular;

       vec4 sum = vec4(0, 0, 0, 1);
    vec2 uv = texCoord;

    for (int i = 0; i < 3; i++) {
        for (int j = 0; j < 3; j++) {
/*
            vec2 offset = vec2(i - 1, j - 1);
            float pixelWeight = kernel[i][j];

            vec2 newCoord = fixCoordinate(toPolar(uv) + offset*3.0);
            vec4 blurredPixel = texture2D(DiffuseSampler, newCoord);

            sum += pixelWeight * blurredPixel;
*/

            vec2 offsetCoordinate = vec2(i - 1, j - 1) * 0.01;
            float pixelWeight = kernel[i - 1][j - 1];
            vec2 newCoord = toPolar(uv) + offsetCoordinate;
            vec4 blurredPixel = texture2D(DiffuseSampler, fixCoordinate(toRectangular(newCoord)));
            vec4 unblurredPixel = texture2D(DiffuseSampler, uv);

            float blurWeight = toPolar(uv).x * 2.0;
            if (blurWeight > 1) blurWeight = 1;

            // Colorize blurredPixel
            // multiply each component of blurredPixel by each component of ColorMult
            blurredPixel *= vec4(ColorMult, 1.0);

            vec4 weightedBlur = blurredPixel * blurWeight + unblurredPixel * (1 - blurWeight);
            sum += pixelWeight * weightedBlur;

        }
    }

gl_FragColor = sum;
//gl_FragColor = texture2D(DiffuseSampler, toRectangular(toPolar(texCoord)));

//    gl_FragColor = vec4(texture2D(DiffuseSampler, texCoord).r, 0.0, 0.0, 1.0);
    /*vec4 blurred = vec4(0.0);
    float totalStrength = 0.0;
    float totalAlpha = 0.0;
    float totalSamples = 0.0;
    for(float r = -Radius; r <= Radius; r += 1.0) {
        vec4 sample = texture2D(DiffuseSampler, texCoord + oneTexel * r * BlurDir);

		// Accumulate average alpha
        totalAlpha = totalAlpha + sample.a;
        totalSamples = totalSamples + 1.0;

		// Accumulate smoothed blur
        float strength = 1.0 - abs(r / Radius);
        totalStrength = totalStrength + strength;
        blurred = blurred + sample;
    }
    gl_FragColor = vec4(blurred.rgb / (Radius * 2.0 + 1.0), totalAlpha);*/
}
