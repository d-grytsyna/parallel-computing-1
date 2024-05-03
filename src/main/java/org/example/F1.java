package org.example;

// MF=min(C-D)*MC*MZ+MM*(MC+MM);
public class F1 {

    private Matrix MC;
    private Matrix MZ;
    private Matrix MM;
    private Vector C;
    private Vector D;
    private int numThreads = 8;

    public F1(Matrix MC, Matrix MZ, Matrix MM, Vector c, Vector d) {
        this.MC = MC;
        this.MZ = MZ;
        this.MM = MM;
        C = c;
        D = d;
    }
    public Matrix calculateMatrixParallel(){
        Matrix MF = new Matrix(MM.getRows(), MM.getCols());
        Double[] resultMin = new Double[1];
        resultMin[0] = Double.MIN_VALUE;
        Matrix resultMultiplyMCMZ = new Matrix(MM.getRows(), MM.getCols());
        Matrix resultMultiplyMinMatrix = new Matrix(MM.getRows(), MZ.getCols());
        Matrix resultSumMCMM = new Matrix(MC.getRows(), MC.getCols());
        Matrix resultMultiplyMM = new Matrix(MM.getRows(), resultSumMCMM.getCols());

        // min(C-D)
        calculateMin(resultMin);

        // MC * MZ
        calculateMultiplyMCMZ(resultMultiplyMCMZ);

        // * min
        calculateMultiplyMinAndMatrix(resultMultiplyMinMatrix, resultMin, resultMultiplyMCMZ);

        // MC + MM
        calculateSumMCMM(resultSumMCMM);

        // MM * sum
        calculateMultiplyMMbySum(resultMultiplyMM, resultSumMCMM);

        // (*min..) + (MM*sum..)
        calculateFinalSum(resultMultiplyMM, resultMultiplyMinMatrix, MF);
        return MF;

    }
    public Matrix calculateMatrixSequential(){
        Matrix MF = new Matrix(MM.getRows(), MM.getCols());
        Vector CD = new Vector(C.getSize());
        // Vector C - Vector D
        Service.vectorSubstraction(0, C.getSize(), C, D, CD);

        // min(C-D)
        Double minResult = Service.minValue(CD.getVector());

        // min * MC
        Matrix multiplyMinMC = new Matrix(MC.getRows(), MC.getCols());
        Service.multiplyMatrixByNum(0, MC, minResult, multiplyMinMC, 1);

        // minMC * MZ
        Matrix subResult1 = new Matrix(MC.getRows(), MC.getCols());
        Service.multiplyMatrix(0, multiplyMinMC, MZ, 1, subResult1);

        // MC + MM
        Matrix sumMCMM = new Matrix(MC.getRows(), MC.getCols());
        Service.matrixAddition(0, MC, MM, 1, sumMCMM);

        // MM * sumMCMM
        Matrix subResult2 = new Matrix(MC.getRows(), MC.getCols());
        Service.multiplyMatrix(0, MM, sumMCMM, 1, subResult2 );

        // subResult1 + subResult2
        Service.matrixAddition(0, subResult1, subResult2, 1, MF);
        return MF;

    }
    private void calculateMin(Double[] resultMin){
        Thread thread = new Thread(() -> {
            synchronized (resultMin[0]){
                Thread[] threads = new Thread[numThreads];
                Vector resultSubtraction = new Vector(C.getSize());
                for (int i = 0; i < numThreads; i++) {
                    int startIndex = i * C.getSize() / numThreads;
                    int endIndex;
                    if(i==numThreads-1) endIndex = C.getSize();
                    else {
                        endIndex = (i + 1) * C.getSize() / numThreads;
                    }
                    threads[i] = new Thread(() -> {
                        Service.vectorSubstraction(startIndex, endIndex, C, D, resultSubtraction);
                    });
                    threads[i].start();
                }
                for(int i=0; i<numThreads; i++){
                    try {
                        threads[i].join();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                resultMin[0] = Service.minValue(resultSubtraction.getVector());
            }
        });
        thread.start();
    }

    private void calculateMultiplyMCMZ(Matrix resultMultiplyMCMZ){
        new Thread(()->{
            synchronized (resultMultiplyMCMZ){
                Thread[] threads = new Thread[numThreads];
                for (int i = 0; i < numThreads; i++) {
                    int startIndex = i;
                    threads[i] = new Thread(() -> {
                        Service.multiplyMatrix(startIndex, MC, MZ, numThreads, resultMultiplyMCMZ);
                    });
                    threads[i].start();
                }
                for(int i=0; i<numThreads; i++){
                    try {
                        threads[i].join();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void calculateMultiplyMinAndMatrix(Matrix resultMultiplyMinMatrix, Double[] resultMin, Matrix resultMultiplyMCMZ){
        new Thread(()->{
            synchronized (resultMultiplyMinMatrix){
                synchronized (resultMin[0]){
                    synchronized (resultMultiplyMCMZ){
                        Thread[] threads = new Thread[numThreads];
                        for(int i=0; i<numThreads; i++){
                            int startIndex = i;
                            threads[i] = new Thread(() -> {
                                Service.multiplyMatrixByNum(startIndex, resultMultiplyMCMZ, resultMin[0], resultMultiplyMinMatrix, numThreads);
                            });
                            threads[i].start();
                        }
                        for(int i=0; i<numThreads; i++){
                            try {
                                threads[i].join();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }
        }).start();
    }

    private void calculateSumMCMM(Matrix resultSumMCMM){
        new Thread(()->{
        synchronized (resultSumMCMM){
            Thread[] threads = new Thread[numThreads];
            for (int i = 0; i < numThreads; i++) {
                int startIndex = i;
                threads[i] = new Thread(() -> {
                    Service.matrixAddition(startIndex, MC, MM, numThreads, resultSumMCMM);
                });
                threads[i].start();
            }
            for(int i=0; i<numThreads; i++){
                try {
                    threads[i].join();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        }).start();
    }

    private void calculateMultiplyMMbySum(Matrix resultMultiplyMM, Matrix resultSumMCMM){
        new Thread(()-> {
            synchronized (resultMultiplyMM) {
                synchronized (resultSumMCMM) {
                    Thread[] threads = new Thread[numThreads];
                    for (int i = 0; i < numThreads; i++) {
                        int startIndex = i;
                        threads[i] = new Thread(() -> {
                            Service.multiplyMatrix(startIndex, MM, resultSumMCMM, numThreads, resultMultiplyMM);
                        });
                        threads[i].start();
                    }
                    for (int i = 0; i < numThreads; i++) {
                        try {
                            threads[i].join();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    private void calculateFinalSum(Matrix resultMultiplyMM, Matrix resultMultiplyMinMatrix, Matrix MF){
        Thread t1 = new Thread(()->{
                synchronized (resultMultiplyMinMatrix){
                    synchronized (resultMultiplyMM){
                        Thread[] threads = new Thread[numThreads];
                        for (int i = 0; i < numThreads; i++) {
                            int startIndex = i;
                            threads[i] = new Thread(() -> {
                               Service.matrixAddition(startIndex, resultMultiplyMinMatrix, resultMultiplyMM, numThreads, MF);
                            });
                            threads[i].start();
                        }
                        for (int i = 0; i < numThreads; i++) {
                            try {
                                threads[i].join();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
