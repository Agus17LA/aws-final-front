package com.awsfinal.awsfinalfront.services;
import com.awsfinal.awsfinalfront.models.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ValidationService {

    private final WebClient webClient;
    private final String validationApiBaseUrl;

    public ValidationService(WebClient webClient,
                             @Value("${validation.api.base-url}") String validationApiBaseUrl) {
        this.webClient = webClient;
        this.validationApiBaseUrl = validationApiBaseUrl;
    }

    public Mono<ResponseEntity<UserDTO>> validateUser(UserDTO user) {
        return webClient.post()
                .uri(validationApiBaseUrl + "/new")
                .bodyValue(user)
                .retrieve()
                .toEntity(UserDTO.class);
    }
}
