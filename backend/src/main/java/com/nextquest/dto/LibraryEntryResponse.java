package com.nextquest.dto;

import com.nextquest.model.LibraryStatus;

import java.math.BigDecimal;

public class LibraryEntryResponse {
    
    private Long id;
    private GameSummaryResponse game;
    private LibraryStatus status;
    private Integer rating;
    private BigDecimal hoursPlayed;

    public LibraryEntryResponse(Long id, GameSummaryResponse game, LibraryStatus status, Integer rating,
            BigDecimal hoursPlayed) {
        this.id = id;
        this.game = game;
        this.status = status;
        this.rating = rating;
        this.hoursPlayed = hoursPlayed;
    }
    
    public Long getId() {
        return id;
    }

    public GameSummaryResponse getGame() {
        return game;
    }

    public LibraryStatus getStatus() {
        return status;
    }

    public Integer getRating() {
        return rating;
    }

    public BigDecimal getHoursPlayed() {
        return hoursPlayed;
    }
}
