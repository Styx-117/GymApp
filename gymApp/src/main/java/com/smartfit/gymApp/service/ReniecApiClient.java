package com.smartfit.gymApp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfit.gymApp.dto.ReniecData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ReniecApiClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiBaseUrl = System.getenv().getOrDefault(
            "GYMAPP_API_BASE_URL", "http://localhost:8080").replaceAll("/+$", "");

    public ReniecData consultarDni(String dni) throws IOException, InterruptedException {
        if (dni == null || !dni.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos numéricos.");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "/api/reniec/dni/" + dni))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("La consulta RENIEC falló (HTTP " + response.statusCode() + ").");
        }

        ReniecData data = objectMapper.readValue(response.body(), ReniecData.class);
        if (data.getNombres() == null || data.getNombres().isBlank()
                || data.getApellidoPaterno() == null || data.getApellidoPaterno().isBlank()) {
            throw new IOException("RENIEC no devolvió nombres y apellidos para ese DNI.");
        }
        return data;
    }
}
