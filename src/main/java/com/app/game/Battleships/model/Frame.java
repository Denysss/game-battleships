package com.app.game.Battleships.model;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Frame extends JFrame {

    public final static int CELL_SIZE = 40,
            HALF_CELL_SIZE = CELL_SIZE / 2,
            THIRD_CELL_SIZE = CELL_SIZE / 3,
            QUARTER_CELL_SIZE = CELL_SIZE / 4,
            FIELD_SIZE = 10,
            FIELD_LENGTH = CELL_SIZE * (FIELD_SIZE + 1),
            SHIP_TOP_LINE_Y = CELL_SIZE * (FIELD_SIZE + 2) + HALF_CELL_SIZE;

    private static final Color COLOR_MY = new Color(0, 204, 255),
            COLOR_ENEMY = new Color(35, 45, 55);

    private Label ship4Label = new Label(0),
            ship3Label = new Label(CELL_SIZE * 5),
            ship2Label = new Label(CELL_SIZE * 9),
            ship1Label = new Label(CELL_SIZE * 12);
    
    private JLabel mainLabel;

    private Location myLocation = new Location(),
            enemyLocation = new Location();

    private Field myField, enemyField;
    private Ship[] ships = new Ship[10];

    private JButton button;
    
    private final String iconPath = "/icon.jpg";
    
    private static final Logger logger = LoggerFactory.getLogger(Frame.class);

    public Frame() {
        logger.info("START");
        initComponents();
        setVisible(true);
        logger.info("The End");
    }

    private void initComponents() {
        // Set Icon
        URL icon = getClass().getResource(iconPath);
        ImageIcon img = new ImageIcon(icon);
        setIconImage(img.getImage());
        // Set frame property
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(1000, 720));
        setTitle("BattleShip Game");
        setResizable(false);
        getContentPane().setLayout(null);
        
        mainLabel = addMainLabel();
        button = addStartButton();
        addNewShips();
        
        ships[0].setUp(CELL_SIZE, CELL_SIZE * 4, ship4Label, ships);
        ships[1].setUp(CELL_SIZE * 6, CELL_SIZE * 3, ship3Label, ships);
        ships[2].setUp(CELL_SIZE * 6, CELL_SIZE * 3, ship3Label, ships);
        ships[3].setUp(CELL_SIZE * 10, CELL_SIZE * 2, ship2Label, ships);
        ships[4].setUp(CELL_SIZE * 10, CELL_SIZE * 2, ship2Label, ships);
        ships[5].setUp(CELL_SIZE * 10, CELL_SIZE * 2, ship2Label, ships);
        ships[6].setUp(CELL_SIZE * 13, CELL_SIZE, ship1Label, ships);
        ships[7].setUp(CELL_SIZE * 13, CELL_SIZE, ship1Label, ships);
        ships[8].setUp(CELL_SIZE * 13, CELL_SIZE, ship1Label, ships);
        ships[9].setUp(CELL_SIZE * 13, CELL_SIZE, ship1Label, ships);

        myField = new Field(0, CELL_SIZE, 2 + FIELD_LENGTH, 2 + FIELD_LENGTH, COLOR_MY);

        enemyField = new Field(getWidth() - FIELD_LENGTH - HALF_CELL_SIZE,
                CELL_SIZE, 2 + FIELD_LENGTH, 2 + FIELD_LENGTH, COLOR_ENEMY);

        enemyLocation.setField(enemyField);
        myLocation.setField(myField);

        addAllToContentPane(ships[0], ships[1], ships[2], ships[3], ships[4], ships[5],
                ships[6], ships[7], ships[8], ships[9], button, mainLabel,
                ship4Label, ship3Label, ship2Label, ship1Label,
                myField, enemyField);
    }

    private void addAllToContentPane(Component... components) {
        Container contentPane = getContentPane();
        for (Component c : components) {
            contentPane.add(c);
        }
    }
    
    private void addNewShips() {
        for (int i = 0; i < ships.length; i++) {
            ships[i] = new Ship();
        }
    }

    private JButton addStartButton() {
        button = new JButton();
        button.setBounds(getWidth() / 2 - 40, getHeight() / 2 - 100, 80, 40);
        button.setText("Start");
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    if (isAllShipsOnField()) {
                        myLocation.generate(ships);
                        enemyLocation.generate();
                        button.setVisible(false);
                        Course battle = new Course(myLocation, enemyLocation, myField, enemyField, mainLabel);
                        battle.start();
                    }
                } catch (InterruptedException ex) {
                    logger.error("addStartButton.InterruptedException:", ex);
                }
            }
        });
        return button;
    }
    
    private JLabel addMainLabel() {
        JLabel label = new JLabel("Use 'Drug&Drop' for arranging the ships on the primary grid and 'Double Click' for turning them");
        label.setHorizontalAlignment((int) CENTER_ALIGNMENT);
        label.setVerticalAlignment((int) CENTER_ALIGNMENT);
        label.setBounds(0, 0, getWidth(), CELL_SIZE);
        return label;
    }

    private boolean isAllShipsOnField() {
        int numberOfShipsOnStart = 0;
        for (Ship ship : ships) {
            if (ship.getLocation().equals(ship.startPoint)) {
                numberOfShipsOnStart++;
            }
        }
        return numberOfShipsOnStart == 0;
    }
}