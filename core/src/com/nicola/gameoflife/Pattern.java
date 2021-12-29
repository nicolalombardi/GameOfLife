package com.nicola.gameoflife;

public class Pattern {
    private int grid[][];
    private int dimX;
    private int dimY;

    public Pattern(int[][] grid, int dimX, int dimY) {
        this.grid = grid;
        this.dimX = dimX;
        this.dimY = dimY;
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getDimX() {
        return dimX;
    }

    public int getDimY() {
        return dimY;
    }
}
