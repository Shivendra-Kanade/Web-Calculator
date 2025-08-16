import java.time.LocalDate;

public class Forecast {
    private LocalDate date;
    private double tempMinC;
    private double tempMaxC;
    private String description;

    public Forecast() {}

    public Forecast(LocalDate date, double tempMinC, double tempMaxC, String description) {
        this.date = date;
        this.tempMinC = tempMinC;
        this.tempMaxC = tempMaxC;
        this.description = description;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public double getTempMinC() { return tempMinC; }
    public void setTempMinC(double tempMinC) { this.tempMinC = tempMinC; }
    public double getTempMaxC() { return tempMaxC; }
    public void setTempMaxC(double tempMaxC) { this.tempMaxC = tempMaxC; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
