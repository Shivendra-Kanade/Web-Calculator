import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WeatherService {

    // NOTE: To keep the project dependency-free and flat, this demo returns static data.
    // You can wire an API call later if you want real data.
    public List<Forecast> getFiveDayForecast(String city, String countryCode, String units) {
        LocalDate today = LocalDate.now();
        List<Forecast> list = new ArrayList<>();
        list.add(new Forecast(today, 24, 31, "clear sky"));
        list.add(new Forecast(today.plusDays(1), 23, 30, "few clouds"));
        list.add(new Forecast(today.plusDays(2), 22, 29, "scattered clouds"));
        list.add(new Forecast(today.plusDays(3), 24, 32, "light rain"));
        list.add(new Forecast(today.plusDays(4), 25, 33, "moderate rain"));
        return list;
    }
}
