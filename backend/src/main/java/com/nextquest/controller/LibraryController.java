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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;


@RestController
@RequestMapping("/api/library")
public class LibraryController {
    
    private final LibraryService libraryService;

    private Long authenticatedUserId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibraryEntryResponse addGameToLibrary(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateLibraryEntryRequest request) {
        return libraryService.addGameToLibrary(authenticatedUserId(jwt), request);
    }

    @GetMapping
    public List<LibraryEntryResponse> getLibrary(
             @AuthenticationPrincipal Jwt jwt) {
        return libraryService.getLibrary(authenticatedUserId(jwt));
    }

    @GetMapping("/{entryId}")
    public LibraryEntryResponse getLibraryEntry(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long entryId) {
        return libraryService.getLibraryEntry(authenticatedUserId(jwt), entryId);
    }

    @PatchMapping("/{entryId}")
    public LibraryEntryResponse updateLibraryEntry(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long entryId,
            @Valid @RequestBody UpdateLibraryEntryRequest request) {
        return libraryService.updateLibraryEntry(authenticatedUserId(jwt), entryId, request);
    }

    @DeleteMapping("/{entryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLibraryEntry(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long entryId) {
        libraryService.removeLibraryEntry(authenticatedUserId(jwt), entryId);
    }
}
