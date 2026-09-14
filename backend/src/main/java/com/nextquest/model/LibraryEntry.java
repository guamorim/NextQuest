package com.nextquest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "library_entries", uniqueConstraints = {
        @UniqueConstraint(name = "uk_library_entries_user_game", columnNames = { "user_id", "game_id" })
})

public class LibraryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LibraryStatus status;

    @Min(1)
    @Max(5)
    private Integer rating;

    @DecimalMin("0.0")
    @Column(name = "hours_played", precision = 10, scale = 2)
    private BigDecimal hoursPlayed;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected LibraryEntry() {
    }

    public LibraryEntry(User user, Game game, LibraryStatus status) {
        this.user = user;
        this.game = game;
        this.status = status;
    }

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Game getGame() {
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
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
