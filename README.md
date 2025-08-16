# Weather Forecast — Flat Java Project (No Folders)

This is a **single-folder** Java web app (no nested folders). It uses the JDK's built-in `HttpServer`.
It serves a simple form and renders a 5‑day demo forecast (plain colors, no images).

## Run
Requires **Java 11+** (works with 17+).

```bash
# Compile
javac *.java

# Run
java WeatherApp
```

Then open: http://localhost:8080

## Notes
- No Maven/Gradle, no external libraries, no folders — everything is in this directory.
- You can upload this folder directly to GitHub as-is.
- If you want real forecast data later, add logic in `WeatherService#getFiveDayForecast` to call an API.
