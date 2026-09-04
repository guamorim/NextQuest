package com.nextquest.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Game {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String genre;

    private String platform;

    private LocalDate releaseDate;

    protected Game() {
    }

    public Game(String name, String genre, String platform, LocalDate releaseDate) {
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
