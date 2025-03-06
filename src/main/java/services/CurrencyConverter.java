package services;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class CurrencyConverter {

    private final Map<String, Double> rates = new HashMap<>();

    private static final String[] CURRENCIES = {"USD", "EUR", "GBP"};

    public CurrencyConverter() {
        loadRates();
    }

    private void loadRates() {
        try {
            String apiKey = "";
            if (apiKey == null || apiKey.isEmpty()) {
                throw new IllegalStateException("API key is missing");
            }

            String baseCurrency = "USD";

            String url = String.format(
              "https://api.exchangeratesapi.io/v1/latest?access_key=%s&base=%s&symbols=%s"
                    , apiKey
                    , baseCurrency
                    , String.join(",", CURRENCIES)
            );
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed to load exchange rates");
            }

            JSONObject json = new JSONObject(response.body());
            JSONObject rates = json.getJSONObject("rates");

            for (String currency : CURRENCIES) {
                double rate = rates.getDouble(currency);
                this.rates.put(currency, rate);
            }

            rates.put(baseCurrency, 1.0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public double convert(double amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        double fromRate = rates.get(fromCurrency);
        double toRate = rates.get(toCurrency);

        return amount * toRate / fromRate;
    }
}
