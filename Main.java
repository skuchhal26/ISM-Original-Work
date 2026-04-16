import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    private static final String API_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private final String apiKey;

    public Main(String apiKey) {
        this.apiKey = apiKey;
    }

    public String askCyberQuestion(String prompt) throws IOException {
        URL url = new URL(API_ENDPOINT + "?key=" + apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);

        JSONObject requestBody = new JSONObject();
        JSONArray contents = new JSONArray();
        JSONObject content = new JSONObject();
        JSONArray parts = new JSONArray();
        JSONObject part = new JSONObject();

        part.put("text", prompt);
        parts.put(part);
        content.put("parts", parts);
        contents.put(content);
        requestBody.put("contents", contents);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }

        int responseCode = conn.getResponseCode();
        InputStream stream = (responseCode == HttpURLConnection.HTTP_OK)
                ? conn.getInputStream()
                : conn.getErrorStream();

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Error " + responseCode + ": " + response);
        }

        JSONObject jsonResponse = new JSONObject(response.toString());
        JSONArray candidates = jsonResponse.optJSONArray("candidates");

        if (candidates != null && candidates.length() > 0) {
            JSONObject candidate = candidates.getJSONObject(0);
            JSONObject contentResponse = candidate.optJSONObject("content");

            if (contentResponse != null) {
                JSONArray partsResponse = contentResponse.optJSONArray("parts");
                if (partsResponse != null && partsResponse.length() > 0) {
                    return partsResponse.getJSONObject(0).optString("text", "No response generated.");
                }
            }
        }

        return "RISK: MEDIUM\nSUMMARY: Unable to analyze response clearly.\nUSER_ADVICE:\n- Verify the sender independently.\n- Do not click links until confirmed.";
    }

    public static String extractRiskLevel(String analysis) {
        String upper = analysis.toUpperCase();

        if (upper.contains("RISK: HIGH")) return "HIGH";
        if (upper.contains("RISK: MEDIUM")) return "MEDIUM";
        if (upper.contains("RISK: LOW")) return "LOW";

        return "MEDIUM";
    }

    public static void displayRiskMeter(String analysis) {
        String risk = extractRiskLevel(analysis);

        System.out.println("\n=== PHISHING RISK RESULT ===");
        switch (risk) {
            case "HIGH":
                System.out.println("PHISHING RISK: HIGH");
                System.out.println("Advice: Do NOT click links, open attachments, or reply.");
                break;
            case "MEDIUM":
                System.out.println("PHISHING RISK: MEDIUM");
                System.out.println("Advice: Verify sender through official channels before taking action.");
                break;
            default:
                System.out.println("PHISHING RISK: LOW");
                System.out.println("Advice: Low risk, but still verify before sharing sensitive information.");
                break;
        }

        System.out.println("CAUTION: AI analysis may be wrong. Always independently verify important emails.");
    }

    public static String buildPrompt(String email, boolean detailed) {
        if (detailed) {
            return "You are a cybersecurity expert analyzing an email for phishing.\n" +
                    "Be conservative because false negatives are dangerous.\n" +
                    "If uncertain, classify as MEDIUM instead of LOW.\n\n" +
                    "Return exactly this format:\n" +
                    "RISK: HIGH or MEDIUM or LOW\n" +
                    "SUMMARY: one short explanation\n" +
                    "RED_FLAGS:\n" +
                    "- item 1\n" +
                    "- item 2\n" +
                    "USER_ADVICE:\n" +
                    "- item 1\n" +
                    "- item 2\n" +
                    "FINAL_NOTE: one sentence warning the user to verify independently\n\n" +
                    "Email:\n" + email;
        } else {
            return "You are a cybersecurity expert analyzing an email for phishing.\n" +
                    "Be conservative because false negatives are dangerous.\n" +
                    "If uncertain, classify as MEDIUM instead of LOW.\n\n" +
                    "Return exactly this format:\n" +
                    "RISK: HIGH or MEDIUM or LOW\n" +
                    "SUMMARY: under 40 words\n" +
                    "USER_ADVICE:\n" +
                    "- one short next step\n" +
                    "- one short next step\n\n" +
                    "Email:\n" + email;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Email Security Analyzer ---");
        System.out.print("Enter API key: ");
        String apiKey = scanner.nextLine().trim();

        Main assistant = new Main(apiKey);

        while (true) {
            System.out.println("\nChoose analysis type:");
            System.out.println("1 - Quick phishing check");
            System.out.println("2 - Detailed analysis");
            System.out.println("Type 'exit' to quit.");

            String choice = scanner.nextLine().trim();
            if (choice.equalsIgnoreCase("exit")) break;

            boolean detailed = choice.equals("2");

            System.out.println("\nPaste email content. Type END on a new line when done:");
            StringBuilder emailBuilder = new StringBuilder();

            while (true) {
                String line = scanner.nextLine();
                if (line.equalsIgnoreCase("END")) break;
                if (line.equalsIgnoreCase("exit")) {
                    scanner.close();
                    System.out.println("Goodbye!");
                    return;
                }
                emailBuilder.append(line).append("\n");
            }

            String email = emailBuilder.toString().trim();
            String prompt = buildPrompt(email, detailed);

            try {
                String analysis = assistant.askCyberQuestion(prompt);
                displayRiskMeter(analysis);
                System.out.println("\n" + analysis);
                System.out.println("\n" + "=".repeat(60));
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Goodbye!");
    }
}