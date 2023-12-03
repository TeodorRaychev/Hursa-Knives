package com.hursa.hursaknives.model.dto;

import java.util.List;

public record ProductViewDTO(
    Long id, String name, String description, Double price, String material, List<String> images) {}
