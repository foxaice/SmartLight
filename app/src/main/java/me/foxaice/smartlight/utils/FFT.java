package me.foxaice.smartlight.utils;

import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

final class FFT {
    private FFT() {
    }

    static double[] getFFTArray(double[] buffer) {
        double[] data = new double[CommonUtils.nextPowOf2(buffer.length) << 1];
        for (int i = 0; i < buffer.length; i++) {
            data[i << 1] = buffer[i];
            data[(i << 1) + 1] = 0;
        }

        //binary inversion
        for (int i = 0, j = 0, size = data.length; i < size >> 1; i += 2) {
            if (j > i) {
                //swap real part
                double temp = data[i];
                data[i] = data[j];
                data[j] = temp;
                //swap imaginary part
                temp = data[i + 1];
                data[i + 1] = data[j + 1];
                data[j + 1] = temp;

                //checks if the changes occurs in the first half
                //and use the mirrored effect on the second half
                if ((j >> 1) < (size >> 2)) {
                    //swap real part
                    temp = data[size - (i + 2)];
                    data[size - (i + 2)] = data[size - (j + 2)];
                    data[size - (j + 2)] = temp;
                    //swap imaginary part
                    temp = data[size - (i + 2) + 1];
                    data[size - (i + 2) + 1] = data[size - (j + 2) + 1];
                    data[size - (j + 2) + 1] = temp;
                }
            }
            int m = size >> 1;
            while (m >= 2 && j >= m) {
                j -= m;
                m >>= 1;
            }
            j += m;
        }

        //the Danielson - Lanczos section of the routine
        for (int N = 2; N < data.length; N <<= 1) {
            double theta = -2.0 * PI / N;
            double tempW = sin(0.5 * theta);
            double wPRe = -2.0 * pow(tempW, 2);
            double wPIm = sin(theta);
            double wRe = 1.0;
            double wIm = 0.0;

            for (int m = 1; m < N; m += 2) {
                for (int i = m; i <= data.length; i += N << 1) {
                    int j = i + N;
                    double tempRe = wRe * data[j - 1] - wIm * data[j];
                    double tempIm = wRe * data[j] + wIm * data[j - 1];
                    data[j - 1] = data[i - 1] - tempRe;
                    data[j] = data[i] - tempIm;
                    data[i - 1] += tempRe;
                    data[i] += tempIm;
                }
                wRe = (tempW = wRe) * wPRe - wIm * wPIm + wRe;
                wIm = wIm * wPRe + tempW * wPIm + wIm;
            }
        }
        return data;
    }
}
