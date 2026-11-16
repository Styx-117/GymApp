package com.smartfit.gymApp.service;

import com.smartfit.gymApp.dto.ReniecData;
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

    @Value("${reniec.api.token:}")
    private String apiToken;

    public ReniecData consultarDni(String dni) {
        if (dni == null || !dni.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener 8 dígitos numéricos.");
        }
        if (apiToken == null || apiToken.isBlank()) {
            throw new IllegalStateException("Falta configurar reniec.api.token en application.properties.");
        }

        // Decolecta espera GET con ?numero=NNNNNNNN
        String url = apiUrl + "?numero=" + dni;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        // GET, no POST
        ResponseEntity<ReniecData> response = restTemplate.exchange(
                url, HttpMethod.GET, request, ReniecData.class);

        ReniecData data = response.getBody();
        if (data == null) {
            throw new RuntimeException("Decolecta devolvió una respuesta vacía para el DNI " + dni);
        }
        if (data.getNombres() == null || data.getNombres().isBlank()) {
            throw new RuntimeException("Decolecta no devolvió nombres para el DNI " + dni);
        }
        return data;
    }
}