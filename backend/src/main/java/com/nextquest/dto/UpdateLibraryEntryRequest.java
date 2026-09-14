package com.nextquest.dto;

import com.nextquest.model.LibraryStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Digits;

import java.math.BigDecimal;

public class UpdateLibraryEntryRequest {
    
    private LibraryStatus status;

    @Min (value = 1, message = "Rating must be at least 1")
    @Max (value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @DecimalMin (value = "0.0", inclusive = true, message = "Hours played must be non-negative")
    @Digits (integer = 8, fraction = 2, message = "Hours played must have at most 8 digits and 2 decimal places")
    private BigDecimal hoursPlayed;

    public LibraryStatus getStatus() {
        return status;
    }

    public Integer getRating() {
        return rating;
    }

    public BigDecimal getHoursPlayed() {
        return hoursPlayed;
    }

    public void setStatus(LibraryStatus status) {
        this.status = status;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setHoursPlayed(BigDecimal hoursPlayed) {
        this.hoursPlayed = hoursPlayed;
    }
}
