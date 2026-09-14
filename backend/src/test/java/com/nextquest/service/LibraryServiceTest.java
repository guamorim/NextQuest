package com.nextquest.service;

import com.nextquest.dto.CreateLibraryEntryRequest;
import com.nextquest.dto.LibraryEntryResponse;
import com.nextquest.exception.DuplicateResourceException;
import com.nextquest.model.Game;
import com.nextquest.model.LibraryEntry;
import com.nextquest.model.LibraryStatus;
import com.nextquest.model.User;
import com.nextquest.repository.GameRepository;
import com.nextquest.repository.LibraryEntryRepository;
import com.nextquest.repository.UserRepository;
import com.nextquest.dto.UpdateLibraryEntryRequest;
import com.nextquest.exception.ResourceNotFoundException;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith (MockitoExtension.class)
public class LibraryServiceTest {
    
    @Mock 
    private LibraryEntryRepository libraryEntryRepository;

    @Mock 
    private GameRepository gameRepository;

    @Mock 
    private UserRepository userRepository;

    private LibraryService libraryService;

    private ValidatorFactory validatorFactory;

    @BeforeEach 
    public void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        libraryService = new LibraryService(libraryEntryRepository, gameRepository, userRepository,
                validatorFactory.getValidator());
    }
    
    @AfterEach
    public void tearDown() {
        validatorFactory.close();
    }

    private LibraryEntry existingLibraryEntry() {
        User user = mock(User.class);
        Game game = mock(Game.class);

        when(game.getId()).thenReturn(42L);
        when(game.getName()).thenReturn("Hades");

        LibraryEntry entry = new LibraryEntry(user, game, LibraryStatus.PLAYING);

        entry.setRating(3);
        entry.setHoursPlayed(new BigDecimal("10.00"));

        return entry;
    }
    
    @Test
    void shouldRejectRatingAboveFive() {
        UpdateLibraryEntryRequest request = new UpdateLibraryEntryRequest();

        request.setRating(6);

        assertThrows(ConstraintViolationException.class, () -> {
            libraryService.updateLibraryEntry(1L, 7L, request);
        });

        verifyNoInteractions(libraryEntryRepository);
    }

    @Test
    void shouldRejectRatingBelowOne() {
        UpdateLibraryEntryRequest request = new UpdateLibraryEntryRequest();

        request.setRating(0);

        assertThrows(ConstraintViolationException.class, () -> {
            libraryService.updateLibraryEntry(1L, 7L, request);
        });

        verifyNoInteractions(libraryEntryRepository);
    }

    @Test
    void shouldRejectNegativeHoursPlayed() {
        UpdateLibraryEntryRequest request = new UpdateLibraryEntryRequest();

        request.setHoursPlayed(new BigDecimal("-1.0"));

        assertThrows(ConstraintViolationException.class, () -> {
            libraryService.updateLibraryEntry(1L, 7L, request);
        });

        verifyNoInteractions(libraryEntryRepository);
    }

    @Test
    void shouldUpdateStatusRatingAndHoursPlayed() {
        LibraryEntry entry = existingLibraryEntry();

        BigDecimal expectedHours = new BigDecimal("40.50");

        UpdateLibraryEntryRequest request = new UpdateLibraryEntryRequest();
        request.setStatus(LibraryStatus.COMPLETED);
        request.setRating(5);
        request.setHoursPlayed(expectedHours);

        when(libraryEntryRepository.findByIdAndUserId(7L, 1L)).thenReturn(Optional.of(entry));

        LibraryEntryResponse response = libraryService.updateLibraryEntry(1L, 7L, request);

        assertEquals(LibraryStatus.COMPLETED, entry.getStatus());
        assertEquals(5, entry.getRating());
        assertEquals(expectedHours, entry.getHoursPlayed());

        assertEquals(LibraryStatus.COMPLETED, response.getStatus());
        assertEquals(5, response.getRating());
        assertEquals(expectedHours, response.getHoursPlayed());
    }

    @Test
    void shouldPreserveFieldsNotProvidedInUpdate() {
        LibraryEntry entry = existingLibraryEntry();

        UpdateLibraryEntryRequest request = new UpdateLibraryEntryRequest();

        request.setRating(4);

        when(libraryEntryRepository.findByIdAndUserId(7L, 1L)).thenReturn(Optional.of(entry));

        LibraryEntryResponse response = libraryService.updateLibraryEntry(1L, 7L, request);

        assertEquals(4, entry.getRating());
        assertEquals(LibraryStatus.PLAYING, entry.getStatus());
        assertEquals(new BigDecimal("10.00"), entry.getHoursPlayed());

        assertEquals(4, response.getRating());
        assertEquals(LibraryStatus.PLAYING, response.getStatus());
        assertEquals(new BigDecimal("10.00"), response.getHoursPlayed());
    }

    @Test
    void shouldAllowZeroHoursPlayed() {
        LibraryEntry entry = existingLibraryEntry();

        UpdateLibraryEntryRequest request = new UpdateLibraryEntryRequest();

        request.setHoursPlayed(BigDecimal.ZERO);

        when(libraryEntryRepository.findByIdAndUserId(7L, 1L)).thenReturn(Optional.of(entry));

        LibraryEntryResponse response = libraryService.updateLibraryEntry(1L, 7L, request);

        assertEquals(BigDecimal.ZERO, entry.getHoursPlayed());
        assertEquals(BigDecimal.ZERO, response.getHoursPlayed());
    }

    @Test 
    void shouldAddGameToLibrary() {
        User user = mock(User.class);
        Game game = mock(Game.class);

        CreateLibraryEntryRequest request = validCreateRequest();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(42L)).thenReturn(Optional.of(game));

        when(game.getId()).thenReturn(42L);
        when(game.getName()).thenReturn("Hades");

        when(libraryEntryRepository.existsByUserIdAndGameId(1L, 42L)).thenReturn(false);

        when(libraryEntryRepository.saveAndFlush(any(LibraryEntry.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LibraryEntryResponse response = libraryService.addGameToLibrary(1L, request);

        assertEquals(42L, response.getGame().getId());
        assertEquals("Hades", response.getGame().getName());
        assertEquals(LibraryStatus.PLAYING, response.getStatus());
        assertNull(response.getRating());
        assertNull(response.getHoursPlayed());

        ArgumentCaptor<LibraryEntry> entryCaptor = ArgumentCaptor.forClass(LibraryEntry.class);
        verify(libraryEntryRepository).saveAndFlush(entryCaptor.capture());

        LibraryEntry entryToSave = entryCaptor.getValue();

        assertSame(user, entryToSave.getUser());
        assertSame(game, entryToSave.getGame());
        assertEquals(LibraryStatus.PLAYING, entryToSave.getStatus());
    }
    
    @Test 
    void shouldRejectDuplicateGame() {
        User user = mock(User.class);
        Game game = mock(Game.class);

        CreateLibraryEntryRequest request = validCreateRequest();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(42L)).thenReturn(Optional.of(game));

        when(libraryEntryRepository.existsByUserIdAndGameId(1L, 42L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            libraryService.addGameToLibrary(1L, request);
        });

        verify(libraryEntryRepository, never()).saveAndFlush(any(LibraryEntry.class));
    }

    private CreateLibraryEntryRequest validCreateRequest() {
        CreateLibraryEntryRequest request = new CreateLibraryEntryRequest();
        request.setGameId(42L);
        request.setStatus(LibraryStatus.PLAYING);
        return request;
    }

    @Test 
    void shouldRejectUpdatingEntryFromAnotherUser() {
        UpdateLibraryEntryRequest request = new UpdateLibraryEntryRequest();

        request.setRating(5);

        when(libraryEntryRepository.findByIdAndUserId(7L, 8L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> libraryService.updateLibraryEntry(8L, 7L, request));

        verify(libraryEntryRepository).findByIdAndUserId(7L, 8L);
    }

    @Test 
    void shouldRemoveLibraryEntry() {
        LibraryEntry entry = mock(LibraryEntry.class);

        when(libraryEntryRepository.findByIdAndUserId(7L, 1L))
                .thenReturn(Optional.of(entry));

        libraryService.removeLibraryEntry(1L, 7L);

        verify(libraryEntryRepository).findByIdAndUserId(7L, 1L);

        verify(libraryEntryRepository).delete(entry);
    }
    
    @Test 
    void shouldRejectRemovingEntryFromAnotherUser() {
        when(libraryEntryRepository.findByIdAndUserId(7L, 8L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> libraryService.removeLibraryEntry(8L, 7L));

        verify(libraryEntryRepository).findByIdAndUserId(7L, 8L);

        verify(libraryEntryRepository, never()).delete(any(LibraryEntry.class));
    }

    @Test
    void shouldGetLibraryEntryForUser() {
        LibraryEntry entry = existingLibraryEntry();

        when(libraryEntryRepository.findByIdAndUserId(7L, 1L)).thenReturn(Optional.of(entry));

        LibraryEntryResponse response = libraryService.getLibraryEntry(1L, 7L);

        assertEquals(42L, response.getGame().getId());
        assertEquals("Hades", response.getGame().getName());
        assertEquals(LibraryStatus.PLAYING, response.getStatus());
        assertEquals(3, response.getRating());
        assertEquals(new BigDecimal("10.00"), response.getHoursPlayed());

        verify(libraryEntryRepository).findByIdAndUserId(7L, 1L);
    }

    @Test 
    void shouldRejectReadingEntryFromAnotherUser() {
        when(libraryEntryRepository.findByIdAndUserId(7L, 8L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> libraryService.getLibraryEntry(8L, 7L));

        verify(libraryEntryRepository).findByIdAndUserId(7L, 8L);
    }

    @Test 
    void shouldListUserLibrary() {
        LibraryEntry entry = existingLibraryEntry();

        when(userRepository.existsById(1L)).thenReturn(true);

        when(libraryEntryRepository.findAllByUserIdOrderByIdAsc(1L)).thenReturn(List.of(entry));

        List<LibraryEntryResponse> response = libraryService.getLibrary(1L);

        assertEquals(1, response.size());

        LibraryEntryResponse item = response.get(0);

        assertEquals(42L, item.getGame().getId());
        assertEquals("Hades", item.getGame().getName());
        assertEquals(LibraryStatus.PLAYING, item.getStatus());
        assertEquals(3, item.getRating());
        assertEquals(new BigDecimal("10.00"), item.getHoursPlayed());

        verify(libraryEntryRepository).findAllByUserIdOrderByIdAsc(1L);
    }

    @Test 
    void shouldReturnEmptyLibraryForExistingUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        when(libraryEntryRepository.findAllByUserIdOrderByIdAsc(1L)).thenReturn(List.of());

        List<LibraryEntryResponse> response = libraryService.getLibrary(1L);

        assertTrue(response.isEmpty());

        verify(libraryEntryRepository).findAllByUserIdOrderByIdAsc(1L);
    }

    @Test 
    void shouldRejectListingLibraryForNonexistentUser() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> libraryService.getLibrary(1L));

        verifyNoInteractions(libraryEntryRepository);
    }

}
