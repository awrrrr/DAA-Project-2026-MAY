# A* Path Planner Demo Runbook

## 1. Start the backend server

Open a terminal in this project folder:

```bash
cd "/Users/aryanroka/Documents/College Work Helper Agents/DAA Project"
```

Compile the Java files:

```bash
javac Main.java WebServer.java AStarSolver.java TerrainMap.java Node.java
```

Start the web server:

```bash
java WebServer 8081
```

You should see:

```text
A* Path Planner UI running at http://localhost:8081
```

## 2. Open the frontend

Use this URL for the demo:

```text
http://localhost:8081
```

Do not use the direct `file://.../web/index.html` page for the main demo unless needed. The hosted URL is cleaner because the same Java server serves both the frontend and backend.

## 3. Test the connection

On the page:

1. Select a map.
2. Click `Find path`.
3. Confirm that the grid highlights the path and the metrics update.

Expected city-map result:

```text
Travel time: 11.00
Visited nodes: 22
Path length: 11 steps
```

You can also test the backend directly:

```bash
curl "http://localhost:8081/api/path?map=city&startX=0&startY=0&goalX=5&goalY=6"
```

## Basic Bug Fixes

### Problem: Buttons do nothing

Make sure the Java server is running:

```bash
java WebServer 8081
```

Then open:

```text
http://localhost:8081
```

If you opened `web/index.html` directly, refresh the page after starting the backend.

### Problem: Port 8081 is already in use

Start the server on another port:

```bash
java WebServer 8082
```

Then open:

```text
http://localhost:8082
```

Note: if opening the frontend directly as a `file://` page, `web/app.js` is currently set to call `localhost:8081`. For another port, use the hosted URL instead.

### Problem: Browser says backend request failed

Check that the API works:

```bash
curl "http://localhost:8081/api/path?map=city&startX=0&startY=0&goalX=5&goalY=6"
```

If this fails, restart the server:

```bash
javac Main.java WebServer.java AStarSolver.java TerrainMap.java Node.java
java WebServer 8081
```

### Problem: Old code still appears

Stop the server with `Ctrl+C`, recompile, and restart:

```bash
javac Main.java WebServer.java AStarSolver.java TerrainMap.java Node.java
java WebServer 8081
```

Then refresh the browser page.

### Problem: Console demo is needed instead of UI

Run:

```bash
javac Main.java AStarSolver.java TerrainMap.java Node.java
java Main
```
