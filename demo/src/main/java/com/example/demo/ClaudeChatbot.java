package com.example.demo;

import java.util.Scanner;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ClaudeChatbot {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        HttpClient client = HttpClient.newHttpClient();

        ObjectMapper mapper = new ObjectMapper();

        System.out.println("===============================");
        System.out.println("       ANBU AI CHATBOT");
        System.out.println("       Powered by Ollama");
        System.out.println("===============================");

        while (true) {

            System.out.print("\nYou: ");

            String question = sc.nextLine();

            if (question.equalsIgnoreCase("exit")) {

                System.out.println("Goodbye!");

                break;
            }

            try {

                // Create JSON request
                ObjectNode requestBody = mapper.createObjectNode();

                requestBody.put("model", "llama3.2:1b");

                requestBody.put("stream", false);

                ObjectNode message = mapper.createObjectNode();

                message.put("role", "user");

                message.put("content", question);

                requestBody.putArray("messages").add(message);

                // Convert JSON to String
                String json = mapper.writeValueAsString(requestBody);

                // Create HTTP request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:11434/api/chat"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                // Send request
                HttpResponse<String> response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                // Read AI response
                JsonNode result = mapper.readTree(response.body());

                if (response.statusCode() == 200) {

                    String answer = result
                            .path("message")
                            .path("content")
                            .asText();

                    System.out.println("\nClaude: " + answer);

                } else {

                    System.out.println("Error: " + response.body());

                }

            } catch (Exception e) {

                System.out.println("Error: " + e.getMessage());

            }
        }

        sc.close();
    }
}