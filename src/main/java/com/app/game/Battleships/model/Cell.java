package com.app.game.Battleships.model;

public class Cell {

    public static enum cellValue {

        EMPTY, OCCUPIED, MISS, HIT, CRUSHED, FAIL
    };
    private cellValue value = cellValue.EMPTY;

    public Cell() {
    }

    public cellValue getValue() {
        return value;
    }

    public void setValue(cellValue v) {
        value = v;
    }

}