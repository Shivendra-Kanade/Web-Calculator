import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class WeatherApp {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticFileHandler("index.html", "text/html; charset=utf-8"));
        server.createContext("/styles.css", new StaticFileHandler("styles.css", "text/css; charset=utf-8"));
        server.createContext("/forecast", new ForecastHandler());
        server.setExecutor(null);
        System.out.println("Weather app running on http://localhost:" + port);
        server.start();
    }

    static class StaticFileHandler implements HttpHandler {
        private final String filename;
        private final String contentType;

        StaticFileHandler(String filename, String contentType) {
            this.filename = filename;
            this.contentType = contentType;
        }

        @Override public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                send(exchange, 405, "Method Not Allowed", "text/plain; charset=utf-8");
                return;
            }
            File f = new File(filename);
            if (!f.exists()) {
                send(exchange, 404, "Not Found", "text/plain; charset=utf-8");
                return;
            }
            byte[] bytes = Files.readAllBytes(f.toPath());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        }
    }

    static class ForecastHandler implements HttpHandler {
        private final WeatherService service = new WeatherService();

        @Override public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                send(exchange, 405, "Method Not Allowed", "text/plain; charset=utf-8");
                return;
            }
            Map<String, String> q = parseQuery(exchange.getRequestURI().getRawQuery());
            String city = q.getOrDefault("city", "").trim();
            String country = q.getOrDefault("country", "").trim();
            String units = q.getOrDefault("units", "metric").trim();

            if (city.isEmpty()) {
                redirect(exchange, "/");
                return;
            }

            List<Forecast> list = service.getFiveDayForecast(city, country, units);
            String template = readFile("forecast.html");
            if (template == null) {
                send(exchange, 500, "Missing forecast.html", "text/plain; charset=utf-8");
                return;
            }
            String title = "Forecast for " + city + (country.isEmpty() ? "" : ", " + country);
            StringBuilder items = new StringBuilder();
            DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE;
            for (Forecast f : list) {
                items.append("<div class=\"item\">")
                     .append("<div class=\"date\">").append(fmt.format(f.getDate())).append("</div>")
                     .append("<div>Min: ").append(formatTemp(f.getTempMinC(), units)).append("</div>")
                     .append("<div>Max: ").append(formatTemp(f.getTempMaxC(), units)).append("</div>")
                     .append("<div class=\"desc\">").append(escapeHtml(f.getDescription())).append("</div>")
                     .append("</div>");
            }
            String html = template
                    .replace("{{TITLE}}", escapeHtml(title))
                    .replace("{{ITEMS}}", items.toString())
                    .replace("{{BACK}}", "/");

            send(exchange, 200, html, "text/html; charset=utf-8");
        }

        private static String formatTemp(double celsius, String units) {
            if ("imperial".equalsIgnoreCase(units)) {
                double f = (celsius * 9/5.0) + 32;
                return Math.round(f) + " °F";
            }
            return Math.round(celsius) + " °C";
        }
    }

    static void send(HttpExchange ex, int code, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    static void redirect(HttpExchange ex, String to) throws IOException {
        ex.getResponseHeaders().set("Location", to);
        ex.sendResponseHeaders(302, -1);
        ex.close();
    }

    static Map<String, String> parseQuery(String raw) {
        Map<String, String> map = new LinkedHashMap<>();
        if (raw == null || raw.isEmpty()) return map;
        for (String pair : raw.split("&")) {
            String[] kv = pair.split("=", 2);
            String k = urlDecode(kv[0]);
            String v = kv.length > 1 ? urlDecode(kv[1]) : "";
            map.put(k, v);
        }
        return map;
    }

    static String urlDecode(String s) {
        try { return URLDecoder.decode(s, StandardCharsets.UTF_8); }
        catch (Exception e) { return s; }
    }

    static String readFile(String name) {
        try { return Files.readString(new File(name).toPath(), StandardCharsets.UTF_8); }
        catch (Exception e) { return null; }
    }

    static String escapeHtml(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
