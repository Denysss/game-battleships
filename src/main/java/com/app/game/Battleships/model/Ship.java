package com.app.game.Battleships.model;

import static com.app.game.Battleships.model.Frame.CELL_SIZE;
import static com.app.game.Battleships.model.Frame.HALF_CELL_SIZE;
import static com.app.game.Battleships.model.Frame.SHIP_TOP_LINE_Y;
import static com.app.game.Battleships.model.Frame.THIRD_CELL_SIZE;
import static javax.swing.BorderFactory.createMatteBorder;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JTextField;

class Ship extends JTextField {

    public Point startPoint;
    private int height, width;
    private Point pointMouse;
    private final Color COLOR_FILL = new Color(204, 204, 255),
            COLOR_BORDER = new Color(51, 51, 51);
    private Label shipLabel;
    private boolean isShipOnStartPosition = false;
    private boolean isDragged = true;
    private Ship[] allShips;

    public Ship() {

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    if (getLocation().equals(startPoint)) {
                        setBounds(getX(), getY(), getHeight(), getWidth());
                        int tmp = height;
                        height = width;
                        width = tmp;
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pointMouse = getMousePosition();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isDragged) {
                    Point point = getShipPoint(e.getPoint());
                    if (isPointOnMyField(point) && !isPointOnShips(point)) {
                        setLocation(getPointOnMyField(point));
                    } else {
                        setLocation(startPoint);
                    }
                    refreshShipLabel();
                    pointMouse.setLocation(0, 0);
                }
            }
        });
                
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragged) {
                    setLocation(getPointOnMyField(getShipPoint(e.getPoint())));
                }
            }
        });
    }

    public Point getLocationStartPoint() {
        Point point = new Point();
        point.setLocation(getX() / CELL_SIZE - 1, getY() / CELL_SIZE - 2);
        return point;
    }

    public Point getLocationEndPoint() {
        Point point = getLocationStartPoint();
        point.setLocation(point.x + width - 1, point.y + height - 1);
        return point;
    }
    
    public void freezeDragging() {
        isDragged = false;
    }
    
    public void setUp(int x, int length, Label label, Ship[] ships) {
        setEditable(false);
        setBackground(COLOR_FILL);
        setBorder(createMatteBorder(1, 1, 1, 1, COLOR_BORDER));
        setDragEnabled(true);

        startPoint = new Point(x, SHIP_TOP_LINE_Y);
        setBounds(startPoint.x, startPoint.y, CELL_SIZE, length);
        width = 1;
        height = length / CELL_SIZE;
        allShips = ships;
        shipLabel = label;
        refreshShipLabel();
    }
    
    private boolean isPointOnMyField(Point point) {
        return CELL_SIZE - THIRD_CELL_SIZE <= point.x
                && CELL_SIZE * 2 - THIRD_CELL_SIZE <= point.y
                && CELL_SIZE * (11 - width) + THIRD_CELL_SIZE >= point.x
                && CELL_SIZE * (12 - height) + THIRD_CELL_SIZE >= point.y;
    }

    private Point getPointOnMyField(Point point) {
        if (isPointOnMyField(point) && !isPointOnShips(point)) {
            int modX = point.x % CELL_SIZE;
            if (modX > HALF_CELL_SIZE) {
                modX = modX - CELL_SIZE;
            }

            int modY = point.y % CELL_SIZE;
            if (modY > HALF_CELL_SIZE) {
                modY = modY - CELL_SIZE;
            }
            point.setLocation(point.x - modX, point.y - modY);
        }
        return point;
    }

    private Point getShipPoint(Point event) {
        return new Point(getLocation().x + event.x - pointMouse.x,
                getLocation().y + event.y - pointMouse.y);
    }

    private void refreshShipLabel() {
        if (isShipOnStartPosition) {
            if (!getLocation().equals(startPoint)) {
                shipLabel.setNumberOfShips(shipLabel.getNumberOfShips() - 1);
                isShipOnStartPosition = false;
            }
        } else {
            if (getLocation().equals(startPoint)) {
                shipLabel.setNumberOfShips(shipLabel.getNumberOfShips() + 1);
                isShipOnStartPosition = true;
            }
        }
    }

    private boolean isPointOnShips(Point point) {
        int PointOnShips = 0;
        for (Ship ship : allShips) {
            if (ship.isPointOnMe(point, width, height)) {
                PointOnShips++;
            }
        }
        return PointOnShips > 1;
    }

    private boolean isPointOnMe(Point point, int w, int h) {
        return (getX() - (0.5 + w) * CELL_SIZE) <= point.x
                && (getY() - (0.5 + h) * CELL_SIZE) <= point.y
                && (getX() + (0.5 + width) * CELL_SIZE) >= point.x
                && (getY() + (0.5 + height) * CELL_SIZE) >= point.y
                && !getLocation().equals(startPoint);
    }
}