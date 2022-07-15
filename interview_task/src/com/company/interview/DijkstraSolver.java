package com.company.interview;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

public class DijkstraSolver {

    private final char[][] cells;
    private double[][] routeCosts;
    private int[][] previousCellFirstIndex;
    private int[][] previousCellSecondIndex;
    private final SortedSet<Cell> cellsQueue;

    private final int matrixHeight;
    private final int matrixWidth;
    private int startCellFirstIndex = emptyIndex;
    private int startCellSecondIndex = emptyIndex;
    private int endCellFirstIndex = emptyIndex;
    private int endCellSecondIndex = emptyIndex;

    private final static int emptyIndex = -1;
    private final static double closeStepValue = 1.0;
    private final static double furtherStepValue = 1.414;
    private final static char wallSymbol = '#';
    private final static char startSymbol = 'S';
    private final static char endSymbol = 'E';
    private final static char routeSymbol = '+';

    private static final int[][] closeNeighborsShifts = {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
    };

    private static final int[][] furtherNeighborsShifts = {
            {1, 1},
            {1, -1},
            {-1, 1},
            {-1, -1}
    };

    public DijkstraSolver(char[][] cells) {
        this.cells = cells;

        matrixHeight = cells.length;
        matrixWidth = cells[0].length;

        cellsQueue = new TreeSet<>();
    }

    public void solve() {
        instatiateMatrixes();

        lookForStartCell();
        lookForEndCell();

        traverseMatrix();

        findRoute();
    }

    private void instatiateMatrixes() {
        routeCosts = new double[matrixHeight][matrixWidth];
        fillCostsMatrix(routeCosts);

        previousCellFirstIndex = new int[matrixHeight][matrixWidth];
        fillIndexesMatrix(previousCellFirstIndex);

        previousCellSecondIndex = new int[matrixHeight][matrixWidth];
        fillIndexesMatrix(previousCellSecondIndex);
    }

    private void fillCostsMatrix(double[][] matrix) {
        for (double[] array : matrix) {
            Arrays.fill(array, Double.MAX_VALUE);
        }
    }

    private void fillIndexesMatrix(int[][] matrix) {
        for (int[] array : matrix) {
            Arrays.fill(array, emptyIndex);
        }
    }

    private void lookForStartCell() {
        int[] result = lookForCell(startSymbol);

        startCellFirstIndex = result[0];
        startCellSecondIndex = result[1];

        if (startCellFirstIndex == -1) {
            throw new InvalidParameterException("Missing start point");
        }

        routeCosts[startCellFirstIndex][startCellSecondIndex] = 0;
        cellsQueue.add(new Cell(0, startCellFirstIndex, startCellSecondIndex));
    }

    private void lookForEndCell() {
        int[] result = lookForCell(endSymbol);

        endCellFirstIndex = result[0];
        endCellSecondIndex = result[1];

        if (endCellFirstIndex == -1) {
            throw new InvalidParameterException("Missing end point");
        }
    }

    private int[] lookForCell(char value) {
        int[] result = {-1, -1};
        for (int i = 0; i < matrixHeight; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (cells[i][j] == value) {
                    result[0] = i;
                    result[1] = j;
                    break;
                }
            }
        }

