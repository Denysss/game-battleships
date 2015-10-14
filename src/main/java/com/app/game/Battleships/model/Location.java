package com.app.game.Battleships.model;

import static com.app.game.Battleships.model.Frame.FIELD_SIZE;
import com.app.game.Battleships.model.Cell.cellValue;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Location {

    private static enum direction {

        WEST, EAST, SOUTH, NORTH
    };

    private Cell[][] cells = new Cell[FIELD_SIZE][FIELD_SIZE];

    private Field field;

    public Location() {
        for (int x = 0; x < FIELD_SIZE; x++) {
            for (int y = 0; y < FIELD_SIZE; y++) {
                cells[x][y] = new Cell();
            }
        }
    }

    public void setField(Field f) {
        field = f;
    }
    
    public boolean shot(Point p) {
        if (isPointEquals(p, cellValue.OCCUPIED)) {
            setCellValue(p, cellValue.HIT);
            setCellValue(new Point(p.x - 1, p.y - 1), cellValue.MISS);
            setCellValue(new Point(p.x - 1, p.y + 1), cellValue.MISS);
            setCellValue(new Point(p.x + 1, p.y - 1), cellValue.MISS);
            setCellValue(new Point(p.x + 1, p.y + 1), cellValue.MISS);
            if (isKilledShip(p)) {
                bury(p);
            }
            return true;
        } else if (isPointEquals(p, cellValue.EMPTY)) {
            setCellValue(p, cellValue.MISS);
            return true;
        } else {
            return false;
        }
    }

    public void shot() {
        Random rnd = new Random();
        List<Point> p;
        if (isHitOnField()) {
            p = getEmptyPointsNearHitPoints();
        } else {
            p = getPointList(cellValue.EMPTY, cellValue.OCCUPIED);
        }
        shot(p.get(rnd.nextInt(p.size())));
    }

    public boolean isKilledAllShips() {
        Point p = new Point(0, 0);
        for (p.setLocation(p.x, 0); p.y < FIELD_SIZE; p.setLocation(p.x, p.y + 1)) {
            for (p.setLocation(0, p.y); p.x < FIELD_SIZE; p.setLocation(p.x + 1, p.y)) {
                if (isPointEquals(p, cellValue.OCCUPIED)
                        || isPointEquals(p, cellValue.HIT)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void generate() {
        Random rnd = new Random();
        setRandomShip(rnd, 3);
        setRandomShip(rnd, 2);
        setRandomShip(rnd, 2);
        setRandomShip(rnd, 1);
        setRandomShip(rnd, 1);
        setRandomShip(rnd, 1);
        setRandomShip(rnd, 0);
        setRandomShip(rnd, 0);
        setRandomShip(rnd, 0);
        setRandomShip(rnd, 0);
    }

    public void generate(Ship[] ships) {
        Point start, end;
        for (Ship ship : ships) {
            start = ship.getLocationStartPoint();
            end = ship.getLocationEndPoint();
            occupy(start, end);
            ship.freezeDragging();
        }
    }

    private boolean isPointEquals(Point p, cellValue value) {
        if (field.isPointOnField(p)) {
            return cells[p.x][p.y].getValue().equals(value);
        } else {
            return false;
        }
    }

    private void setCellValue(Point point, cellValue value) {
        if (field.isPointOnField(point)) {
            switch (value) {
                case MISS:
                    field.drawMiss(point);
                    break;
                case HIT:
                    field.drawHit(point);
                    break;
                case CRUSHED:
                    field.drawCrush(point);
                    break;
            }
            cells[point.x][point.y].setValue(value);
        }
    }

    private boolean canOccupied(Point start, Point end) {
        int startX = start.x, startY = start.y, endX = end.x, endY = end.y;
        if (field.isPointOnField(start) && field.isPointOnField(end)
                && start.x <= end.x && start.y <= end.y) {
            if (start.x > 0) {
                startX = start.x - 1;
            }
            if (start.y > 0) {
                startY = start.y - 1;
            }
            if (end.x < FIELD_SIZE - 1) {
                endX = end.x + 1;
            }
            if (end.y < FIELD_SIZE - 1) {
                endY = end.y + 1;
            }
            Point p = new Point(startX, startY);
            for (p.setLocation(startX, p.y); p.x <= endX; p.setLocation(p.x + 1, p.y)) {
                for (p.setLocation(p.x, startY); p.y <= endY; p.setLocation(p.x, p.y + 1)) {
                    if (isPointEquals(p, cellValue.OCCUPIED)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean occupy(Point start, Point end) {
        if (canOccupied(start, end)) {
            Point p = new Point(start);
            for (p.setLocation(start.x, p.y); p.x <= end.x; p.setLocation(p.x + 1, p.y)) {
                for (p.setLocation(p.x, start.y); p.y <= end.y; p.setLocation(p.x, p.y + 1)) {
                    setCellValue(p, cellValue.OCCUPIED);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean isKilledShip(Point p) {
        return isKilledShip(new Point(p.x - 1, p.y), direction.WEST)
                && isKilledShip(new Point(p.x + 1, p.y), direction.EAST)
                && isKilledShip(new Point(p.x, p.y - 1), direction.NORTH)
                && isKilledShip(new Point(p.x, p.y + 1), direction.SOUTH);
    }

    private boolean isKilledShip(Point p, direction direction) {
        switch (direction) {
            case WEST:
                if (p.x >= 0) {
                    if (isPointEquals(p, cellValue.OCCUPIED)) {
                        return false;
                    } else if (isPointEquals(p, cellValue.HIT)) {
                        return isKilledShip(new Point(p.x - 1, p.y), direction.WEST);
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            case NORTH:
                if (p.y >= 0) {
                    if (isPointEquals(p, cellValue.OCCUPIED)) {
                        return false;
                    } else if (isPointEquals(p, cellValue.HIT)) {
                        return isKilledShip(new Point(p.x, p.y - 1), direction.NORTH);
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            case EAST:
                if (p.x < FIELD_SIZE) {
                    if (isPointEquals(p, cellValue.OCCUPIED)) {
                        return false;
                    } else if (isPointEquals(p, cellValue.HIT)) {
                        return isKilledShip(new Point(p.x + 1, p.y), direction.EAST);
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            case SOUTH:
                if (p.y < FIELD_SIZE) {
                    if (isPointEquals(p, cellValue.OCCUPIED)) {
                        return false;
                    } else if (isPointEquals(p, cellValue.HIT)) {
                        return isKilledShip(new Point(p.x, p.y + 1), direction.SOUTH);
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
        }
        return true;
    }

    private void bury(Point p) {
        if (isPointEquals(p, cellValue.HIT)) {
            setCellValue(p, cellValue.CRUSHED);
            bury(new Point(p.x - 1, p.y));
            bury(new Point(p.x + 1, p.y));
            bury(new Point(p.x, p.y - 1));
            bury(new Point(p.x, p.y + 1));
        } else if (isPointEquals(p, cellValue.EMPTY)) {
            setCellValue(p, cellValue.MISS);
        }
    }

    private void setRandomShip(Random rnd, int lenShip) {
        boolean set;
        Point point;
        do {
            point = new Point(rnd.nextInt(FIELD_SIZE), rnd.nextInt(FIELD_SIZE));
            if (rnd.nextBoolean()) {
                set = occupy(point, new Point(point.x + lenShip, point.y));
            } else {
                set = occupy(point, new Point(point.x, point.y + lenShip));
            }
        } while (!set);
    }

    private boolean isHitOnField() {
        List<Point> p;
        p = getPointList(cellValue.HIT);
        return p.size() > 0;
    }

    private List<Point> getEmptyPointsNearHitPoints() {
        List<Point> listHitPoint, listEmptyPoint = new ArrayList<>();
        listHitPoint = getPointList(cellValue.HIT);
        for (Point point : listHitPoint) {
            addPointToList(listEmptyPoint, new Point(point.x - 1, point.y));
            addPointToList(listEmptyPoint, new Point(point.x + 1, point.y));
            addPointToList(listEmptyPoint, new Point(point.x, point.y - 1));
            addPointToList(listEmptyPoint, new Point(point.x, point.y + 1));
        }
        return listEmptyPoint;
    }

    private void addPointToList(List<Point> list, Point point, cellValue... cellValues) {
        for (cellValue v : cellValues) {
            if (isPointEquals(point, v)) {
                list.add(point);
            }
        }
    }

    private void addPointToList(List<Point> list, Point point) {
        addPointToList(list, point, cellValue.EMPTY, cellValue.OCCUPIED);
    }

    private List<Point> getPointList(cellValue... cellValues) {
        List<Point> pointList = new ArrayList<>();
        Point p = new Point(0, 0);
        for (p.setLocation(p.x, 0); p.y < FIELD_SIZE; p.setLocation(p.x, p.y + 1)) {
            for (p.setLocation(0, p.y); p.x < FIELD_SIZE; p.setLocation(p.x + 1, p.y)) {
                for (cellValue v : cellValues) {
                    if (isPointEquals(p, v)) {
                        pointList.add(new Point(p.x, p.y));
                    }
                }
            }
        }
        return pointList;
    }
}