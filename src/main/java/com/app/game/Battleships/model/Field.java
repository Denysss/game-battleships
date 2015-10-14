package com.app.game.Battleships.model;

import static com.app.game.Battleships.model.Frame.CELL_SIZE;
import static com.app.game.Battleships.model.Frame.FIELD_SIZE;
import static com.app.game.Battleships.model.Frame.HALF_CELL_SIZE;
import static com.app.game.Battleships.model.Frame.QUARTER_CELL_SIZE;
import static com.app.game.Battleships.model.Frame.THIRD_CELL_SIZE;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class Field extends JPanel{

    public final Color colorGrey = new Color(51, 51, 51),
            colorHit = new Color(163, 38, 56),
            colorMiss = new Color(28, 107, 160);
    private final Font font = new Font("Tahoma", 0, 20);
    private final char[] topChars = {'A','B','C','D','E','F','G','H','I','K'};
    private final char[] leftChars = {'0','1','2','3','4','5','6','7','8','9'};
    private final Point nullPoint = new Point(-1, -1);
    private final Point shotPoint = new Point(nullPoint);
    private final Color colorLine;
    private final int syncWait= 10000;
    private boolean isReadyToShot = false;
    private Graphics2D graphics;
    
    Field(int x, int y, int w, int h, Color color) {
        setBounds(x, y, w, h);
        colorLine = color;
        
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (isReadyToShot) {
                    Point p = new Point(convertXY(e.getX()), convertXY(e.getY()));
                    if (isPointOnField(p)) {
                        shotPoint.setLocation(p);
                    }
                }
                synchronized(shotPoint) {
                    shotPoint.notifyAll();
                }
            }
        });
    }
    
    @Override
    @SuppressWarnings("empty-statement")
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        graphics = (Graphics2D) g;
        
        graphics.setColor(colorGrey);
        graphics.setFont(font);
        for (int i = 0; i < FIELD_SIZE; i++) {
            graphics.drawChars(topChars, i, 1,
                   CELL_SIZE * (i + 1) + THIRD_CELL_SIZE,
                   CELL_SIZE - QUARTER_CELL_SIZE);
        }

        for (int i = 0; i < FIELD_SIZE; i++) {
            graphics.drawChars(leftChars, i, 1,
                  HALF_CELL_SIZE,
                  CELL_SIZE * (i + 2) - THIRD_CELL_SIZE);
        }
                
        graphics.setColor(colorLine);
        for (int i = 1; i <= FIELD_SIZE; i++) {
            for (int j = 1; j <= FIELD_SIZE; j++) {
                graphics.drawRect(CELL_SIZE * i, CELL_SIZE * j,
                       CELL_SIZE, CELL_SIZE);
            }
        }
    }
    
    public void setReadyToShot() {
        isReadyToShot = true;
    }
    
    public Point getShotPoint() throws InterruptedException {
        do {
            synchronized (shotPoint) {
                shotPoint.wait(syncWait);
            }
        } while (shotPoint.equals(nullPoint));
        Point tmpPoint = new Point(shotPoint);
        shotPoint.setLocation(nullPoint);
        return tmpPoint;
    }
    
    public void drawMiss(Point miss) {
        graphics = (Graphics2D) super.getGraphics();
        graphics.setColor(colorMiss);
        graphics.setStroke(new BasicStroke(1));
        graphics.fillOval(deConvertXY(miss.x) + THIRD_CELL_SIZE,
                    deConvertXY(miss.y) + THIRD_CELL_SIZE,
                    THIRD_CELL_SIZE,
                    THIRD_CELL_SIZE);
    }
    
    public void drawHit(Point hit) {
        graphics = (Graphics2D) super.getGraphics();
        graphics.setColor(colorHit);
        graphics.setStroke(new BasicStroke(2));
        int x = deConvertXY(hit.x),
            y = deConvertXY(hit.y);
        graphics.drawLine(x + 1, y + 1, x + CELL_SIZE - 1, y + CELL_SIZE - 1);
        graphics.setStroke(new BasicStroke(3));
        graphics.drawLine(x + 2, y + CELL_SIZE - 2, x + CELL_SIZE - 2, y + 2);
    }

    public void drawCrush(Point crush) {
        graphics = (Graphics2D) super.getGraphics();
        graphics.setColor(colorLine);
        graphics.setStroke(new BasicStroke(3));
        int x = deConvertXY(crush.x),
            y = deConvertXY(crush.y);
        graphics.drawRect(x + 1, y + 1, CELL_SIZE - 1, CELL_SIZE - 1);
    }
    
    public boolean isPointOnField(Point p) {
        if (p == null) {
            return false;
        } else {
            return p.x >= 0 && p.x < FIELD_SIZE && p.y >= 0 && p.y < FIELD_SIZE;
        }
    }
    
    private int convertXY(int i) {
        if (i >= CELL_SIZE) {
            return (i - CELL_SIZE) / CELL_SIZE;
        } else {
            return -1;
        }
    }

    private int deConvertXY(int i) {
        return (i + 1) * CELL_SIZE;
    }
}