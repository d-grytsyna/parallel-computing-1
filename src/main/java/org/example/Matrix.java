package org.example;

import java.io.*;
import java.util.Random;

public class Matrix {
    private Double[][] matrix;

    private int rows;
    private int cols;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.matrix = new Double[rows][cols];
    }

    public Double[][] getMatrix() {
        return matrix;
    }

    public void generateMatrix(){
        Random random = new Random();
        for(int i=0; i<rows; i++){
            for(int j=0; j<cols; j++){
                double randomDouble = random.nextDouble() * 9;
                matrix[i][j] = randomDouble + 1.0;
            }
        }
    }
    public void writeToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    writer.write(Double.toString(matrix[i][j]));
                    writer.write(" ");
                }
                writer.newLine();
            }
            System.out.println("Matrix has been successfully written to file: " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing matrix to file: " + e.getMessage());
        }
    }

    public void readFromFile(String fileName){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int row = 0;
            while ((line = reader.readLine()) != null && row < rows) {
                String[] values = line.trim().split("\\s+");
                for (int col = 0; col < cols && col < values.length; col++) {
                    matrix[row][col] = Double.parseDouble(values[col]);
                }
                row++;
            }
            System.out.println("Matrix has been successfully read from file: " + fileName);
        } catch (IOException e) {
            System.err.println("Error reading matrix from file: " + e.getMessage());
        }
    }
    public void printMatrix(int sizeLimit){
        for(int i=0; i<sizeLimit; i++){
            for(int j=0; j<sizeLimit; j++){
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void setElement(int row, int col, Double element){
        matrix[row][col] = element;
    }
    public Double getElement(int row, int col){
        return matrix[row][col];
    }
    public Double[] getRow(int row){
        return matrix[row];
    }

    public Double[] getCol(int col){
        Double[] column = new Double[rows];
        for (int i = 0; i < rows; i++) {
            column[i] = matrix[i][col];
        }
        return column;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
