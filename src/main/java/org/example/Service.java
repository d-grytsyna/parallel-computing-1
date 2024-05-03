package org.example;


import java.util.List;

public class Service {
    private static Double multiplyRowByColumn(Double[] row, Double[] column){
        Double result = 0d;
        for(int i=0; i<row.length; i++){
            result += row[i] * column[i];
        }
        return result;
    }

    private static Double multiplyRowByColumnKahan(Double[] row, Double[] column){
        double result = 0.0;
        double correction = 0.0;
        for(int i = 0; i < row.length; i++){
            double product = row[i] * column[i];
            double y = product -  correction;
            double temp = result + y;
            correction = (temp - result) - y;
            result = temp;
        }
        return result;
    }
    public static void vectorSubstraction(int elementIndexFrom, int elementIndexTo, Vector v1, Vector v2, Vector result){
        for(int i=elementIndexFrom; i<elementIndexTo; i++){
            Double resultEl =  v1.getElement(i) - v2.getElement(i);
            result.setElement(i, resultEl);
        }
    }

    public static void vectorSum(int elementIndexFrom, int elementIndexTo, Vector v1, Vector v2, Vector result){
        for(int i=elementIndexFrom; i<elementIndexTo; i++){
            result.setElement(i, v1.getElement(i) + v2.getElement(i));
        }
    }
    public static void multiplyMatrixByNum (int rowIndex, Matrix m1, Double num, Matrix resultMatrix, int numThreads){
        for(int j=rowIndex*(m1.getRows()/numThreads); j<(rowIndex+1)*(m1.getRows()/numThreads); j++){
            int startIndex = j;
            for(int k=startIndex, counter = 0; counter<m1.getCols(); counter++ ){
                Double resultElement = m1.getElement(j, k) * num;
                resultMatrix.setElement(startIndex, k, resultElement);
                k--;
                if(k<0) k = m1.getCols()-1;
            }
        }
    }
    public static Double minValue(Double[] elements){
        Double min = elements[0];
        for(int i=1; i<elements.length; i++) {
            if (elements[i] < min) min = elements[i];
        }
        return min;
    }

    public static void matrixAddition(int rowIndex, Matrix m1, Matrix m2, int numThreads, Matrix resultMatrix){
        for(int i=rowIndex*(m1.getRows()/numThreads); i < (rowIndex+1)*(m1.getRows()/numThreads); i++){
            for(int j=0; j<m1.getCols(); j++){
                Double result = m1.getElement(i, j) + m2.getElement(i, j);
                resultMatrix.setElement(i, j, result);
            }
        }
    }
    public static void multiplyMatrix(int rowIndex, Matrix m1, Matrix m2, int numThreads, Matrix resultMatrix){
        for(int j=rowIndex*(m1.getRows()/numThreads); j<(rowIndex+1)*(m1.getRows()/numThreads); j++){
            int startIndex = j;
            Double[] matrixRow = m1.getRow(startIndex);
            for(int k=startIndex, counter = 0; counter<m2.getCols(); counter++ ){
                Double[] matrixCol = m2.getCol(k);
                Double resultElement = multiplyRowByColumnKahan(matrixRow, matrixCol);
                resultMatrix.setElement(startIndex, k, resultElement);
                k--;
                if(k<0) k = m2.getCols()-1;
            }
        }
    }
    public static void multiplyMatrixByVector(int rowIndex, Matrix m1, Vector v2, int numThreads, Vector resultVector){
        for(int j=rowIndex*(m1.getRows()/numThreads); j<(rowIndex+1)*(m1.getRows()/numThreads); j++){
            int startIndex = j;
            Double[] matrixRow = m1.getRow(startIndex);
            Double resultElement = multiplyRowByColumnKahan(matrixRow, v2.getVector());
            resultVector.setElement(startIndex, resultElement);
        }
    }

    public static boolean matrixEqual(Matrix m1, Matrix m2) {
        for (int i = 0; i < m1.getRows(); i++) {
            for (int j = 0; j < m1.getCols(); j++) {
                boolean equal = areEqualWithPrecision(m1.getElement(i, j), m2.getElement(i, j), 6);
                if (!equal) return false;
            }
        }
        return true;
    }

    public static boolean vectorEqual(Vector v1, Vector v2){
        for(int i=0; i<v1.getSize(); i++){
            boolean equal = areEqualWithPrecision(v1.getElement(i), v2.getElement(i), 6);
            if (!equal) return false;
        }
        return true;
    }
    public static boolean areEqualWithPrecision(double num1, double num2, int precision) {
        double epsilon = Math.pow(10,-precision);
        return Math.abs(num1 - num2) < epsilon;
    }

    public static void sort(List<Double> list, int min, int max) {
        if (list == null || list.isEmpty() || min >= max) {
            return;
        }
        for (int i = min; i < max - 1; i++) {
            for (int j = min; j < max - i - 1; j++) {
                if (list.get(j) > list.get(j + 1)) {
                    double temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }
    }
    public static Double findMin(Double[] array) {
        Double min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }
    public static Double findMax(Double[] array) {
        Double max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }
}
