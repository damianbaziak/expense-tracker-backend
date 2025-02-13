package com.example.trainingsapp.authorization.api.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthAccessTokenDTO(@NotBlank String accessToken) {
}
