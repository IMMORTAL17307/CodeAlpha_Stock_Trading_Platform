import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StockPlatform {
    private static final Map<String, Stock> marketStocks = new HashMap<>();
    private static final Portfolio portfolio = new Portfolio(10000.00);
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeMarket();

        System.out.println("=================================================");
        System.out.println("       CODEALPHA STOCK TRADING SIMULATOR         ");
        System.out.println("=================================================");
        System.out.println(" Welcome! You start with $10,000.00 in cash.     ");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            
            updateMarketPrices();
            
            switch (choice) {
                case "1":
                    displayMarketPrices();
                    break;
                case "2":
                    buyOperation();
                    break;
                case "3":
                    sellOperation();
                    break;
                case "4":
                    displayPortfolio();
                    break;
                case "5":
                    displayTransactionHistory();
                    break;
                case "6":
                    System.out.println("\nThank you for playing Stock Trading Simulator. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid option. Please select a valid option (1-6).");
            }
        }
    }

    private static void initializeMarket() {
        marketStocks.put("AAPL", new Stock("AAPL", "Apple Inc.", 175.50));
        marketStocks.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 152.20));
        marketStocks.put("MSFT", new Stock("MSFT", "Microsoft Corp.", 415.80));
        marketStocks.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 178.40));
        marketStocks.put("TSLA", new Stock("TSLA", "Tesla Inc.", 171.05));
        marketStocks.put("NVDA", new Stock("NVDA", "NVIDIA Corp.", 875.12));
    }

    private static void updateMarketPrices() {
        for (Stock stock : marketStocks.values()) {
            stock.updatePrice();
        }
    }

    private static void printMenu() {
        System.out.println("\n>>> MENU OPTIONS <<<");
        System.out.println("1. View Market Prices (Updates Live)");
        System.out.println("2. Buy Stock");
        System.out.println("3. Sell Stock");
        System.out.println("4. View Portfolio Performance");
        System.out.println("5. View Transaction History");
        System.out.println("6. Exit");
        System.out.print("Choose an option (1-6): ");
    }

    private static void displayMarketPrices() {
        System.out.println("\n--- Live Stock Market Prices ---");
        System.out.printf("%-8s | %-18s | %-12s\n", "Ticker", "Stock Name", "Current Price");
        System.out.println("-------------------------------------------------");
        for (Stock stock : marketStocks.values()) {
            System.out.printf("%-8s | %-18s | $%-12.2f\n", stock.getTicker(), stock.getName(), stock.getCurrentPrice());
        }
    }

    private static void buyOperation() {
        System.out.println("\n--- Buy Stock ---");
        System.out.print("Enter stock ticker to buy (e.g. AAPL): ");
        String ticker = scanner.nextLine().trim().toUpperCase();

        Stock stock = marketStocks.get(ticker);
        if (stock == null) {
            System.out.println("[!] Stock ticker not found in the market.");
            return;
        }

        System.out.printf("Current price of %s is $%.2f. You have $%.2f cash.\n", ticker, stock.getCurrentPrice(), portfolio.getBalance());
        System.out.print("Enter number of shares to buy: ");
        String qtyInput = scanner.nextLine().trim();
        try {
            int shares = Integer.parseInt(qtyInput);
            portfolio.buyStock(stock, shares);
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid quantity. Please enter an integer.");
        }
    }

    private static void sellOperation() {
        System.out.println("\n--- Sell Stock ---");
        System.out.print("Enter stock ticker to sell (e.g. AAPL): ");
        String ticker = scanner.nextLine().trim().toUpperCase();

        Stock stock = marketStocks.get(ticker);
        if (stock == null) {
            System.out.println("[!] Stock ticker not found in the market.");
            return;
        }

        int sharesOwned = portfolio.getSharesOwned(ticker);
        if (sharesOwned <= 0) {
            System.out.printf("[!] You do not own any shares of %s.\n", ticker);
            return;
        }

        System.out.printf("Current price of %s is $%.2f. You own %d shares.\n", ticker, stock.getCurrentPrice(), sharesOwned);
        System.out.print("Enter number of shares to sell: ");
        String qtyInput = scanner.nextLine().trim();
        try {
            int shares = Integer.parseInt(qtyInput);
            portfolio.sellStock(stock, shares);
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid quantity. Please enter an integer.");
        }
    }

    private static void displayPortfolio() {
        System.out.println("\n--- Portfolio Status & Performance ---");
        System.out.printf("Available Cash  : $%.2f\n", portfolio.getBalance());
        
        Map<String, Integer> holdings = portfolio.getHoldings();
        if (holdings.isEmpty()) {
            System.out.println("Holdings        : None");
        } else {
            System.out.println("Holdings        :");
            System.out.printf("  %-8s | %-8s | %-12s | %-12s\n", "Ticker", "Shares", "Current Price", "Market Value");
            System.out.println("  --------------------------------------------------");
            for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
                String ticker = entry.getKey();
                int shares = entry.getValue();
                Stock stock = marketStocks.get(ticker);
                double currentPrice = stock.getCurrentPrice();
                double marketValue = currentPrice * shares;
                System.out.printf("  %-8s | %-8d | $%-12.2f | $%-12.2f\n", ticker, shares, currentPrice, marketValue);
            }
        }

        double totalVal = portfolio.getPortfolioValue(marketStocks);
        double profitLoss = portfolio.getProfitLoss(marketStocks);

        System.out.println("-------------------------------------------------");
        System.out.printf("Total Portfolio Value: $%.2f\n", totalVal);
        if (profitLoss >= 0) {
            System.out.printf("Total Net Returns    : +$%.2f (Gain)\n", profitLoss);
        } else {
            System.out.printf("Total Net Returns    : -$%.2f (Loss)\n", Math.abs(profitLoss));
        }
    }

    private static void displayTransactionHistory() {
        System.out.println("\n--- Transaction History ---");
        java.util.List<Portfolio.Transaction> list = portfolio.getTransactionHistory();
        if (list.isEmpty()) {
            System.out.println("No transactions recorded yet.");
            return;
        }

        System.out.printf("%-4s | %-6s | %-6s | %-8s | %-10s\n", "Type", "Ticker", "Shares", "Price", "Total Cost");
        System.out.println("----------------------------------------------------");
        for (Portfolio.Transaction tx : list) {
            System.out.println(tx);
        }
    }
}
