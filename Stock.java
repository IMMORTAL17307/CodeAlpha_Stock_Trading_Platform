import java.util.Random;

public class Stock {
    private String ticker;
    private String name;
    private double currentPrice;
    private final Random random = new Random();

    public Stock(String ticker, String name, double currentPrice) {
        this.ticker = ticker;
        this.name = name;
        this.currentPrice = currentPrice;
    }

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void updatePrice() {
    
        double percentChange = (random.nextDouble() * 6.0) - 3.0;
        double changeFactor = 1 + (percentChange / 100.0);
        this.currentPrice = Math.max(0.01, this.currentPrice * changeFactor);
    }
}
