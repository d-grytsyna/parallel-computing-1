package org.example;

// Варіант 28
// MF=min(C-D)*MC*MZ+MM*(MC+MM);
// X = SORT(MC*M+D-C)
public class Main {
    public static void main(String[] args) {
        int size = 1000;
        Matrix MC = new Matrix(size, size);
        Matrix MZ = new Matrix(size, size);
        Matrix MM = new Matrix(size, size);
        Vector C = new Vector(size);
        Vector D = new Vector(size);
        Vector M = new Vector(size);

//            MC.generateMatrix();
//            MC.writeToFile("MC");
        MC.readFromFile("MC");

//            MZ.generateMatrix();
//            MZ.writeToFile("MZ");
        MZ.readFromFile("MZ");

//            MM.generateMatrix();
//            MM.writeToFile("MM");
        MM.readFromFile("MM");

//            C.generateVector();
//            C.writeToFile("C");
        C.readFromFile("C");

//            D.generateVector();
//            D.writeToFile("D");
        D.readFromFile("D");


//            M.generateVector();
//            M.writeToFile("M");
        M.readFromFile("M");


        F1 f1 = new F1(MC, MZ, MM, C, D);
        F2 f2 = new F2(MC, C, D, M);


        // Parallel computing
        Thread parallelTest = new Thread(() -> {

            Matrix MF = f1.calculateMatrixParallel();
            System.out.println("First 100 elements in MF from parallel computing: ");
            MF.printMatrix(100);


            Vector X = f2.calculateParallel();
            System.out.println("First 100 elements in X from parallel computing : ");
            X.printVector(100);


        });
        parallelTest.start();
        long startTime = System.currentTimeMillis();
        try {
            parallelTest.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Parallel computing elapsed time: " + elapsedTime);


        // Sequential computing
        long startTimeSequential = System.currentTimeMillis();
        Matrix MF = f1.calculateMatrixSequential();
        System.out.println("First 100 elements in MF from sequential computing: ");
        MF.printMatrix(100);

        Vector X = f2.calculateSequential();
        System.out.println("First 100 elements in X from sequential computing: ");
        X.printVector(100);
        long endTimeSequential = System.currentTimeMillis();
        long elapsedTimeSequential = endTimeSequential - startTimeSequential;
        System.out.println("Sequential computing elapsed time: " + elapsedTimeSequential);

//            resultMatrix2.printMatrix();
//            boolean equal = Service.matrixEqual(resultMatrix, resultMatrix2);
//            System.out.println("Matrix equal? " + equal);

//            System.out.println("Vectors equal? " + Service.vectorEqual(X, X2));


        //Test equal
        Matrix parallelMatrix = f1.calculateMatrixParallel();
        Matrix sequentialMatrix = f1.calculateMatrixSequential();
        System.out.println("Parallel and sequential calculations for matrix MF in F1 are equal: " + Service.matrixEqual(parallelMatrix, sequentialMatrix));

        Vector parallelVector = f2.calculateParallel();
        Vector sequentialVector = f2.calculateSequential();
        System.out.println("Parallel and sequential calculations for vector X in F2 are equal: " + Service.vectorEqual(parallelVector, sequentialVector));



    }


}