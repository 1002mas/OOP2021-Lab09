package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {
    private final int threadsNumber;

    /*
     * @param n Number of threads
     * @throws IllegalArgumentException
     * */
    public MultiThreadedSumMatrix(final int n) {
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        threadsNumber = n;
    }

    @Override
    public double sum(final double[][] matrix) {

        final int interval = matrix.length % this.threadsNumber + matrix.length / this.threadsNumber;
        final List<Worker> threads = new ArrayList<>();
        for (int i = 0; i < matrix.length; i += interval) {
            final int end = i + interval > matrix.length ? matrix.length : i + interval;
            threads.add(new Worker(matrix, i, end));
        }
        for (final Worker w : threads) {
            w.start();
        }
        double sum = 0;

        for (final Worker w : threads) {
            try {
                w.join();
                sum += w.getSum();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(sum);
        return sum;
    }

    private class Worker extends Thread {
        private final double[][] matrix;
        private final int begin;
        private final int end;
        private double sum;

        public Worker(final double[][] matrix, final int begin, final int end) {
            this.matrix = matrix.clone();
            this.begin = begin;
            this.end = end;
        }

        @Override
        public void run() {
            for (int r = this.begin; r < this.end; r++) {
                for (int c = 0; c < this.matrix[0].length; c++) {
                    sum += matrix[r][c];
                }
            }
        }

        private double getSum() {
            return sum;
        }

    }
}
