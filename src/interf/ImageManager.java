package interf;

import map.MapElements;
import map.RegularGridMap;
import plan.DynamicProgrammingAlgorithm;
import util.Coordinate;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import static map.MapElements.*;

public class ImageManager {

    private static final String PATH = "maps/";

    public static BufferedImage readImage(String name) {

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(PATH + name + ".png"));
        } catch (IOException e) {
            System.err.println("Error al cargar la imagen");
            System.exit(1);
        }
        return img;
    }

    public static MapElements[][] parseImageToMap(BufferedImage image) {
        int error = 2;
        MapElements[][] result = new MapElements[image.getWidth()][image.getHeight()];
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                result[x][y] = BLACK; // if it doesen't match, it will be treated as obstacle
                final int clr = image.getRGB(x, y);
                Color color = new Color(image.getRGB(x, y));

                if (color.equals(Color.BLACK))
                    result[x][y] = BLACK;
                if (color.equals(Color.WHITE))
                    result[x][y] = WHITE;
                if (color.equals(Color.RED)) {
                    result[x][y] = START;
                    error--;
                }
                if (color.equals(Color.GREEN)) {
                    result[x][y] = GOAL;
                    error--;
                }

            }
        }
        if (error > 0) System.err.println("THE MAP HAS NOT GOAL/START MARKS");
        return result;
    }

    public static void showImage(BufferedImage img) {
        Graphics2D g = (Graphics2D) img.getGraphics();
        JFrame frame = new JFrame();
        ImageIcon icon = new ImageIcon(img);
        JLabel label = new JLabel(icon);
        frame.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
    }

    public static BufferedImage parseMapToImage(RegularGridMap map) {
        MapElements[][] grid = map.getGrid();
        int cellSize = map.getCellSize();
        int width = grid.length * cellSize;
        int height = grid[0].length * cellSize;
        final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) img.getGraphics();

        int xCount = 0, yCount = 0;
        int x = 0, y = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color color;
                switch (grid[x][y]) {
                    case BLACK:
                        color = Color.BLACK;
                        break;
                    case WHITE:
                        color = Color.WHITE;
                        break;
//                    case GOAL:
//                        color = Color.RED;
//                        break;
//                    case START:
//                        color = Color.GREEN;
//                        break;
                    default:
                        color = Color.MAGENTA;
                }

                if (x == map.getStart().getX() && y == map.getStart().getY())
                    color = Color.GREEN;
                if (x == map.getGoal().getX() && y == map.getGoal().getY())
                    color = Color.RED;

                g.setColor(color);
                g.fillRect(i, j, cellSize, cellSize);

                yCount++;
                if (yCount >= cellSize) {
                    y++;
                    yCount = 0;
                }
            }
            y = 0;
            xCount++;
            if (xCount >= cellSize) {
                x++;
                xCount = 0;
            }
        }

        return img;
    }


    public static void showImageFromMap(RegularGridMap map) {
        showImage(parseMapToImage(map));
    }

    public static void showGridedImage(BufferedImage image, int cellSize) {
        showImage(gridImage(image, cellSize));
    }

    public static BufferedImage gridImage(BufferedImage image, int cellSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, image.getType());

        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.setColor(Color.GRAY);

        int xstart = 0;
        for (int i = 0; i < width; i++) {
            xstart = i * cellSize;
            g.drawLine(xstart, 0, xstart, height);
        }
        int ystart = 0;
        for (int j = 0; j < height; j++) {
            ystart = j * cellSize;
            g.drawLine(0, ystart, width, ystart);
        }
        return newImage;
    }

    public static void showPlanNumbers(BufferedImage image, DynamicProgrammingAlgorithm planner, int cellSize) {
        showImage(drawPlanNumbers(image, planner, cellSize));

    }

    public static BufferedImage drawPlanNumbers(BufferedImage image, DynamicProgrammingAlgorithm planner, int cellSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage newImage = gridImage(image, cellSize);

        Graphics2D g = (Graphics2D) newImage.getGraphics();
//        g.drawImage(image, 0, 0, null);
        g.setColor(Color.GRAY);
        Font font = new Font("Serif", Font.PLAIN, 20);
        DecimalFormat df = new DecimalFormat("#.##");
        g.setFont(font);

//        int i = numColumns / 2, j = numColumns / 2;
        for (int x = 0, i = cellSize / 4; x < planner.getDistToGoal().length; x++, i += cellSize) {
            for (int y = 0, j = cellSize / 2; y < planner.getDistToGoal()[0].length; y++, j += cellSize) {
                double n = planner.getDistToGoal()[x][y];
                String s = n >= Double.MAX_VALUE ? "inf" : df.format(n);
                g.drawString(s, i, j);
//                j += numColumns;
            }
//            i += numColumns;
        }
        return newImage;
    }

    public static void showPath(RegularGridMap map, DynamicProgrammingAlgorithm planner, List<Coordinate> path) {
        showImage(drawPath(map, planner, path));

    }

    public static BufferedImage drawPath(RegularGridMap map, DynamicProgrammingAlgorithm planner, List<Coordinate> path) {
        MapElements[][] grid = map.getGrid();
        int cellSize = map.getCellSize();
        int width = grid.length * cellSize;
        int height = grid[0].length * cellSize;
        final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) img.getGraphics();

        int xCount = 0, yCount = 0;
        int x = 0, y = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Color color;
                switch (grid[x][y]) {
                    case BLACK:
                        color = Color.BLACK;
                        break;
                    case WHITE:
                        color = Color.WHITE;
                        break;
//                    case GOAL:
//                        color = Color.RED;
//                        break;
//                    case START:
//                        color = Color.GREEN;
//                        break;
                    default:
                        color = Color.MAGENTA;
                }
                if (path.contains(new Coordinate(x, y)))
                    color = Color.yellow;

                if (x == map.getStart().getX() && y == map.getStart().getY())
                    color = Color.GREEN;
                if (x == map.getGoal().getX() && y == map.getGoal().getY())
                    color = Color.RED;

                g.setColor(color);
                g.fillRect(i, j, cellSize, cellSize);

                yCount++;
                if (yCount >= cellSize) {
                    y++;
                    yCount = 0;
                }
            }
            y = 0;
            xCount++;
            if (xCount >= cellSize) {
                x++;
                xCount = 0;
            }
        }
        return gridImage(img, cellSize);
    }
}
