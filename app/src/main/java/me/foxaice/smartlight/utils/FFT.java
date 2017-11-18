package me.foxaice.smartlight.utils;

import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

final class FFT {
    private FFT() {}

    static double[] getFFTArray(double[] buffer) {
        double[] data = FFT.createArrayOfComplexNumbers(buffer);
        FFT.binaryInversion(data);
        FFT.routineDanielsonLanczos(data);
        return data;
    }

    private static double[] createArrayOfComplexNumbers(double[] real) {
        double[] complexData = new double[CommonUtils.nextPowOf2(real.length) << 1];
        for (int i = 0; i < real.length; i++) {
            complexData[i << 1] = real[i];
            complexData[(i << 1) + 1] = 0;
        }
        return complexData;
    }

    private static void binaryInversion(double[] complexes) {
        for (int i = 0, j = 0, size = complexes.length; i < size >> 1; i += 2) {
            if (j > i) {
                //swap real part
                double temp = complexes[i];
                complexes[i] = complexes[j];
                complexes[j] = temp;
                //swap imaginary part
                temp = complexes[i + 1];
                complexes[i + 1] = complexes[j + 1];
                complexes[j + 1] = temp;

                //checks if the changes occurs in the first half
                //and use the mirrored effect on the second half
                if ((j >> 1) < (size >> 2)) {
                    //swap real part
                    temp = complexes[size - (i + 2)];
                    complexes[size - (i + 2)] = complexes[size - (j + 2)];
                    complexes[size - (j + 2)] = temp;
                    //swap imaginary part
                    temp = complexes[size - (i + 2) + 1];
                    complexes[size - (i + 2) + 1] = complexes[size - (j + 2) + 1];
                    complexes[size - (j + 2) + 1] = temp;
                }
            }
            int m = size >> 1;
            while (m >= 2 && j >= m) {
                j -= m;
                m >>= 1;
            }
            j += m;
        }
    }

    private static void routineDanielsonLanczos(double[] complexes) {
        for (int N = 2; N < complexes.length; N <<= 1) {
            double theta = -2.0 * PI / N;
            double tempW = sin(0.5 * theta);
            double wPRe = -2.0 * pow(tempW, 2);
            double wPIm = sin(theta);
            double wRe = 1.0;
            double wIm = 0.0;

            for (int m = 1; m < N; m += 2) {
                for (int i = m; i <= complexes.length; i += N << 1) {
                    int j = i + N;
                    double tempRe = wRe * complexes[j - 1] - wIm * complexes[j];
                    double tempIm = wRe * complexes[j] + wIm * complexes[j - 1];
                    complexes[j - 1] = complexes[i - 1] - tempRe;
                    complexes[j] = complexes[i] - tempIm;
                    complexes[i - 1] += tempRe;
                    complexes[i] += tempIm;
                }
                wRe = (tempW = wRe) * wPRe - wIm * wPIm + wRe;
                wIm = wIm * wPRe + tempW * wPIm + wIm;
            }
        }
    }
}
