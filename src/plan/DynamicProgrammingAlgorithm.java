package plan;

import map.MapElements;
import map.RegularGridMap;
import util.Coordinate;

import java.util.*;

public class DynamicProgrammingAlgorithm implements PlanAlgorithm {

    private final Coordinate goal;
    private final double[][] weights;
    private final RegularGridMap map;

    public DynamicProgrammingAlgorithm(RegularGridMap map) {
        this.goal = map.getGoal();
        this.map = map;
        this.weights = new double[map.getGrid().length][map.getGrid()[0].length];
    }

    @Override
    public void executePlanner() {
//        List<Coordinate> previousWavefront = new ArrayList<>();
//        previousWavefront.add(goal);
        Queue<Coordinate> wavefront = new LinkedList<>();
//        double i = 0;
//        for (Coordinate c : previousWavefront) {
//            weights[c.getX()][c.getY()] = i;
//
//        }
//        Coordinate parent = goal;
//        wavefront.push(getChildren(goal));
        wavefront.add(goal);
        Set visited = new HashSet();
        Queue<Coordinate> parents = new LinkedList<>();
        Coordinate currentParent = goal;
        int i = 8;
        while (!wavefront.isEmpty()) {
            Coordinate node = wavefront.poll();
//            if (node.getX() == start.getX() && node.getY() == goal.getY())
//                return;

            weights[node.getX()][node.getY()] = weights[currentParent.getX()][currentParent.getY()] +
                    Math.sqrt(Math.pow(currentParent.getX() - node.getX(), 2) + Math.pow(currentParent.getY() - node.getY(), 2));

            Stack<Coordinate> children = getChildren(node);
            for (Coordinate child : children) {
                if (!visited.contains(child) && !wavefront.contains(child)
                        && !map.getCell(child).equals(MapElements.BLACK))
                    wavefront.add(child);
            }
            visited.add(node);
            parents.add(node);
            if (i >= 8) {
                currentParent = parents.poll();
                i = 0;
            }
            ++i;
        }
        System.out.println('g');
//        while fringe not empty
//        node:=first element of fringe
//        if node is what we are searching for
//        return success
//        endif
        //do whatever you need to do to node here
//        children:=find children of node in graph
//        add children not in visited to back of fringe
//        add node to visited
//        remove node from fringe
//        end while


    }

    private Stack<Coordinate> getChildren(Coordinate parent) {
        Stack<Coordinate> result = new Stack<>();
        int x = parent.getX(), y = parent.getY();
        for (int xshift = -1; xshift <= 1; xshift++) {
            for (int yshift = -1; yshift <= 1; yshift++) {
                if (xshift == 0 && yshift == 0) continue;
                try {
                    double a = weights[x + xshift][y + yshift];
                    result.push(new Coordinate(x + xshift, y + yshift));
                } catch (IndexOutOfBoundsException ex) {
                }
            }
        }
        return result;
    }

    @Override
    public List<Coordinate> getPath() {
        return null;
    }

    public double[][] getWeights() {
        return this.weights;
    }
}