        return result;
    }

    private void traverseMatrix() {
        while (!cellsQueue.isEmpty() && isNextIterationSensable()) {
            Cell currentCell = cellsQueue.first();
            updateNeighborsCosts(currentCell);
            deleteCell(currentCell);
        }
    }

    private void updateNeighborsCosts(Cell currentCell) {
        updateCloseNeighborsCosts(currentCell);
        updateFurtherNeighborsCosts(currentCell);
    }

    private void updateCloseNeighborsCosts(Cell currentCell) {
        updateAroundCellsWithShifts(currentCell, closeNeighborsShifts, closeStepValue);
    }

    private void updateFurtherNeighborsCosts(Cell currentCell) {
        updateAroundCellsWithShifts(currentCell, furtherNeighborsShifts, furtherStepValue);
    }

    private void updateAroundCellsWithShifts(Cell currentCell, int[][] furtherNeighborsShifts, double stepValue) {
        for (int[] shiftCoordinates : furtherNeighborsShifts) {
            int nextCellFirstIndex = currentCell.getFirstIndex() + shiftCoordinates[0];
            int nextCellSecondIndex = currentCell.getSecondIndex() + shiftCoordinates[1];

            checkAndUpdateCellCost(nextCellFirstIndex, nextCellSecondIndex, currentCell, stepValue);
        }
    }

    private boolean isNextIterationSensable() {
        return cellsQueue.first().getCost() < routeCosts[endCellFirstIndex][endCellSecondIndex];
    }

    private void checkAndUpdateCellCost(int nextCellFirstIndex, int nextCellSecondIndex, Cell currentCell, double stepValue) {
        if (isValidIndex(nextCellFirstIndex, nextCellSecondIndex) && isNotWall(nextCellFirstIndex, nextCellSecondIndex)) {
            double possibleValue = currentCell.getCost() + stepValue;

            if (possibleValue < routeCosts[nextCellFirstIndex][nextCellSecondIndex]) {
                previousCellFirstIndex[nextCellFirstIndex][nextCellSecondIndex] = currentCell.getFirstIndex();
                previousCellSecondIndex[nextCellFirstIndex][nextCellSecondIndex] = currentCell.getSecondIndex();

                routeCosts[nextCellFirstIndex][nextCellSecondIndex] = possibleValue;

                createCellOrUpdateCellCost(nextCellFirstIndex, nextCellSecondIndex, possibleValue);
            }
        }

    }

    private boolean isNotWall(int cellFirstIndex, int cellSecondIndex) {
        return cells[cellFirstIndex][cellSecondIndex] != wallSymbol;
    }

    private boolean isValidIndex(int cellFirstIndex, int cellSecondIndex) {
        return isBetweenBoundaries(cellFirstIndex, matrixHeight) && isBetweenBoundaries(cellSecondIndex, matrixWidth);
    }

    private boolean isBetweenBoundaries(int index, int maxSize) {
        return index >= 0 && index < maxSize;
    }

    private void createCellOrUpdateCellCost(int cellFirstIndex, int cellSecondIndex, double cellCost) {
        Optional<Cell> cellOptional = cellsQueue
                .stream()
                .filter(cell -> cell.getFirstIndex() == cellFirstIndex && cell.getSecondIndex() == cellSecondIndex)
                .findFirst();

        if (cellOptional.isPresent()) {
            Cell tempCell = cellOptional.get();
            tempCell.setCost(cellCost);
        } else {
            addCell(cellCost, cellFirstIndex, cellSecondIndex);
        }
    }

    private void deleteCell(Cell cell) {
        cellsQueue.remove(cell);
    }

    private void addCell(double cellCost, int cellFirstIndex, int cellSecondIndex) {
        cellsQueue.add(new Cell(cellCost, cellFirstIndex, cellSecondIndex));
    }

    private void findRoute() {
        if (previousCellFirstIndex[endCellFirstIndex][endCellSecondIndex] == emptyIndex) {
            System.out.println("No way was found");
        } else {
            editRoute();
            printResultTable();
        }
    }

    private void editRoute() {
        int currentCellFirstIndex = previousCellFirstIndex[endCellFirstIndex][endCellSecondIndex];
        int currentCellSecondIndex = previousCellSecondIndex[endCellFirstIndex][endCellSecondIndex];

        while ((currentCellFirstIndex != startCellFirstIndex) || (currentCellSecondIndex != startCellSecondIndex)) {
            cells[currentCellFirstIndex][currentCellSecondIndex] = routeSymbol;

            int newFirstIndex = previousCellFirstIndex[currentCellFirstIndex][currentCellSecondIndex];
            currentCellSecondIndex = previousCellSecondIndex[currentCellFirstIndex][currentCellSecondIndex];

            currentCellFirstIndex = newFirstIndex;
        }
    }

    private void printResultTable() {
        for (char[] line : cells) {
            System.out.println(String.copyValueOf(line));
        }
    }
}
