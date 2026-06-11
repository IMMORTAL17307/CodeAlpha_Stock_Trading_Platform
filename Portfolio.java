import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Portfolio {
    private double balance;
    private final double initialCapital;
    private final Map<String, Integer> holdings;
    private final ArrayList<Transaction> transactionHistory;

    public static class Transaction {
        private final String type; 
        private final String ticker;
        private final int shares;
        private final double price;
        private final double totalCost;

        public Transaction(String type, String ticker, int shares, double price) {
            this.type = type;
            this.ticker = ticker;
            this.shares = shares;
            this.price = price;
            this.totalCost = shares * price;
        }

        public String getType() {
            return type;
        }

        public String getTicker() {
            return ticker;
        }

        public int getShares() {
            return shares;
        }

        public double getPrice() {
            return price;
        }

        public double getTotalCost() {
            return totalCost;
        }

        @Override
        public String toString() {
            return String.format("%-4s | %-6s | %-6d | $%-7.2f | $%-8.2f", type, ticker, shares, price, totalCost);
        }
    }

    public Portfolio(double startingBalance) {
        this.balance = startingBalance;
        this.initialCapital = startingBalance;
        this.holdings = new HashMap<>();
        this.transactionHistory = new ArrayList<>();
    }

    public double getBalance() {
        return balance;
    }

    public Map<String, Integer> getHoldings() {
        return holdings;
    }

    public ArrayList<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public int getSharesOwned(String ticker) {
        return holdings.getOrDefault(ticker, 0);
    }

    public boolean buyStock(Stock stock, int shares) {
        if (shares <= 0) {
            System.out.println("[!] Invalid quantity. Must buy 1 or more shares.");
            return false;
        }

        double totalCost = stock.getCurrentPrice() * shares;
        if (totalCost > balance) {
            System.out.printf("[!] Insufficient funds. Total cost is $%.2f, but you only have $%.2f.\n", totalCost, balance);
            return false;
        }

        balance -= totalCost;
        holdings.put(stock.getTicker(), holdings.getOrDefault(stock.getTicker(), 0) + shares);
        transactionHistory.add(new Transaction("BUY", stock.getTicker(), shares, stock.getCurrentPrice()));
        System.out.printf("[✓] Successfully bought %d shares of %s (%s) at $%.2f per share.\n", shares, stock.getName(), stock.getTicker(), stock.getCurrentPrice());
        return true;
    }

    public boolean sellStock(Stock stock, int shares) {
        if (shares <= 0) {
            System.out.println("[!] Invalid quantity. Must sell 1 or more shares.");
            return false;
        }

        int sharesOwned = getSharesOwned(stock.getTicker());
        if (sharesOwned < shares) {
            System.out.printf("[!] Insufficient shares. You own %d shares of %s, but tried to sell %d.\n", sharesOwned, stock.getTicker(), shares);
            return false;
        }

        double totalProceeds = stock.getCurrentPrice() * shares;
        balance += totalProceeds;

        int newShares = sharesOwned - shares;
        if (newShares == 0) {
            holdings.remove(stock.getTicker());
        } else {
            holdings.put(stock.getTicker(), newShares);
        }

        transactionHistory.add(new Transaction("SELL", stock.getTicker(), shares, stock.getCurrentPrice()));
        System.out.printf("[✓] Successfully sold %d shares of %s (%s) at $%.2f per share.\n", shares, stock.getName(), stock.getTicker(), stock.getCurrentPrice());
        return true;
    }

    public double getPortfolioValue(Map<String, Stock> marketStocks) {
        double holdingsValue = 0;
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = marketStocks.get(entry.getKey());
            if (stock != null) {
                holdingsValue += stock.getCurrentPrice() * entry.getValue();
            }
        }
        return balance + holdingsValue;
    }

    public double getProfitLoss(Map<String, Stock> marketStocks) {
        return getPortfolioValue(marketStocks) - initialCapital;
    }
}
