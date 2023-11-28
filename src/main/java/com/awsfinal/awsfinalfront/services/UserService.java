package com.awsfinal.awsfinalfront.services;

import com.awsfinal.awsfinalfront.models.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class UserService {

    private final WebClient webClient;
    private final String abmApiBaseUrl;

    public UserService(WebClient.Builder webClientBuilder,
                       @Value("${abm.api.base-url}") String abmApiBaseUrl) {
        this.abmApiBaseUrl = abmApiBaseUrl;
        this.webClient = webClientBuilder.baseUrl(abmApiBaseUrl).build();

    }

    public Map<String, Object> convertToMapExcludingDni(UserDTO userDTO) {
        Map<String, Object> map = new HashMap<>();
        if (userDTO != null) {
            map.put("firstName", userDTO.getFirstName());
            map.put("lastName", userDTO.getLastName());
            map.put("email", userDTO.getEmail());
            map.put("birthdate", userDTO.getBirthdate());
            map.put("phoneNumber", userDTO.getPhoneNumber());
            //map.put("profession", userDTO.getProfession());
        }
        return map;
    }

    public List<UserDTO> getAllUsers() {
        return webClient.get()
                .uri("/")
                .retrieve()
                .bodyToFlux(UserDTO.class).collectList().block();
    }

    public UserDTO getUserByDni(String dni) {
        return webClient.get()
                .uri("/{userDni}", dni)
                .retrieve()
                .onStatus(status -> status.value() == 404, response -> Mono.empty())
                .bodyToMono(UserDTO.class).block();
    }

    public UserDTO saveUser(UserDTO user) {
        return webClient.post()
                .uri( "/add")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(UserDTO.class).block();
    }

    public Void deleteUser(String dni) {
        return webClient.delete()
                .uri("/" + dni)
                .retrieve()
                .bodyToMono(Void.class).block();
    }

    public UserDTO patchUser(UserDTO newUser) {
        Map<String, Object> changes = convertToMapExcludingDni(newUser);
        return webClient.patch()
                .uri("/{userDni}", newUser.getDni())
                .bodyValue(changes)
                .retrieve()
                .bodyToMono(UserDTO.class).block();
    }


}
