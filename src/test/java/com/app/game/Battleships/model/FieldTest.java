package com.app.game.Battleships.model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

public class FieldTest {
    
    private static Field instance = null;
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FieldTest.class);
    
    public FieldTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        instance = new Field(0, 0, 1, 1, new Color(8, 16, 32));
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testGetShotPoint() throws Exception {
        setUpClass();
        final java.lang.reflect.Field field = instance.getClass().getDeclaredField("shotPoint");
        field.setAccessible(true);
        Point initShotPoint = (Point) field.get(instance);
        
        assertTrue("shotPoint should be [-1, -1]", initShotPoint.x == -1 && initShotPoint.y == -1);
        
        Thread thread = new Thread() {
            @Override public void run() {
                super.run();
                try {
                    field.set(instance, new Point(5, 9));
                } catch (IllegalArgumentException ex) {
                    logger.error("testGetShotPoint.IllegalArgumentException:", ex);
                } catch (IllegalAccessException ex) {
                    logger.error("testGetShotPoint.IllegalAccessException:", ex);
                }
                synchronized(field) {
                    field.notifyAll();
                }
            }
        };
        thread.run();
        Point shotPoint = instance.getShotPoint();
        initShotPoint = (Point) field.get(instance);
        
        assertTrue("getShotPoint should be [5, 9]", shotPoint.x == 5 && shotPoint.y == 9);
        assertTrue("shotPoint should be [-1, -1]", initShotPoint.x == -1 && initShotPoint.y == -1);
    }

/*    @Test
    public void testPaintComponent() throws Exception {
    Graphics gMock = Mockito.mock(Graphics.class);

    Color expectedColor = instance.colorGrey;
    java.lang.reflect.Field field = instance.getClass().getDeclaredField("font");
    field.setAccessible(true);
    Font expectedFont = (Font) field.get(instance);

    instance.paintComponent(gMock); // get super Graphics?

    Mockito.verify(gMock).setColor(expectedColor);
    Mockito.verify(gMock).setFont(expectedFont);
    // ..?
    
    }
*/
    @Test
    public void testIsPointOnField_PointShouldBeOnField() {
        setUpClass();
        String message = " should be on the Field";
        int[][] res = {{0, 9}, {9, 0}, {9, 9}, {0, 0}};

        for (int i = 0; i < res.length; i++) {        
            Point p = new Point(res[i][0], res[i][1]);
            assertTrue(p.toString() + message, instance.isPointOnField(p));
        }
    }
    
    @Test
    public void testIsPointOnField_PointShouldNotBeOnField() {
        setUpClass();
        String message = " should not be on the Field";
        int[][] res = {{10, 0}, {0, 10}, {9, -1}, {-1, 9}};

        Point p = null;
        assertFalse("Point null" + message, instance.isPointOnField(p));

        for (int i = 0; i < res.length; i++) {        
            p = new Point(res[i][0], res[i][1]);
            assertFalse(p.toString() + message, instance.isPointOnField(p));
        }
    }
    
    @Test
    public void testConvertXY() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        setUpClass();
        String message = " should return ";
        String methodName = "convertXY";
        int[][] res = {{39, -1}, {0, -1}, {-1, -1}, {40, 0}, {79, 0}, {9854753, 246367}};
        
        Class[] methodParameters = new Class[1];
        methodParameters[0] = Integer.TYPE;
        Method method = instance.getClass().getDeclaredMethod(methodName, methodParameters);
        method.setAccessible(true);

        for (int i = 0; i < res.length; i++) {
            assertTrue(methodName + "(" + res[i][0] + ")" + message + res[i][1],
                    res[i][1] == (Integer) method.invoke(instance, res[i][0]));
        }
    }
    
}
