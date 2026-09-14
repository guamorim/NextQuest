package com.nextquest.dto;

public class GameSummaryResponse {
    
    private Long id;
    private String name;

    public GameSummaryResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
