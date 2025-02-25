package services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.AccessToken;
import okhttp3.*;
import org.json.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class AiServices {

    // URL correcte de l'API
    private static final String API_URL = "https://aiplatform.googleapis.com/v1/projects/socialmedia-452018/locations/us-central1/publishers/google/models/gemini-1.5-flash-001:generateContent";

    // Chemin vers le fichier de clé de service (à externaliser idéalement)
    private static final String SERVICE_ACCOUNT_KEY_PATH = "src/main/resources/json/socialmedia-452018-092a9a2ee5e6.json";

    // Méthode pour obtenir un token d'accès
    public static String getAccessToken() throws IOException {
        // Charger les informations de la clé de service
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH))
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));

        // Rafraîchir et obtenir le token d'accès
        credentials.refreshIfExpired();
        AccessToken accessToken = credentials.getAccessToken();

        return accessToken.getTokenValue();
    }

    // Fonction pour modérer le contenu via l'API Gemini
    public String processPost(String postContent) {
        try {
            return moderateContent(postContent);
        } catch (Exception e) {
            System.err.println("Erreur lors de la modération du contenu : " + e.getMessage());
            return null;
        }
    }

    // Fonction pour modérer le contenu
    public String moderateContent(String content) throws Exception {
        // Vérifie si le contenu est vide
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        // Appel à l'API Gemini pour modérer le contenu
        String response = callGeminiApi(content);

        // Si la réponse est vide ou identique au contenu original, la rejeter
        if (response == null || response.trim().isEmpty() || response.equals(content)) {
            System.err.println("Le contenu n'a pas été modéré ou est identique à l'original.");
            return null;
        }

        return response; // Retourne le contenu modéré
    }

    private String callGeminiApi(String content) throws IOException {
        // Récupérer le token d'accès
        String accessToken = getAccessToken();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)  // Augmenter le délai de connexion
                .readTimeout(60, TimeUnit.SECONDS)     // Augmenter le délai de lecture
                .build();

        // Construction de la requête JSON avec la structure correcte
        JSONObject json = new JSONObject();

        // Contenu à envoyer
        JSONArray contents = new JSONArray();
        JSONObject contentObj = new JSONObject();
        contentObj.put("role", "user");
        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();
        //Add prompt to tell the Model to remove inappropriate content
        part.put("text", "Please rewrite the following text to remove any offensive, hateful, or inappropriate content. Keep the overall meaning as similar as possible to the original: " + content);
        parts.put(part);
        contentObj.put("parts", parts);
        contents.put(contentObj);

        json.put("contents", contents);

        // Paramètres additionnels (exemple de configuration de génération, à ajuster selon tes besoins)
        JSONObject generationConfig = new JSONObject();
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topP", 1.0);
        generationConfig.put("topK", 50);
        generationConfig.put("candidateCount", 1);
        generationConfig.put("maxOutputTokens", 800); //Increased maxOutputTokens
        generationConfig.put("presencePenalty", 0.0);
        generationConfig.put("frequencyPenalty", 0.0);
        generationConfig.put("stopSequences", new JSONArray());
        //  generationConfig.put("responseMimeType", "text/plain");  //Removed as it's not required

        json.put("generationConfig", generationConfig);

        //Example for removing safety settings as its not supported in this model
        //**REMOVE SAFETY SETTINGS BLOCK ENTIRELY**
        // Création du corps de la requête
        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));

        // Construction de la requête HTTP
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        // Exécution de la requête et traitement de la réponse
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur API Gemini : " + response.code() + " " + response.body().string()); //Included response code
            }

            // Traitement de la réponse JSON de l'API
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

  /*  public static void main(String[] args) {
        AiServices aiServices = new AiServices();
        String postContent = "This is a test post with some potentially offensive content.  I hate everyone!"; //Test Content

        try {
            // Processus de modération du contenu
            String result = aiServices.processPost(postContent);
            if (result != null) {
                System.out.println("Contenu modéré : " + result);
            } else {
                System.out.println("Le contenu n'a pas été modéré.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}