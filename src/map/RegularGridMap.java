package map;

import util.Coordinate;

public class RegularGridMap implements SpaceMap {

    private MapElements[][] grid;
    private Coordinate goal;
    private Coordinate start;
    private int cellSize = 2; // n x n pixels, use powers of 2 numbers (1, 2, 4, 8, 16...)

    public MapElements[][] getGrid() {
        return grid;
    }

    public RegularGridMap(int cellSize) {
        this.cellSize = cellSize;
    }

    public Coordinate getGoal() {
        return goal;
    }

    public Coordinate getStart() {
        return start;
    }

    public RegularGridMap() {
    }

    @Override
    public void buildMap(MapElements[][] img) {
        grid = new MapElements[img.length / cellSize][img[0].length / cellSize];

        int xCount = 0, yCount = 0;
        int x = 0, y = 0;
        for (int i = 0; i < img.length; i++) {
            for (int j = 0; j < img[0].length; j++) {

                // if it is not empty, save the data and finish the iteration
                if (img[i][j].equals(MapElements.GOAL)) {
                    this.goal = new Coordinate(x, y);
                    if (grid[x][y] == null) grid[x][y] = MapElements.WHITE; // only if not assigned yet
                } else if (img[i][j].equals(MapElements.START)) {
                    this.start = new Coordinate(x, y);
                    if (grid[x][y] == null) grid[x][y] = MapElements.WHITE; // only if not assigned yet
                } else if (img[i][j].equals(MapElements.BLACK)) {
                    grid[x][y] = MapElements.BLACK; // if any black, always the cell is black
                } else {
                    if (grid[x][y] == null) grid[x][y] = MapElements.WHITE;
                }

                yCount++;
                if (yCount >= this.cellSize) {
                    y++;
                    yCount = 0;
                }
            }
            y = 0;
            xCount++;
            if (xCount >= this.cellSize) {
                x++;
                xCount = 0;
            }
        }
    }

    @Override
    public String parseString() {
        StringBuffer bf = new StringBuffer();
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                if (grid[x][y].equals(MapElements.BLACK))
                    bf.append('#');
                if (grid[x][y].equals(MapElements.WHITE))
                    bf.append('*');
                if (grid[x][y].equals(MapElements.START))
                    bf.append('S');
                if (grid[x][y].equals(MapElements.GOAL))
                    bf.append('G');
            }
            bf.append("\n");
        }
        return bf.toString();
    }

    public int getCellSize() {
        return this.cellSize;
    }

    public MapElements getCell(Coordinate c) {
        return this.getGrid()[c.getX()][c.getY()];
    }

    public boolean isFree(Coordinate c) {
        return isFree(c.getX(), c.getY());
    }

    public boolean isFree(int x, int y) {
        return grid[x][y] != MapElements.BLACK;
    }
}
