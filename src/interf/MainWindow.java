package interf;

import map.RegularGridMap;
import plan.DynamicProgrammingAlgorithm;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainWindow {
    private JFrame frame;
    private JButton backButton;
    private JPanel mainPanel;
    private JButton forwardButton;
    private JButton browseButton;
    private JLabel mainLabel;

    final JFileChooser fileChooser = new JFileChooser();

    private int actionCounter = 0;
    private File f;
    private BufferedImage image;

    private final int numActions = 4;
    int cellSize = 64;
    RegularGridMap map;
    DynamicProgrammingAlgorithm planner;
    List<BufferedImage> iconHistoric;


    public MainWindow() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(mainPanel);
        frame.setMinimumSize(new Dimension(500, 500));

        backButton.addActionListener(e -> {
            if (actionCounter > 0) actionCounter--;
            repaintMap();
        });
        forwardButton.addActionListener(e -> {
            if (actionCounter <= numActions) actionCounter++;
            repaintMap();
        });
        forwardButton.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0), "RIGHT_pressed");
        backButton.getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW).
                put(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0), "LEFT_pressed");
        browseButton.addActionListener(e -> {
            int returnVal = fileChooser.showOpenDialog(mainPanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                f = fileChooser.getSelectedFile();
                backButton.setEnabled(true);
                forwardButton.setEnabled(true);

                try {
                    image = ImageIO.read(f);

                } catch (IOException e1) {
                    e1.printStackTrace();
                    System.exit(-1);
                }
                actionCounter = 0;
                iconHistoric.clear();
                repaintMap();
                map.buildMap(ImageManager.parseImageToMap(image));
                planner = new DynamicProgrammingAlgorithm(map);
                System.out.println(map.parseString());

            }
        });
        mainPanel.setFocusable(true);
        frame.setFocusable(true);
        mainLabel.setFocusable(true);
        mainPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int keyCode = e.getKeyCode();

                switch (keyCode) {
                    case KeyEvent.VK_LEFT:
                        backButton.doClick();
                        break;

                    case KeyEvent.VK_RIGHT:
                        forwardButton.doClick();
                        break;
                }
            }
        });

        map = new RegularGridMap(cellSize);
        frame.pack();
        frame.setVisible(true);
        fileChooser.setCurrentDirectory(new File("./maps"));
        iconHistoric = new ArrayList<>();
    }

    private void repaintMap() {
        switch (actionCounter) {
            case 0:
                showImage();
                break;
            case 1:
                showGrid();
                break;
            case 2:
                showCSpace();
                planner.executePlanner();
                break;
            case 3:
                showPlanResults();
                break;
            case 4:
                showFinalPath();
                break;
        }
        frame.pack();
        System.out.println("actionCounter: " + actionCounter);
    }

    private void showFinalPath() {
        BufferedImage img;
        if (iconHistoric.size() < 5) {
            img = ImageManager.drawPath(map, planner, planner.computePath());
            iconHistoric.add(img);
        } else
            img = iconHistoric.get(actionCounter);
        setImageAndRepaint(img);
    }

    private void showPlanResults() {
        BufferedImage img;
        if (iconHistoric.size() < 4) {
            img = ImageManager.drawPlanNumbers(ImageManager.parseMapToImage(map), planner, cellSize);
            iconHistoric.add(img);
        } else
            img = iconHistoric.get(actionCounter);
        setImageAndRepaint(img);
    }

    private void showCSpace() {
        BufferedImage img;
        if (iconHistoric.size() < 3) {
            img = ImageManager.gridImage(ImageManager.parseMapToImage(map),cellSize);
            iconHistoric.add(img);
        } else
            img = iconHistoric.get(actionCounter);
        setImageAndRepaint(img);
    }

    private void showGrid() {
        BufferedImage img;
        if (iconHistoric.size() < 2) {
            img = ImageManager.gridImage(image, cellSize);
            iconHistoric.add(img);
        } else
            img = iconHistoric.get(actionCounter);
        setImageAndRepaint(img);
    }

    private void showImage() {
        BufferedImage img;
        if (iconHistoric.size() < 1) {
            img = ImageManager.gridImage(image, cellSize);
            iconHistoric.add(img);
        } else
            img = iconHistoric.get(actionCounter);
        setImageAndRepaint(img);

        setImageAndRepaint(image);
    }

    private void setImageAndRepaint(BufferedImage img) {
        mainLabel.setIcon(new ImageIcon(img));
        mainLabel.validate();
        mainLabel.repaint();

    }
}
