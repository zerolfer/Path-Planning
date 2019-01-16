package interf;

import map.RegularGridMap;
import plan.AStarAlgorithm;
import plan.PlanAlgorithm;
import plan.WavefrontAlgorithm;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
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
    private JComboBox comboBox1;

    final JFileChooser fileChooser = new JFileChooser();

    private int actionCounter = 0;
    private File f;
    private BufferedImage image;

    private final int numActions = 4;
    //    int numColumns = 128;
    RegularGridMap map;
    PlanAlgorithm planner;
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
                map = new RegularGridMap(image.getWidth() / (int) numColumsSpinner.getValue());
                repaintMap();
                map.buildMap(ImageManager.parseImageToMap(image));

                comboBox1.setModel(new DefaultComboBoxModel(
                        new PlanAlgorithm[]{new WavefrontAlgorithm(map), new AStarAlgorithm(map)})
                );
                comboBox1.setSelectedIndex(0);


                planner = (PlanAlgorithm) comboBox1.getSelectedItem();
                System.out.println(map.parseString());

            }
        });
        fileChooser.setCurrentDirectory(new File("./maps"));
        iconHistoric = new ArrayList<>();

        ChangeListener changeListener = e -> {
            if (image != null && map != null) {
                iconHistoric.clear();
                actionCounter = 0;
                map = new RegularGridMap(image.getWidth() / (Integer) numColumsSpinner.getValue());
                map.buildMap(ImageManager.parseImageToMap(image));
                repaintMap();
            }
        };

        showNumbersCheckBox.addChangeListener(changeListener);
        mainPanel.setFocusable(true);
        frame.setFocusable(true);
        mainLabel.setFocusable(true);
        numColumsSpinner.addChangeListener(changeListener);
        numColumsSpinner.setModel(new SpinnerListModel(new Integer[]{2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048}));
        numColumsSpinner.setValue(64);

        frame.pack();
        frame.setVisible(true);
        comboBox1.addActionListener(e -> {
            planner = (PlanAlgorithm) comboBox1.getSelectedItem();
            actionCounter = 0;
            iconHistoric.clear();
            repaintMap();
        });
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
                if (iconHistoric.size() < 3 || map.getGrid() == null)
                    map.buildMap(ImageManager.parseImageToMap(image));
                showCSpace();
                break;
            case 3:
                if (iconHistoric.size() < 4)
                    planner.executePlanner();
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
