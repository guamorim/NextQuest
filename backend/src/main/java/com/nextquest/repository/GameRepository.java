package com.nextquest.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nextquest.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
    boolean existsByNameIgnoreCaseAndPlatformIgnoreCase(String name, String platform);
}