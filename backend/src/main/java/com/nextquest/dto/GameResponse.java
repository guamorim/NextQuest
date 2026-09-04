package com.nextquest.dto;

import java.time.LocalDate;

public class GameResponse {
    private Long id;

    private String name;

    private String genre;

    private String platform;

    private LocalDate releaseDate;

    public GameResponse(Long id, String name, String genre, String platform, LocalDate releaseDate) {
        this.id = id;
        this.name = name;
        this.genre = genre;
        this.platform = platform;
        this.releaseDate = releaseDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getPlatform() {
        return platform;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }
}
