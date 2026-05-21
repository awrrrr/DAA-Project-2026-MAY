const maps = {
    city: {
        grid: [
            [0, 0, 0, 0, 0, 0, 0],
            [0, -1, -1, 0, 0, -1, 0],
            [0, 0, 0, -1, 0, -1, 0],
            [0, -1, 0, 0, 0, 0, 0],
            [0, -1, 0, -1, -1, 0, 0],
            [0, 0, 0, 0, 0, 0, 0]
        ],
        start: { x: 0, y: 0 },
        goal: { x: 5, y: 6 }
    },
    forest: {
        grid: [
            [2, 3, 1, 1, 2, 2, 2],
            [2, 3, 2, 1, 2, 1, 2],
            [1, 3, 2, 2, 3, 1, 1],
            [2, 1, 3, 2, 1, 2, 2],
            [2, 2, 2, 3, 2, 2, 2],
            [1, 2, 2, 1, 1, 2, 2]
        ],
        start: { x: 0, y: 0 },
        goal: { x: 5, y: 6 }
    },
    trapped: {
        grid: [
            [0, 0, 0, -1, 0],
            [0, 0, 0, -1, 0],
            [0, 0, 0, -1, 0],
            [-1, -1, -1, -1, 0],
            [0, 0, 0, 0, 0]
        ],
        start: { x: 0, y: 0 },
        goal: { x: 0, y: 4 }
    }
};

const gridEl = document.getElementById("grid");
const mapSelect = document.getElementById("mapSelect");
const modeButtons = document.querySelectorAll(".mode");
const runButton = document.getElementById("runButton");
const resetButton = document.getElementById("resetButton");
const statusEl = document.getElementById("status");
const startLabel = document.getElementById("startLabel");
const goalLabel = document.getElementById("goalLabel");
const travelTime = document.getElementById("travelTime");
const visitedNodes = document.getElementById("visitedNodes");
const pathLength = document.getElementById("pathLength");
const API_BASE_URL = window.location.protocol === "file:" ? "http://localhost:8081" : "";

let currentMap = "city";
let mode = "start";
let start = { ...maps.city.start };
let goal = { ...maps.city.goal };
let pathCells = new Set();

function renderGrid() {
    const grid = maps[currentMap].grid;
    gridEl.style.gridTemplateColumns = `repeat(${grid[0].length}, 58px)`;
    gridEl.innerHTML = "";

    grid.forEach((row, x) => {
        row.forEach((terrain, y) => {
            const cell = document.createElement("button");
            cell.className = `cell ${terrainClass(terrain)}`;
            cell.type = "button";
            cell.title = `(${x}, ${y})`;

            if (pathCells.has(`${x},${y}`)) {
                cell.classList.add("path");
            }
            if (start.x === x && start.y === y) {
                cell.classList.add("start");
            }
            if (goal.x === x && goal.y === y) {
                cell.classList.add("goal");
            }

            cell.addEventListener("click", () => chooseCell(x, y, terrain));
            gridEl.appendChild(cell);
        });
    });

    updateLabels();
}

function chooseCell(x, y, terrain) {
    if (terrain === -1) {
        setStatus("Blocked cell");
        return;
    }

    if (mode === "start") {
        start = { x, y };
        if (goal.x === x && goal.y === y) {
            setStatus("Start equals goal");
        }
    } else {
        goal = { x, y };
    }

    clearMetrics();
    pathCells = new Set();
    renderGrid();
}

async function findPath() {
    setStatus("Running");
    clearMetrics();

    const params = new URLSearchParams({
        map: currentMap,
        startX: start.x,
        startY: start.y,
        goalX: goal.x,
        goalY: goal.y
    });

    try {
        const response = await fetch(`${API_BASE_URL}/api/path?${params.toString()}`);
        const data = await response.json();
        if (!response.ok) {
            throw new Error(data.error || "Request failed");
        }

        pathCells = new Set(data.path.map((node) => `${node.x},${node.y}`));
        renderGrid();

        if (data.found) {
            travelTime.textContent = data.travelTime.toFixed(2);
            visitedNodes.textContent = data.visitedNodes;
            pathLength.textContent = `${data.steps} steps`;
            setStatus("Path found");
        } else {
            travelTime.textContent = "-";
            visitedNodes.textContent = data.visitedNodes;
            pathLength.textContent = "No path";
            setStatus("No path");
        }
    } catch (error) {
        setStatus(error.message);
    }
}

function resetPoints() {
    start = { ...maps[currentMap].start };
    goal = { ...maps[currentMap].goal };
    pathCells = new Set();
    clearMetrics();
    setStatus("Ready");
    renderGrid();
}

function terrainClass(value) {
    if (value === -1) return "wall";
    if (value === 1) return "gravel";
    if (value === 2) return "grass";
    if (value === 3) return "sand";
    return "road";
}

function setMode(nextMode) {
    mode = nextMode;
    modeButtons.forEach((button) => {
        button.classList.toggle("active", button.dataset.mode === mode);
    });
}

function updateLabels() {
    startLabel.textContent = `(${start.x}, ${start.y})`;
    goalLabel.textContent = `(${goal.x}, ${goal.y})`;
}

function clearMetrics() {
    travelTime.textContent = "-";
    visitedNodes.textContent = "-";
    pathLength.textContent = "-";
}

function setStatus(message) {
    statusEl.textContent = message;
}

mapSelect.addEventListener("change", () => {
    currentMap = mapSelect.value;
    resetPoints();
});

modeButtons.forEach((button) => {
    button.addEventListener("click", () => setMode(button.dataset.mode));
});

runButton.addEventListener("click", findPath);
resetButton.addEventListener("click", resetPoints);

renderGrid();
