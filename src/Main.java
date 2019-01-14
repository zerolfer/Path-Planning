import interf.ImageManager;
import map.MapElements;
import map.RegularGridMap;
import plan.DynamicProgrammingAlgorithm;
import plan.PlanAlgorithm;

import java.awt.image.BufferedImage;

public class Main {
    public static void main(String[] args){

        // load image
        BufferedImage img = ImageManager.readImage("map2");
        MapElements[][] tiles = ImageManager.parseImageToMap(img);
        ImageManager.showImage(img);

        // Map representation
        int cellSize=16;
        RegularGridMap map = new RegularGridMap(cellSize);
        map.buildMap(tiles);
        System.out.println(map.parseString());
        ImageManager.showImageFromMap(map);
        ImageManager.showGridedImage(img, cellSize);
        ImageManager.showGridedImage(ImageManager.parseMapToImage(map), cellSize);

        // Planning
        DynamicProgrammingAlgorithm planner = new DynamicProgrammingAlgorithm(map);
        planner.executePlanner();

        ImageManager.showPlanNumbers(ImageManager.parseMapToImage(map), planner, cellSize);

    }
}
