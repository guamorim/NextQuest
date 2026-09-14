package com.nextquest.service;

import com.nextquest.dto.CreateLibraryEntryRequest;
import com.nextquest.dto.GameSummaryResponse;
import com.nextquest.dto.LibraryEntryResponse;
import com.nextquest.dto.UpdateLibraryEntryRequest;
import com.nextquest.exception.DuplicateResourceException;
import com.nextquest.exception.ResourceNotFoundException;
import com.nextquest.model.Game;
import com.nextquest.model.LibraryEntry;
import com.nextquest.model.User;
import com.nextquest.repository.GameRepository;
import com.nextquest.repository.LibraryEntryRepository;
import com.nextquest.repository.UserRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class LibraryService {
    
    private final LibraryEntryRepository libraryEntryRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final Validator validator;

    public LibraryService(LibraryEntryRepository libraryEntryRepository, GameRepository gameRepository,
            UserRepository userRepository, Validator validator) {
        this.libraryEntryRepository = libraryEntryRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.validator = validator;
    }
    
    public LibraryEntryResponse addGameToLibrary(
            Long userId,
            CreateLibraryEntryRequest request) {
        validateRequest(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + userId));

        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Game not found: " + request.getGameId()));

        boolean alreadyExists = libraryEntryRepository.existsByUserIdAndGameId(userId, request.getGameId());

        if (alreadyExists) {
            throw new DuplicateResourceException(
                    "Game already exists in this user's library: " + request.getGameId());
        }

        LibraryEntry entry = new LibraryEntry(
                user,
                game,
                request.getStatus());

        LibraryEntry savedEntry = libraryEntryRepository.saveAndFlush(entry);

        return toResponse(savedEntry);

    }
    
    @Transactional(readOnly = true)
    public List<LibraryEntryResponse> getLibrary(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }

        return libraryEntryRepository
                .findAllByUserIdOrderByIdAsc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public LibraryEntryResponse getLibraryEntry(Long userId, Long entryId) {
        LibraryEntry entry = findEntryForUser(userId, entryId);
        return toResponse(entry);
    }

    public LibraryEntryResponse updateLibraryEntry(Long userId, Long entryId, UpdateLibraryEntryRequest request) {

        validateRequest(request);

        LibraryEntry entry = findEntryForUser(userId, entryId);

        if (request.getStatus() != null) {
            entry.setStatus(request.getStatus());
        }

        if (request.getRating() != null) {
            entry.setRating(request.getRating());
        }

        if (request.getHoursPlayed() != null) {
            entry.setHoursPlayed(request.getHoursPlayed());
        }

        return toResponse(entry);
    }
    
    public void removeLibraryEntry(Long userId, Long entryId) {
        LibraryEntry entry = findEntryForUser(userId, entryId);
        libraryEntryRepository.delete(entry);
    }

    private LibraryEntry findEntryForUser(Long userId, Long entryId) {
        return libraryEntryRepository.findByIdAndUserId(entryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Library entry not found for user: " + userId + ", entry: " + entryId));
    }

    private void validateRequest(Object request) {
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private LibraryEntryResponse toResponse(LibraryEntry entry) {

        Game game = entry.getGame();

        GameSummaryResponse gameSummary = new GameSummaryResponse(game.getId(), game.getName());

        return new LibraryEntryResponse(
                entry.getId(),
                gameSummary,
                entry.getStatus(),
                entry.getRating(),
                entry.getHoursPlayed()
        );
    }

}
