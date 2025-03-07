package services;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CurrencyConverter {

    private final Map<String, Double> rates = new HashMap<>();
    private static final String[] CURRENCIES = {"USD", "EUR", "GBP", "TND"};
    private static final Logger logger = Logger.getLogger(CurrencyConverter.class.getName());

    public CurrencyConverter() {
        loadRates();
    }

    private void loadRates() {
        try {
            // Set USD as base currency
            rates.put("USD", 1.0);

            // Get rates for other currencies relative to USD
            for (String currency : CURRENCIES) {
                if (!currency.equals("USD")) {
                    fetchRate("USD", currency);
                }
            }

            logger.info("Exchange rates loaded successfully");
        } catch (Exception e) {
            logger.severe("Failed to load exchange rates: " + e.getMessage());
            useBackupRates();
        }
    }

    private void fetchRate(String from, String to) {
        try {
            String apiKey = "YOUR_API_KEY"; // Replace with your actual API key

            String url = String.format(
                    "https://currencyconversionapi.com/api/v1/convert?q=%s_%s&compact=y&apiKey=%s",
                    from, to, apiKey);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                String pair = from + "_" + to;

                if (json.has(pair)) {
                    double rate = json.getJSONObject(pair).getDouble("val");
                    rates.put(to, rate);
                    logger.info("Rate for " + to + ": " + rate);
                } else {
                    throw new RuntimeException("Rate not found in response");
                }
            } else {
                throw new RuntimeException("API returned status code: " + response.statusCode());
            }
        } catch (Exception e) {
            logger.warning("Failed to fetch rate for " + to + ": " + e.getMessage());
            // We'll use backup rates instead
        }
    }

    private void useBackupRates() {
        logger.info("Using backup exchange rates");
        rates.clear();
        // Approximate rates as of 2023
        rates.put("USD", 1.0);
        rates.put("EUR", 0.92);
        rates.put("GBP", 0.79);
        rates.put("TND", 3.11);
    }

    public double convert(double amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        // If we're missing any rates, use backup rates
        if (!rates.containsKey(fromCurrency) || !rates.containsKey(toCurrency)) {
            useBackupRates();
        }

        double fromRate = rates.get(fromCurrency);
        double toRate = rates.get(toCurrency);

        return amount * toRate / fromRate;
    }
}