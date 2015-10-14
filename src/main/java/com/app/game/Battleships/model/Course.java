package com.app.game.Battleships.model;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Course extends Thread {
    
    private static final Logger logger = LoggerFactory.getLogger(Course.class);
    private final Location myLocation, enemyLocation;
    private final Field myField, enemyField;
    private final JLabel label;
    private boolean isVictoryMine = false, isVictoryEnemy = false;

    Course(Location myLoc, Location enemyLoc, Field myF, Field enemyF, JLabel lab) throws InterruptedException {
        myLocation = myLoc;
        myField = myF;
        enemyLocation = enemyLoc;
        enemyField = enemyF;
        enemyField.setReadyToShot();
        label = lab;
    }

    public void run() {
        Point shotPoint = new Point();
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        Cursor cursor = enemyField.getCursor();
        while (!isVictoryMine && !isVictoryEnemy) {
            do {
                try {
                    enemyField.setCursor(cursor);
                    shotPoint = enemyField.getShotPoint();
                    enemyField.setCursor(blankCursor);
                } catch (InterruptedException ex) {
                    logger.error("run.InterruptedException:", ex);
                }
            } while (!enemyLocation.shot(shotPoint));
            sleep(500);
            myLocation.shot();
            isVictoryMine = enemyLocation.isKilledAllShips();
            isVictoryEnemy = myLocation.isKilledAllShips();
        }
        enemyField.setCursor(cursor);
        showMessage();
    }
    
    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
            logger.error("sleep.InterruptedException:", ex);
        }
    }
    
    private void showMessage() {
        if (isVictoryEnemy) {
            label.setText("You lost the game!");
        } else if (isVictoryMine) {
            label.setText("You won the game!");
        } else {
            label.setText("No One survived!!!");
        }
    }
}