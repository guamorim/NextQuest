package com.nextquest.dto;

import com.nextquest.model.LibraryStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateLibraryEntryRequest {
    
    @NotNull (message = "Game ID is required")
    @Positive (message = "Game ID must be positive")
    private Long gameId;

    @NotNull (message = "Status is required")
    private LibraryStatus status;

    public Long getGameId() {
        return gameId;
    }

    public LibraryStatus getStatus() {
        return status;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public void setStatus(LibraryStatus status) {
        this.status = status;
    }
}
