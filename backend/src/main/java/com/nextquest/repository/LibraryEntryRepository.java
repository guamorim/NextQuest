package com.nextquest.repository;

import com.nextquest.model.LibraryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibraryEntryRepository extends JpaRepository<LibraryEntry, Long> {
    
    boolean existsByUserIdAndGameId(Long userId, Long gameId);

    List<LibraryEntry> findAllByUserIdOrderByIdAsc(Long userId);

    Optional<LibraryEntry> findByIdAndUserId(Long id, Long userId);
}
