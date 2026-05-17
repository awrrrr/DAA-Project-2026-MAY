import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("====== A* SEARCH ALGORITHM TEST CASES ======\n");

        TerrainMap cityMap = TerrainMap.createCityMap();
        TerrainMap forestMap = TerrainMap.createForestMap();

        System.out.println("--- TEST CASE 1: City Map (Standard Path) ---");
        System.out.println("Scenario: Navigate through city roads (cost 1.0), avoiding buildings (-1).");
        runTestCase(cityMap, 0, 0, 5, 6);

        System.out.println("\n--- TEST CASE 2: Forest Map (Terrain Cost Optimization) ---");
        System.out.println("Scenario: Reach destination while prioritizing faster gravel (cost 1.5) over slow grass (cost 2.5).");
        runTestCase(forestMap, 0, 0, 5, 6);

        System.out.println("\n--- TEST CASE 3: Short Trip (City Map) ---");
        System.out.println("Scenario: A very short distance to ensure the algorithm handles close proximity correctly.");
        runTestCase(cityMap, 0, 0, 2, 2);

        System.out.println("\n--- TEST CASE 4: Unreachable Goal (Blocked Path) ---");
        System.out.println("Scenario: Attempt to reach a goal completely cut off by obstacles.");
        int[][] trappedGrid = {
            { 0,  0,  0, -1,  0},
            { 0,  0,  0, -1,  0},
            { 0,  0,  0, -1,  0},
            {-1, -1, -1, -1,  0},
            { 0,  0,  0,  0,  0}
        };
        TerrainMap trappedMap = new TerrainMap(trappedGrid);
        runTestCase(trappedMap, 0, 0, 0, 4);
    }

    private static void runTestCase(TerrainMap map, int startX, int startY, int goalX, int goalY) {
        AStarSolver solver = new AStarSolver(map);
        List<Node> path = solver.findPath(startX, startY, goalX, goalY);
        solver.printResult(path, startX, startY, goalX, goalY);
    }
}
