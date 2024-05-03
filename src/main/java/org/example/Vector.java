package org.example;

import java.io.*;
import java.util.List;
import java.util.Random;

public class Vector {
    private Double[] elements;
    private int size;

    public Vector(int size) {
        this.size = size;
        this.elements = new Double[size];
    }
    public Double[] getVector(){
        return elements;
    }
    public void generateVector(){
        Random random = new Random();
        for(int i=0; i<size; i++){
            double randomDouble = random.nextDouble() * 1000; // Generate between 0.0 and 8.999...
            elements[i] = randomDouble + 100.0;
        }
    }
    public void writeToFile(String fileName){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < size; i++) {
                writer.write(Double.toString(elements[i]));
                writer.newLine();
            }
            System.out.println("Vector has been successfully written to file: " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing vector to file: " + e.getMessage());
        }
    }

    public void readFromFile(String fileName){
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int el = 0;
            while ((line = reader.readLine()) != null && el < size) {
                String value = line.trim();
                elements[el] = Double.parseDouble(value);
                el++;

            }
            System.out.println("Vector has been successfully read from file: " + fileName);
        } catch (IOException e) {
            System.err.println("Error reading vector from file: " + e.getMessage());
        }
    }
    public void printVector(int sizeLimit){
        for(int i=0; i<sizeLimit; i++){
            System.out.print(elements[i] + " ");
        }
        System.out.println();
        System.out.println();
    }

    public void setElement(int num, Double element){
        elements[num] = element;
    }

    public void setVector(List<Double> array){
        for (int i = 0; i < array.size(); i++) {
            elements[i] = array.get(i);
        }
    }
    public Double getElement(int num){
        return elements[num];
    }


    public int getSize() {
        return size;
    }
}
