# Mini Project: Java Console Path Planner (A* Algorithm)

## Overview
A Java-based console application that calculates the fastest path between two points on a grid. The grid represents different terrain types (Road, Grass, Sand) that affect travel time. We will use the **A* Search Algorithm** to find the optimal route based on predefined "Time Costs" for each surface.

---

## Core Features
- **Java Only**: No external libraries (like Matplotlib). Uses standard Java `java.util` classes.
- **Console Interface**: Input and output are handled entirely in the terminal.
- **Predefined Grids**: Hardcoded 2D arrays representing different map scenarios.
- **Time-Based Logic**: Instead of just distance, we calculate "Travel Time" based on road quality.

---

## Terrain & Time Approximations
Each cell in the grid has a "Surface Factor" that multiplies the time taken to cross it:
- **Paved Road**: 1.0x (Baseline speed)
- **Gravel/Dirt**: 1.5x (Slower)
- **Grass**: 2.5x (Much slower)
- **Sand**: 5.0x (Very difficult)
- **Obstacle**: ∞ (Blocked)

---

## Simplified Implementation Plan

### Phase 1: Grid & Data Structure (Week 1)
- Create a `Map` class that holds a 2D `int` array for terrain types.
- Define a `Node` class to store coordinates, costs ($G, H, F$), and parent references.
- Hardcode 2-3 sample grids for testing (e.g., a "City Map" with roads vs. a "Forest Map" with grass).

### Phase 2: A* Implementation (Week 2)
- Implement the A* logic using Java's `PriorityQueue` for the "Open List".
- Calculate:
    - **G Cost**: Time taken from start to current node (Distance × Surface Factor).
    - **H Cost**: Heuristic distance to the goal (Euclidean or Manhattan).
    - **F Cost**: $G + H$.

### Phase 3: Path Visualization (Week 3)
- Create a simple function to print the grid to the console.
- Use ASCII characters to show the path (e.g., `.` for empty, `#` for obstacle, `*` for the path).
- Print metrics: Total Time Taken, Total Nodes Visited, and Path Length.

### Phase 4: Final Report (Week 4)
- Document the code structure.
- Explain how the "Surface Factor" changes the path choice (e.g., the algorithm choosing a longer road path over a shorter grass path because it's faster).

---

## Project Structure
- `Main.java`: The entry point that runs the demo scenarios.
- `AStarSolver.java`: The core logic for pathfinding.
- `TerrainMap.java`: Stores the grid and provides terrain costs.
- `Node.java`: Helper class for the algorithm.

---

## Example Console Output
```text
Map Scenario: Emergency Rescue
S . . G G G
R R R R R .
. . . . R E

Calculating path...
Path Found! 
Total Time: 12.5 seconds
Visual Path:
[S] . . G G G
[*] [*] [*] [*] [*] .
. . . . [*] [E]
(Legend: S=Start, E=End, R=Road, G=Grass, *=Path)
```
