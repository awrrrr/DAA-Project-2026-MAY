import java.util.*;

public class AStarSolver {
    private TerrainMap map;
    private int visitedNodesCount = 0;

    public AStarSolver(TerrainMap map) {
        this.map = map;
    }

    public int getVisitedNodesCount() {
        return visitedNodesCount;
    }

    public List<Node> findPath(int startX, int startY, int goalX, int goalY) {
        if (!map.isWalkable(startX, startY) || !map.isWalkable(goalX, goalY)) {
            visitedNodesCount = 0;
            return null;
        }

        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fCost));
        boolean[][] closedSet = new boolean[map.getRows()][map.getCols()];
        
        // Track the best gCost for each cell to avoid duplicate processing with worse paths
        double[][] gCostMap = new double[map.getRows()][map.getCols()];
        for (double[] row : gCostMap) {
            Arrays.fill(row, Double.POSITIVE_INFINITY);
        }

        Node startNode = new Node(startX, startY);
        startNode.gCost = 0;
        startNode.hCost = calculateHeuristic(startX, startY, goalX, goalY);
        startNode.fCost = startNode.gCost + startNode.hCost;

        openList.add(startNode);
        gCostMap[startX][startY] = 0;
        visitedNodesCount = 0;

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        while (!openList.isEmpty()) {
            Node current = openList.poll();
            visitedNodesCount++;

            if (current.x == goalX && current.y == goalY) {
                return reconstructPath(current);
            }

            closedSet[current.x][current.y] = true;

            for (int i = 0; i < 4; i++) {
                int nx = current.x + dx[i];
                int ny = current.y + dy[i];

                if (nx >= 0 && nx < map.getRows() && ny >= 0 && ny < map.getCols()) {
                    if (closedSet[nx][ny]) continue;

                    double terrainCost = map.getTerrainCost(nx, ny);
                    if (Double.isInfinite(terrainCost)) continue; // Obstacle

                    double tentativeGCost = current.gCost + (1.0 * terrainCost);

                    if (tentativeGCost < gCostMap[nx][ny]) {
                        Node neighbor = new Node(nx, ny);
                        neighbor.gCost = tentativeGCost;
                        neighbor.hCost = calculateHeuristic(nx, ny, goalX, goalY);
                        neighbor.fCost = neighbor.gCost + neighbor.hCost;
                        neighbor.parent = current;

                        gCostMap[nx][ny] = tentativeGCost;
                        openList.add(neighbor);
                    }
                }
            }
        }
        return null; // No path found
    }

    private double calculateHeuristic(int x1, int y1, int x2, int y2) {
        // Manhattan distance
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    public void printResult(List<Node> path, int startX, int startY, int goalX, int goalY) {
        if (path == null || path.isEmpty()) {
            System.out.println("No path found!");
            return;
        }

        int rows = map.getRows();
        int cols = map.getCols();
        char[][] displayGrid = new char[rows][cols];
        
        int[][] rawGrid = map.getGrid();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (rawGrid[i][j] == -1) {
                    displayGrid[i][j] = '#';
                } else {
                    displayGrid[i][j] = '.';
                }
            }
        }

        for (Node n : path) {
            displayGrid[n.x][n.y] = '*';
        }

        displayGrid[startX][startY] = 'S'; // override start
        displayGrid[goalX][goalY] = 'E'; // override end

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (displayGrid[i][j] == 'S') {
                    System.out.print("[S]");
                } else if (displayGrid[i][j] == 'E') {
                    System.out.print("[E]");
                } else if (displayGrid[i][j] == '*') {
                    System.out.print("[*]");
                } else {
                    System.out.print(" " + displayGrid[i][j] + " ");
                }
            }
            System.out.println();
        }

        Node endNode = path.get(path.size() - 1);
        System.out.println();
        System.out.printf("Total Travel Time: %.2f\n", endNode.gCost);
        System.out.println("Total Nodes Visited: " + visitedNodesCount);
        System.out.println("Path Length: " + (path.size() - 1) + " steps");
    }
}
