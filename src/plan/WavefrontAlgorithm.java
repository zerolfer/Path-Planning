package plan;

import map.MapElements;
import map.RegularGridMap;
import util.Coordinate;

import java.util.*;

public class WavefrontAlgorithm implements PlanAlgorithm {

    private Coordinate goal;
    private Coordinate start;
    private double[][] distToGoal;
    private RegularGridMap map;

    private Map<Coordinate, Double> moveCost = new HashMap<>();

    public WavefrontAlgorithm(RegularGridMap map) {
        this.goal = map.getGoal();
        this.start = map.getStart();
        this.map = map;
        this.distToGoal = new double[map.getGrid().length][map.getGrid()[0].length];
        for (int i = 0; i < distToGoal.length; i++) {
            for (int j = 0; j < distToGoal[0].length; j++) {
                if (!goal.equals(i, j))
                    this.distToGoal[i][j] = Double.MAX_VALUE;
            }
        }
        double r2 = Math.sqrt(2);
        moveCost.put(new Coordinate(0, -1), 1d);
        moveCost.put(new Coordinate(0, +1), 1d);
        moveCost.put(new Coordinate(+1, 0), 1d);
        moveCost.put(new Coordinate(-1, 0), 1d);
        moveCost.put(new Coordinate(-1, -1), r2);
        moveCost.put(new Coordinate(+1, +1), r2);
        moveCost.put(new Coordinate(+1, -1), r2);
        moveCost.put(new Coordinate(-1, +1), r2);
    }

    @Override
    public void executePlanner() {

        Queue<Coordinate> wavefront = new LinkedList<>();
        wavefront.add(goal);
        Set<Coordinate> visited = new HashSet<>();
        Queue<Coordinate> parents = new LinkedList<>();
        Coordinate currentParent = goal;
        int i = 8;
        while (!wavefront.isEmpty()) {
            Coordinate node = wavefront.poll();
//            if (node.getX() == start.getX() && node.getY() == goal.getY())
//                return;
            if (!node.equals(goal)) {
                // seach the minimun value of the neighbours of the current node
                int dx = 0, dy = 0;
                double minCost = Double.MAX_VALUE;
                for (Coordinate delta : moveCost.keySet()) {
                    try {
                        double cost = distToGoal[node.getX() + delta.getX()][node.getY() + delta.getY()];
                        if (minCost > cost) {
                            dx = delta.getX();
                            dy = delta.getY();
                            minCost = cost;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        // continue
                    }
                }

                // once we have the mininum dx and dy, compute the cost:
                distToGoal[node.getX()][node.getY()] =
                        moveCost.get(new Coordinate(dx, dy)) + distToGoal[node.getX() + dx][node.getY() + dy];
            }

//            distToGoal[node.getX()][node.getY()] = distToGoal[currentParent.getX()][currentParent.getY()] +
//                    Math.sqrt(Math.pow(currentParent.getX() - node.getX(), 2) + Math.pow(currentParent.getY() - node.getY(), 2));

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
        System.out.println("Planner executed");
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
                    double a = distToGoal[x + xshift][y + yshift];
                    result.push(new Coordinate(x + xshift, y + yshift));
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }
        return result;
    }

    /**
     * Greedy algorithm used after call the fuction <code><a href=executePlanner>executePlanner</a></code>
     *
     * @return the path form starting point to goal point based on the wavefront-based planning algorithm
     * in
     */
    @Override
    public List<Coordinate> computePath() {
        List<Coordinate> path = new ArrayList<>();
//        path.add(start);
        path.add(computePathRecursive(path, start));
        System.out.println(path);
//        for (int x =0;x<distToGoal.length;x++){
//            for(int y=0;y<distToGoal[0].length;y++){
//                Coordinate nextStep = findMinNeighbour(distToGoal[x][y]);
//                path.add(nextStep);
//                Coordinate current=nextStep;

//            }
//        }
        return path;
    }

    private Coordinate computePathRecursive(List<Coordinate> path, Coordinate current) {
        if (!current.equals(goal)) {
            Coordinate nextStep = findMinNeighbour(current);
            path.add(current);
            return computePathRecursive(path, nextStep);
        } else return current;

    }

    private Coordinate findMinNeighbour(Coordinate pivot) {
        Coordinate minCoordinate = pivot;
        double minWeight = Double.MAX_VALUE;

        for (int xshift = -1; xshift <= 1; xshift++) {
            for (int yshift = -1; yshift <= 1; yshift++) {
                if (xshift == 0 && yshift == 0) continue;
                try {
                    double weight = distToGoal[pivot.getX() + xshift][pivot.getY() + yshift];
                    if (weight < minWeight) {
                        minWeight = weight;
                        minCoordinate = new Coordinate(pivot.getX() + xshift, pivot.getY() + yshift);
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }

            }
        }
        return minCoordinate;
    }

    public double[][] getDistToGoal() {
        return this.distToGoal;
    }

    @Override
    public void reset(RegularGridMap map) {
        this.map=map;
        this.goal=map.getGoal();
        this.start=map.getStart();
        this.distToGoal = new double[this.map.getGrid().length][this.map.getGrid()[0].length];
        for (int i = 0; i < distToGoal.length; i++) {
            for (int j = 0; j < distToGoal[0].length; j++) {
                if (!goal.equals(i, j))
                    this.distToGoal[i][j] = Double.MAX_VALUE;
            }
        }
    }

    /**
     * THIS IS FOR THE USER INTERFACE JCOMBOBOX
     */
    @Override
    public String toString() {
        return "Wavefront Algorithm";
    }
}
