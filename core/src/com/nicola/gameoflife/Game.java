package com.nicola.gameoflife;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Nicola Lombardi on 24/10/2016.
 */
public class Game {
    public static final int ALIVE = 1, DEAD = 0;
    public static int dimX, dimY;
    private int[][] population;
    private int[][] previusGenPop;
    private int generations = 0;
    private boolean isChanging = false;

    public static class Cell {
        private int x, y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public Game(int x, int y) {
        dimX = x;
        dimY = y;
        population = new int[dimX][dimY];
    }

    public void update() {
        int[][] temp = new int[dimX][dimY];
        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                if (population[i][j] == ALIVE) {
                    if (checkForDeathByOverpopulation(i, j) || checkForDeathByUnderpopulation(i, j)) {
                        temp[i][j] = DEAD;
                    }
                    if (checkForSurvival(i, j))
                        temp[i][j] = ALIVE;
                }
            }
        }
        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                if (population[i][j] == DEAD) {
                    if (checkForReproduction(i, j)) {
                        temp[i][j] = ALIVE;
                    } else
                        temp[i][j] = DEAD;
                }
            }
        }
        population = temp.clone();
        isChanging = !Arrays.deepEquals(population, previusGenPop);

        previusGenPop = population.clone();
        generations++;
    }

    public int[][] getGrid() {
        return population;
    }

    public void reverseCell(Cell cell) {
        population[cell.x][cell.y] = 1 - population[cell.x][cell.y];
    }

    public void setCell(Cell cell, boolean alive){
        population[cell.x][cell.y] = alive ? 1 : 0;
    }

    public void reset() {
        population = new int[dimX][dimY];
        generations = 0;
    }

    public void generateRandom(int amount) {
        reset();
        Random r = new Random();
        for (int i = 0; i < dimX; i++) {
            for (int j = 0; j < dimY; j++) {
                if (r.nextInt(100) < amount)
                    population[i][j] = ALIVE;
                else
                    population[i][j] = DEAD;
            }
        }
    }

    public void setPattern(Pattern p) {
        int x = p.getDimX();
        int y = p.getDimY();
        reset();
        int offX = dimX / 2;
        int offY = dimY / 2;
        for (int i = 0; i < y; i++) {
            for (int j = 0; j < x; j++) {
                population[j + offX - x / 2][i + offY - y / 2] = p.getGrid()[y - 1 - i][j];
            }
        }

    }

    public int getGenerations() {
        return generations;
    }

    public boolean isChanging() {
        return isChanging;
    }


    //Returns true if a live cell has less than two neighbours
    private boolean checkForDeathByUnderpopulation(int x, int y) {
        return neighbours(x, y) < 2;
    }

    //Returns true if a live cell has more than three neighbours
    private boolean checkForDeathByOverpopulation(int x, int y) {
        return neighbours(x, y) > 3;
    }

    //Returns true if the cell stays alive
    private boolean checkForSurvival(int x, int y) {
        return neighbours(x, y) == 2 || neighbours(x, y) == 3;
    }

    //Returns true if a dead cell has exactly three neighbours
    private boolean checkForReproduction(int x, int y) {
        return neighbours(x, y) == 3;
    }

    //Returns the number of neighbour of a given cell
    private int neighbours(int x, int y) {
        int neighbourCount = 0;
        for (int i = x-1; i <= x+1; i++){
            for (int j = y-1; j <= y+1; j++) {
                if(i != x || j != y) {
                    if (i >= 0 && i < dimX && j >= 0 && j < dimY) {
                        if (population[i][j] == ALIVE) {
                            neighbourCount++;
                        }
                    }
                }
            }
        }

        //Return the neighbours count
        return neighbourCount;
    }
}
