package com.company.interview;

public class Cell implements Comparable<Cell>{

    private double cost;
    private int firstIndex;
    private int secondIndex;
    private int id;
    private static int cellNumber = 0;

    public Cell(double cost, int firstIndex, int secondIndex) {
        this.cost = cost;
        this.firstIndex = firstIndex;
        this.secondIndex = secondIndex;
        id = cellNumber;
        cellNumber++;
    }

    @Override
    public int compareTo(Cell o) {
        int difference = (int)((cost-o.getCost())*100000)+id-o.getId();
        return difference;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public void setFirstIndex(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    public int getSecondIndex() {
        return secondIndex;
    }

    public void setSecondIndex(int secondIndex) {
        this.secondIndex = secondIndex;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "cost=" + cost +
                ", firstIndex=" + firstIndex +
                ", secondIndex=" + secondIndex +
                '}';
    }

}
