import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

// This program is a simple question assistant.
// It sends the user's question to Google's API and prints the response.
public class CyberSecurityAssistant {

// This is how it connects with the API.
    private static final String API_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    private String apiKey;

    public CyberSecurityAssistant(String apiKey) {
        this.apiKey = apiKey;
    }

    // This method sends a cybersecurity question to the API and returns the answer
    public String askCyberQuestion(String question) throws IOException {

        String enhancedPrompt = "As a cybersecurity expert, please answer this question: " + question;

        // This creates a connection to the API server over the internet.
        URL url = new URL(API_ENDPOINT + "?key=" + apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject requestBody = new JSONObject();
        JSONArray contents = new JSONArray();
        JSONObject content = new JSONObject();
        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();

        part.put("text", enhancedPrompt);
        parts.put(part);
        content.put("parts", parts);
        contents.put(content);
        requestBody.put("contents", contents);

        // This sends the question data to the server
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // This checks if the server was able to responded
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            br.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            if (candidates.length() > 0) {
                JSONObject candidate = candidates.getJSONObject(0);
                JSONObject contentResponse = candidate.getJSONObject("content");
                JSONArray partsResponse = contentResponse.getJSONArray("parts");
                if (partsResponse.length() > 0) {
                    return partsResponse.getJSONObject(0).getString("text");
                }
            }
            return "No response generated.";

        } else {
            // If something goes wrong, it will show a error message
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                errorResponse.append(line.trim());
            }
            br.close();
            throw new IOException("Error " + responseCode + ": " + errorResponse.toString());
        }
    }

    // This is how the program actually begins.
    public static void main(String[] args) {

        // Scanner allows the user to enter their question.
        Scanner scanner = new Scanner(System.in);

        System.out.println(" --- Cybersecurity Q&A Assistant ---");
        System.out.print("Enter your Google API key: ");
        String apiKey = scanner.nextLine().trim();

        CyberSecurityAssistant assistant = new CyberSecurityAssistant(apiKey);

        System.out.println("\nAsk cybersecurity questions (type 'exit' to quit):\n");

        // The user can keep asking questions until they type "exit"
        while (true) {
            System.out.print("Question: ");
            String question = scanner.nextLine().trim();

            if (question.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            if (question.isEmpty()) {
                continue;
            }

            // The output is recived from the methods above.
            try {
                System.out.println("\nAnswer:");
                String answer = assistant.askCyberQuestion(question);
                System.out.println(answer);
                System.out.println("\n" + "=".repeat(60) + "\n");
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
                System.out.println();
            }
        }
        scanner.close();
    }
}
