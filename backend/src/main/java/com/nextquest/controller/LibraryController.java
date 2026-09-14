package com.nextquest.controller;

import com.nextquest.dto.CreateLibraryEntryRequest;
import com.nextquest.dto.LibraryEntryResponse;
import com.nextquest.dto.UpdateLibraryEntryRequest;
import com.nextquest.service.LibraryService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/users/{userId}/library")
public class LibraryController {
    
    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryEntryResponse addGameToLibrary(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody CreateLibraryEntryRequest request) {
        return libraryService.addGameToLibrary(userId, request);
    }

    @GetMapping
    public List<LibraryEntryResponse> getLibrary(
            @PathVariable("userId") Long userId) {
        return libraryService.getLibrary(userId);
    }

    @GetMapping("/{entryId}")
    public LibraryEntryResponse getLibraryEntry(
            @PathVariable("userId") Long userId,
            @PathVariable("entryId") Long entryId) {
        return libraryService.getLibraryEntry(userId, entryId);
    }

    @PatchMapping("/{entryId}")
    public LibraryEntryResponse updateLibraryEntry(
            @PathVariable("userId") Long userId,
            @PathVariable("entryId") Long entryId,
            @Valid @RequestBody UpdateLibraryEntryRequest request) {
        return libraryService.updateLibraryEntry(userId, entryId, request);
    }

    @DeleteMapping("/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLibraryEntry(
            @PathVariable("userId") Long userId,
            @PathVariable("entryId") Long entryId) {
        libraryService.removeLibraryEntry(userId, entryId);
    }
}
