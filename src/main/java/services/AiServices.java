package services;

import java.io.IOException;
import okhttp3.*;
import org.json.*;

public class AiServices {

    // Remplace par ta clé API (et pense à la stocker dans une variable d'environnement pour plus de sécurité)
    private static final String API_KEY = ("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");  // Utilise une variable d'environnement pour plus de sécurité
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    // Fonction principale pour modérer le contenu
    public String processPost(String postContent) {
        try {
            return moderateContent(postContent);
        } catch (Exception e) {
            System.err.println("Erreur lors de la modération du contenu : " + e.getMessage());
            return null;
        }
    }

    // Fonction pour modérer le contenu (appelle l'API OpenAI)
    public String moderateContent(String content) throws Exception {
        // On vérifie si le contenu est vide
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        // Appel à l'API OpenAI pour modérer le contenu
        String response = callOpenAiApi(content);

        // Si la réponse est vide, ou identique au contenu original, on la rejette
        if (response == null || response.trim().isEmpty() || response.equals(content)) {
            System.err.println("Le contenu n'a pas été modéré ou est identique à l'original.");
            return null;
        }

        // Vérification supplémentaire pour des mots offensants
        if (containsOffensiveContent(response)) {
            System.err.println("Contenu modéré détecté comme offensant.");
            return null; // Le contenu modéré est inacceptable
        }

        return response; // Retourne le contenu modéré
    }

    // Fonction qui appelle l'API OpenAI pour reformuler le contenu
    private String callOpenAiApi(String content) throws IOException {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalStateException("Clé API OpenAI non définie !");
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        JSONObject json = new JSONObject();
        json.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system")
                .put("content", "Tu es un assistant qui reformule du texte en supprimant tout contenu raciste ou offensant."));
        messages.put(new JSONObject().put("role", "user").put("content", content));

        json.put("messages", messages);
        json.put("temperature", 0.7);

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorResponse = response.body().string();
                JSONObject errorJson = new JSONObject(errorResponse);
                // Vérification si le message d'erreur correspond à un quota insuffisant
                if (errorJson.has("error") && errorJson.getJSONObject("error").getString("code").equals("insufficient_quota")) {
                    System.err.println("Quota d'API dépassé. Veuillez vérifier votre plan et vos détails de facturation.");
                    return null;
                }
                throw new IOException("Erreur API OpenAI : " + errorResponse);
            }

            JSONObject responseBody = new JSONObject(response.body().string());
            JSONArray choices = responseBody.optJSONArray("choices");

            if (choices != null && choices.length() > 0) {
                return choices.getJSONObject(0).getJSONObject("message").getString("content").trim();
            } else {
                throw new IOException("Réponse vide de l'API.");
            }
        } catch (IOException e) {
            // Affichage détaillé de l'erreur pour aider à résoudre le problème
            System.err.println("Erreur lors de l'appel à l'API OpenAI : " + e.getMessage());
            return null;  // Retourne null en cas d'erreur
        }
    }


    // Fonction pour vérifier si le contenu contient des mots offensants
    private boolean containsOffensiveContent(String content) {
        // Liste des mots-clés offensants (exemples, à personnaliser)
        String[] offensiveWords = {"raciste", "haine", "violence", "insulte"};

        // Recherche des mots-clés dans le texte
        for (String word : offensiveWords) {
            if (content.toLowerCase().contains(word)) {
                return true;
            }
        }

        return false;
    }
}
