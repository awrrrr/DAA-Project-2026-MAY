public class TerrainMap {
    private int[][] grid;

    public TerrainMap(int[][] grid) {
        this.grid = grid;
    }

    public static TerrainMap createCityMap() {
        // Mostly roads (0s) with a few obstacles (-1)
        int[][] cityGrid = {
            { 0,  0,  0,  0,  0,  0,  0},
            { 0, -1, -1,  0,  0, -1,  0},
            { 0,  0,  0, -1,  0, -1,  0},
            { 0, -1,  0,  0,  0,  0,  0},
            { 0, -1,  0, -1, -1,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0}
        };
        return new TerrainMap(cityGrid);
    }

    public static TerrainMap createForestMap() {
        // Mix of grass (2) and gravel (1), no roads
        int[][] forestGrid = {
            { 2,  2,  1,  1,  2,  2,  2},
            { 2,  2,  2,  1,  2,  1,  2},
            { 1,  1,  2,  2,  2,  1,  1},
            { 2,  1,  1,  2,  1,  2,  2},
            { 2,  2,  2,  2,  2,  2,  2},
            { 1,  2,  2,  1,  1,  2,  2}
        };
        return new TerrainMap(forestGrid);
    }

    public static TerrainMap createTrappedMap() {
        int[][] trappedGrid = {
            { 0,  0,  0, -1,  0},
            { 0,  0,  0, -1,  0},
            { 0,  0,  0, -1,  0},
            {-1, -1, -1, -1,  0},
            { 0,  0,  0,  0,  0}
        };
        return new TerrainMap(trappedGrid);
    }

    public void printGrid() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.printf("%3d ", grid[i][j]);
            }
            System.out.println();
        }
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getRows() {
        return grid.length;
    }

    public int getCols() {
        if (grid.length == 0) return 0;
        return grid[0].length;
    }

    public double getTerrainCost(int x, int y) {
        if (x < 0 || x >= getRows() || y < 0 || y >= getCols()) {
            return Double.POSITIVE_INFINITY;
        }
        
        int terrainType = grid[x][y];
        switch (terrainType) {
            case 0: return 1.0;
            case 1: return 1.5;
            case 2: return 2.5;
            case 3: return 5.0;
            case -1: return Double.POSITIVE_INFINITY;
            default: return Double.POSITIVE_INFINITY;
        }
    }

    public boolean isWalkable(int x, int y) {
        return !Double.isInfinite(getTerrainCost(x, y));
    }
}
