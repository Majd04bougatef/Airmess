package services;

import java.io.IOException;
import okhttp3.*;
import org.json.*;

public class AiServices {

    private static final String API_KEY = System.getenv("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");
    private static final String API_URL = "https://platform.openai.com/api-keys";

    // Fonction pour détecter et reformuler un post
    public String processPost(String postContent) throws Exception {
        return detectAndReformulateContent(postContent);
    }

    // Fonction pour appeler l'API et reformuler le texte
    private String detectAndReformulateContent(String postContent) throws Exception {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", "Tu es un assistant qui reformule du texte en supprimant tout contenu raciste ou offensant."));
        messages.put(new JSONObject().put("role", "user").put("content", postContent));

        json.put("messages", messages);
        json.put("temperature", 0.7);

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur API : " + response);
            }

            JSONObject responseBody = new JSONObject(response.body().string());
            JSONArray choices = responseBody.getJSONArray("choices");

            if (choices.length() > 0) {
                return choices.getJSONObject(0).getJSONObject("message").getString("content").trim();
            } else {
                throw new IOException("Réponse vide de l'API.");
            }
        }
    }


    public String moderateContent(String content) throws Exception {
        return detectAndReformulateContent(content); // Utilise déjà ta fonction de reformulation
    }

}
