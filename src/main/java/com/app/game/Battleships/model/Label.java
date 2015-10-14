package com.app.game.Battleships.model;

import static com.app.game.Battleships.model.Frame.CELL_SIZE;
import static com.app.game.Battleships.model.Frame.SHIP_TOP_LINE_Y;
import javax.swing.JLabel;

public class Label extends JLabel {
    
    private int numberOfShips = 0;
    
    public Label (int x) {
        setHorizontalAlignment((int) CENTER_ALIGNMENT);
        setVerticalAlignment((int) CENTER_ALIGNMENT);
        setBounds(x, SHIP_TOP_LINE_Y, CELL_SIZE, CELL_SIZE);
    }
    
    public void setNumberOfShips (int cnt) {
        if (cnt > 0) {
            numberOfShips = cnt;
            setText(cnt + " pcs.");
        } else {
            numberOfShips = 0;
            setText("");
        }
    }
    
    public int getNumberOfShips() {
        return numberOfShips;
    }
}