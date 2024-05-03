package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// X = SORT(MC*M+D-C)
public class F2 {

    private Matrix MC;
    private Vector C;
    private Vector D;
    private Vector M;

    private int numThreads = 8;


    public F2(Matrix MC, Vector c, Vector d, Vector m) {
        this.MC = MC;
        C = c;
        D = d;
        M = m;
    }
    public Vector calculateParallel(){
        Vector multiplyMCbyM = new Vector(MC.getRows());
        Vector sumD  = new Vector(D.getSize());
        Vector minusC  = new Vector(C.getSize());
        Vector X = new Vector(C.getSize());
        calculateMultiplyMCbyM(multiplyMCbyM);
        calculateSumD(multiplyMCbyM,sumD);
        calculateMinusC(minusC, sumD);
        sortVector(minusC, X);
        return X;
    }

    public Vector calculateSequential(){
        Vector multiplyMCbyM = new Vector(MC.getRows());

        // MC * M
        Service.multiplyMatrixByVector(0, MC, M, 1, multiplyMCbyM);

        Vector sumD = new Vector(D.getSize());
        // + D
        Service.vectorSum(0, D.getSize(), multiplyMCbyM, D, sumD);

        Vector minusC = new Vector(C.getSize());
        // - C
        Service.vectorSubstraction(0, C.getSize(), sumD, C, minusC);

        // SORT
        List<Double> unsortedList = Arrays.asList(minusC.getVector());
        Service.sort(unsortedList, 0, minusC.getVector().length);
        Vector X = new Vector(C.getSize());
        X.setVector(unsortedList);
        return X;
    }

    private void calculateMultiplyMCbyM(Vector multiplyMCbyM){
        Thread thread = new Thread(()->{
            synchronized (multiplyMCbyM){
                Thread[] threads = new Thread[numThreads];
                for(int i=0; i<numThreads; i++){
                    int startIndex = i;
                    threads[i] = new Thread(() -> {
                        Service.multiplyMatrixByVector(startIndex, MC, M, numThreads, multiplyMCbyM);
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
        });
        thread.start();

    }
    private void calculateSumD(Vector multiplyMCbyM, Vector sumD){
        Thread thread = new Thread(()->{
            synchronized (sumD){
                synchronized (multiplyMCbyM){
                    Thread[] threads = new Thread[numThreads];
                    for (int i = 0; i < numThreads; i++) {
                        int startIndex = i * D.getSize() / numThreads;
                        int endIndex;
                        if(i==numThreads-1) endIndex = D.getSize();
                        else {
                            endIndex = (i + 1) * D.getSize() / numThreads;
                        }
                        threads[i] = new Thread(() -> {
                            Service.vectorSum(startIndex, endIndex, multiplyMCbyM, D, sumD);
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
        });
        thread.start();

    }

    private void calculateMinusC(Vector minusC, Vector sumD){
        Thread thread = new Thread(()->{
            synchronized (minusC){
            synchronized (sumD){
                Thread[] threads = new Thread[numThreads];
                for (int i = 0; i < numThreads; i++) {
                    int startIndex = i * C.getSize() / numThreads;
                    int endIndex;
                    if(i==numThreads-1) endIndex = C.getSize();
                    else {
                        endIndex = (i + 1) * C.getSize() / numThreads;
                    }
                    threads[i] = new Thread(() -> {
                        Service.vectorSubstraction(startIndex, endIndex, sumD, C, minusC);
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
        }});
        thread.start();
    }

    private void sortVector(Vector minusC, Vector sortResult){
        Thread thread = new Thread(()->{
            synchronized (minusC){
                Double min = Service.findMin(minusC.getVector());
                Double max = Service.findMax(minusC.getVector());
                sortResult.setVector(parallelSortTask(Arrays.asList(minusC.getVector()), min, max+1, 4));
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private List<Double> parallelSortTask(List<Double> list, Double min, Double max, int depth){
        if (list.size()<=50 || depth == 0) {
            Service.sort(list, 0, list.size());
            return list;
        }
        final double mid = min + (max - min) / 2;
        List<Double> leftList = new ArrayList<>();
        List<Double> rightList = new ArrayList<>();
        Runnable leftTask = new Runnable() {
            @Override
            public void run() {
                leftList.addAll(parallelSortTask(getSublist(list, min, mid), min, mid , depth - 1));
            }
        };
        Runnable rightTask = new Runnable() {
            @Override
            public void run() {
                rightList.addAll(parallelSortTask(getSublist(list, mid, max), mid, max , depth - 1));
            }
        };
        Thread leftThread = new Thread(leftTask);
        Thread rightThread = new Thread(rightTask);
        leftThread.start();
        rightThread.start();
        try {
            leftThread.join();
            rightThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Double> newList = new ArrayList<>();
        newList.addAll(leftList);

        newList.addAll(rightList);

        return newList;

    }

    private List<Double> getSublist(List<Double> currentList, Double min, Double max) {
        List<Double> sublist = new ArrayList<>();
        for (Double el : currentList) {
            if (el >= min && el < max) {
                sublist.add(el);
            }
        }
        return sublist;
    }


}
