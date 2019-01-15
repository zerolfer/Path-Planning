package interf;

import map.RegularGridMap;
import plan.DynamicProgrammingAlgorithm;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;

public class MainWindow {
    private JFrame frame;
    private JButton backButton;
    private JPanel mainPanel;
    private JButton forwardButton;
    private JButton browseButton;
    private JLabel mainLabel;
    private JCheckBox showNumbersCheckBox;
    private JSpinner numColumsSpinner;

    final JFileChooser fileChooser = new JFileChooser();

    private int actionCounter = 0;
    private File f;
    private BufferedImage image;

    private final int numActions = 4;
//    int numColumns = 128;
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


        forwardButton.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), "MOVE_FORWARD");
        forwardButton.getActionMap().put("MOVE_FORWARD", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (actionCounter <= numActions) actionCounter++;
                repaintMap();
            }
        });
        backButton.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), "MOVE_BACK");
        backButton.getActionMap().put("MOVE_BACK", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (actionCounter > 0) actionCounter--;
                repaintMap();
            }
        });


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
                map = new RegularGridMap(image.getWidth() / (int)numColumsSpinner.getValue());
                repaintMap();
                map.buildMap(ImageManager.parseImageToMap(image));
                planner = new DynamicProgrammingAlgorithm(map);
                System.out.println(map.parseString());

            }
        });
        fileChooser.setCurrentDirectory(new File("./maps"));
        iconHistoric = new ArrayList<>();

        ChangeListener changeListener = e -> {
            iconHistoric.clear();
            actionCounter = 0;
        };

        showNumbersCheckBox.addChangeListener(changeListener);
        mainPanel.setFocusable(true);
        frame.setFocusable(true);
        mainLabel.setFocusable(true);
        numColumsSpinner.addChangeListener(changeListener);
        numColumsSpinner.setModel(new SpinnerListModel(new Integer[]{2,4,8,16,32,64,128,256,512,1024,2048}));
        numColumsSpinner.setValue(64);

        frame.pack();
        frame.setVisible(true);
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
                if (showNumbersCheckBox.isSelected()) {
                    showPlanResults();
                    break;
                }
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
            if (showNumbersCheckBox.isSelected())
                img = ImageManager.drawPlanNumbers(ImageManager.drawPath(map, planner, planner.computePath()),
                        planner, map.getCellSize());
            else
                img = ImageManager.drawPath(map, planner, planner.computePath());

            iconHistoric.add(img);
        } else
            img = iconHistoric.get(actionCounter);
        setImageAndRepaint(img);
    }

    private void showPlanResults() {
        BufferedImage img;
        if (iconHistoric.size() < 4) {
            img = ImageManager.drawPlanNumbers(ImageManager.parseMapToImage(map), planner, map.getCellSize());
            iconHistoric.add(img);
        } else
            img = iconHistoric.get(actionCounter);
        setImageAndRepaint(img);
    }

    private void showCSpace() {
        BufferedImage img;
        if (iconHistoric.size() < 3) {
            img = ImageManager.gridImage(ImageManager.parseMapToImage(map), map.getCellSize());
            iconHistoric.add(img);
        } else
            img = iconHistoric.get(actionCounter);
        setImageAndRepaint(img);
    }

    private void showGrid() {
        BufferedImage img;
        if (iconHistoric.size() < 2) {
            img = ImageManager.gridImage(image, map.getCellSize());
            iconHistoric.add(img);
        } else
            img = iconHistoric.get(actionCounter);
        setImageAndRepaint(img);
    }

    private void showImage() {
        BufferedImage img;
        if (iconHistoric.size() < 1) {
            img = ImageManager.gridImage(image, map.getCellSize());
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
