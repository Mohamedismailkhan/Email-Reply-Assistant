package com.email.Writer.Service;

import com.email.Writer.io.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;
    private final WebClient webClient;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateEmailReply(EmailRequest emailRequest){

        //build prompt
        String prompt = buildPrompt(emailRequest);

        //craft request
        Map<String,Object> requestBody=Map.of(
                "contents",new Object[] {
                        Map.of("parts",new Object[]{
                                Map.of("text",prompt)
                        })
                }
        );

        // do request and gey response
        String response =webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        //extract response and return
        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode node=mapper.readTree(response);

            return node.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();


        }
        catch (Exception e){
           return "Error processing request :"+e.getMessage();
        }

    }

    public String buildPrompt(EmailRequest emailRequest){
        StringBuilder prompt=new StringBuilder();
        prompt.append("Generate a professional email reply for the following email content.please don't generate subject line");
        if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()){
            prompt.append("Use a ").append(emailRequest.getTone()).append("tone.");
        }
        prompt.append("Original Email : \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}
