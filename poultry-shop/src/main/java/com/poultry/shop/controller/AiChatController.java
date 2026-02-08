package com.poultry.shop.controller;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/ai-chat")
public class AiChatController {

    @Value("${openai.api.key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();

    // ✅ Health check (prevents 405 spam in logs if someone opens URL in browser)
    @GetMapping
    public String health() {
        return "AI Chat endpoint is alive. Please use POST to chat.";
    }

    // ✅ Main AI Chat endpoint (POST)
    @PostMapping
    public String chat(@org.springframework.web.bind.annotation.RequestBody String userMessage) throws IOException {

        // 🔎 Basic validation
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "Please type a message.";
        }

        // 🧠 Log for debugging
        System.out.println("AI endpoint hit. User said: " + userMessage);
        System.out.println("OpenAI key loaded? " + (apiKey != null && !apiKey.isBlank()));

        // ✅ Build request JSON for OpenAI Responses API
        String json = """
        {
          "model": "gpt-4.1-mini",
          "input": "%s"
        }
        """.formatted(userMessage.replace("\"", "\\\""));

        RequestBody body = RequestBody.create(
                json,
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/responses")
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful() || response.body() == null) {
                // 🔥 Return real error instead of hiding it
                return "AI error: " + response.code() + " - " + response.message();
            }

            String resp = response.body().string();

            // 🧩 Safe extraction of output_text (MVP-friendly)
            int idx = resp.indexOf("\"output_text\":\"");
            if (idx == -1) {
                // Log raw response for debugging
                System.out.println("Raw OpenAI response: " + resp);
                return "AI response format changed. Please try again.";
            }

            int start = idx + 15;
            int end = resp.indexOf("\"", start);

            return resp.substring(start, end).replace("\\n", "\n");
        }
    }
}
