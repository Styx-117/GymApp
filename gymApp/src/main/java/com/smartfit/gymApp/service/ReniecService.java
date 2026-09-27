package com.smartfit.gymApp.service;

import com.smartfit.gymApp.dto.ReniecData;
import com.smartfit.gymApp.dto.ReniecRequest;
import com.smartfit.gymApp.dto.ReniecResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReniecService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${reniec.api.url}")
    private String apiUrl;

    @Value("${reniec.api.token}")
    private String apiToken;

    public ReniecData consultarDni(String dni) {
        if (dni == null || !dni.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos numéricos.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiToken);

        ReniecRequest body = new ReniecRequest(dni);
        HttpEntity<ReniecRequest> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<ReniecResponse> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, request, ReniecResponse.class);

            ReniecResponse r = response.getBody();
            if (r == null || !r.isSuccess() || r.getData() == null) {
                throw new RuntimeException("No se encontró información para el DNI: " + dni);
            }
            return r.getData();
        } catch (Exception e) {
            throw new RuntimeException("Error al consultar RENIEC: " + e.getMessage(), e);
        }
    }
}