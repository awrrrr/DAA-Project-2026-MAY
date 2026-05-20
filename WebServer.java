import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServer {
    private static final int DEFAULT_PORT = 8080;
    private static final Path WEB_ROOT = Path.of("web");

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/path", WebServer::handlePathRequest);
        server.createContext("/", WebServer::handleStaticRequest);
        server.setExecutor(null);
        server.start();

        System.out.println("A* Path Planner UI running at http://localhost:" + port);
    }

    private static void handlePathRequest(HttpExchange exchange) throws IOException {
        if ("OPTIONS".equals(exchange.getRequestMethod())) {
            sendText(exchange, 204, "", "text/plain");
            return;
        }

        if (!"GET".equals(exchange.getRequestMethod())) {
            sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        try {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
            String mapName = query.getOrDefault("map", "city");
            TerrainMap map = getMap(mapName);

            int startX = parseInt(query, "startX");
            int startY = parseInt(query, "startY");
            int goalX = parseInt(query, "goalX");
            int goalY = parseInt(query, "goalY");

            AStarSolver solver = new AStarSolver(map);
            List<Node> path = solver.findPath(startX, startY, goalX, goalY);
            sendJson(exchange, 200, buildPathResponse(map, path, solver.getVisitedNodesCount()));
        } catch (IllegalArgumentException e) {
            sendJson(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private static void handleStaticRequest(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        if (requestPath.equals("/")) {
            requestPath = "/index.html";
        }

        Path filePath = WEB_ROOT.resolve(requestPath.substring(1)).normalize();
        if (!filePath.startsWith(WEB_ROOT) || !Files.exists(filePath) || Files.isDirectory(filePath)) {
            sendText(exchange, 404, "Not found", "text/plain");
            return;
        }

        byte[] bytes = Files.readAllBytes(filePath);
        exchange.getResponseHeaders().set("Content-Type", getContentType(filePath));
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static TerrainMap getMap(String mapName) {
        switch (mapName) {
            case "city": return TerrainMap.createCityMap();
            case "forest": return TerrainMap.createForestMap();
            case "trapped": return TerrainMap.createTrappedMap();
            default: throw new IllegalArgumentException("Unknown map: " + mapName);
        }
    }

    private static int parseInt(Map<String, String> query, String key) {
        try {
            return Integer.parseInt(query.get(key));
        } catch (Exception e) {
            throw new IllegalArgumentException("Missing or invalid parameter: " + key);
        }
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> result = new HashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) {
            return result;
        }

        for (String pair : rawQuery.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = decode(parts[0]);
            String value = parts.length > 1 ? decode(parts[1]) : "";
            result.put(key, value);
        }
        return result;
    }

    private static String buildPathResponse(TerrainMap map, List<Node> path, int visitedNodes) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"found\":").append(path != null).append(",");
        json.append("\"visitedNodes\":").append(visitedNodes).append(",");
        json.append("\"rows\":").append(map.getRows()).append(",");
        json.append("\"cols\":").append(map.getCols()).append(",");
        json.append("\"grid\":").append(gridToJson(map.getGrid())).append(",");

        if (path == null || path.isEmpty()) {
            json.append("\"travelTime\":0,");
            json.append("\"steps\":0,");
            json.append("\"path\":[]");
        } else {
            Node endNode = path.get(path.size() - 1);
            json.append("\"travelTime\":").append(String.format(java.util.Locale.US, "%.2f", endNode.gCost)).append(",");
            json.append("\"steps\":").append(path.size() - 1).append(",");
            json.append("\"path\":[");
            for (int i = 0; i < path.size(); i++) {
                Node node = path.get(i);
                if (i > 0) json.append(",");
                json.append("{\"x\":").append(node.x).append(",\"y\":").append(node.y).append("}");
            }
            json.append("]");
        }

        json.append("}");
        return json.toString();
    }

    private static String gridToJson(int[][] grid) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < grid.length; i++) {
            if (i > 0) json.append(",");
            json.append("[");
            for (int j = 0; j < grid[i].length; j++) {
                if (j > 0) json.append(",");
                json.append(grid[i][j]);
            }
            json.append("]");
        }
        json.append("]");
        return json.toString();
    }

    private static String getContentType(Path path) {
        String fileName = path.getFileName().toString();
        if (fileName.endsWith(".html")) return "text/html; charset=utf-8";
        if (fileName.endsWith(".css")) return "text/css; charset=utf-8";
        if (fileName.endsWith(".js")) return "application/javascript; charset=utf-8";
        return "application/octet-stream";
    }

    private static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
        sendText(exchange, status, json, "application/json; charset=utf-8");
    }

    private static void sendText(HttpExchange exchange, int status, String text, String contentType) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
