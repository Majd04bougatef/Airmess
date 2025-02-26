package services;

import okhttp3.*;
import org.json.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AiServices {

    private static final String API_KEY = "AIzaSyBH_tXU7l8OV_-ULvyuVHKrAp_K8bjoFDU";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;
    public String processPost(String postContent) {
        try {
            return moderateContent(postContent);
        } catch (Exception e) {
            System.err.println("Erreur lors de la modération du contenu : " + e.getMessage());
            return null;
        }
    }

    public String moderateContent(String content) throws Exception {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        String response = callGeminiApi(content);

        if (response == null || response.trim().isEmpty() || response.equals(content)) {
            System.err.println("Le contenu n'a pas été modéré ou est identique à l'original.");
            return null;
        }

        return response; // Retourne le contenu modéré
    }

    private String callGeminiApi(String content) throws IOException {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        JSONObject json = new JSONObject();

        JSONArray contents = new JSONArray();
        JSONObject contentObj = new JSONObject();
        contentObj.put("role", "user");
        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();
        part.put("text", "Please rewrite the following text to remove any offensive, hateful, or inappropriate content. Keep the overall meaning as similar as possible to the original: " + content);
        parts.put(part);
        contentObj.put("parts", parts);
        contents.put(contentObj);

        json.put("contents", contents);

        JSONObject generationConfig = new JSONObject();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topP", 1.0);
        generationConfig.put("topK", 50);
        generationConfig.put("candidateCount", 1);
        generationConfig.put("maxOutputTokens", 800);
        generationConfig.put("presencePenalty", 0.0);
        generationConfig.put("frequencyPenalty", 0.0);
        generationConfig.put("stopSequences", new JSONArray());

        json.put("generationConfig", generationConfig);

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(API_URL) // Use the new API_URL with the key
                .header("Content-Type", "application/json")
                .post(body)
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur API Gemini : " + response.code() + " " + response.body().string()); //Included response code
            }

            JSONObject responseBody = new JSONObject(response.body().string());
            JSONArray candidates = responseBody.getJSONArray("candidates");

            if (candidates != null && candidates.length() > 0) {
                JSONObject candidate = candidates.getJSONObject(0);
                JSONArray contentParts = candidate.getJSONObject("content").getJSONArray("parts");
                StringBuilder moderatedContent = new StringBuilder();
                for (int i = 0; i < contentParts.length(); i++) {
                    moderatedContent.append(contentParts.getJSONObject(i).getString("text"));
                }
                return moderatedContent.toString().trim();
            } else {
                throw new IOException("Réponse vide de l'API.");
            }
        }
    }
}