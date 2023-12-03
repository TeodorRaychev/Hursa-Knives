package com.hursa.hursaknives.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record ProductBindingModel(
    @NotEmpty(message = "Name cannot be empty")
        @Size(min = 2, message = "Name must be at least 2 characters")
        String name,
    @Size(min = 5, message = "Description must be at least 5 characters") String description,
    Double price,
    String material) {}
