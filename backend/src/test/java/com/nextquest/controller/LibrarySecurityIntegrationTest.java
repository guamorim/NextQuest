package com.nextquest.controller;

import java.time.LocalDate;

import com.nextquest.model.Game;
import com.nextquest.model.LibraryEntry;
import com.nextquest.model.LibraryStatus;
import com.nextquest.model.User;
import com.nextquest.repository.GameRepository;
import com.nextquest.repository.LibraryEntryRepository;
import com.nextquest.repository.UserRepository;
import com.nextquest.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LibrarySecurityIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private LibraryEntryRepository libraryEntryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private User firstUser;
    private LibraryEntry secondUserEntry;
    private String firstUserToken;

    @BeforeEach
    void setUp() {
        libraryEntryRepository.deleteAll();
        userRepository.deleteAll();
        gameRepository.deleteAll();

        String encodedPassword = passwordEncoder.encode("coxinha123");

        firstUser = userRepository.save(
                new User(
                        "First User",
                        "first@gmail.com",
                        encodedPassword));

        User secondUser = userRepository.save(
                new User(
                        "Second User",
                        "second@gmail.com",
                        encodedPassword));

        Game game = gameRepository.save(
                new Game(
                        "Hades",
                        "Roguelike",
                        "PC",
                        LocalDate.of(2020, 9, 17)));

        secondUserEntry = libraryEntryRepository.saveAndFlush(
                new LibraryEntry(
                        secondUser,
                        game,
                        LibraryStatus.PLAYING));

        firstUserToken = jwtService.generateToken(firstUser.getId());
    }

    @AfterEach
    void tearDown() {
        libraryEntryRepository.deleteAll();
        userRepository.deleteAll();
        gameRepository.deleteAll();
}
    
    @Test
    void shouldRejectRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/api/library"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowRequestWithValidToken() throws Exception {
        mockMvc.perform(
                        get("/api/library")
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + firstUserToken
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldPreventUserFromReadingAnotherUsersEntry() throws Exception {
        mockMvc.perform(
                        get(
                                "/api/library/{entryId}",
                                secondUserEntry.getId()
                        )
                                .header(
                                        HttpHeaders.AUTHORIZATION,
                                        "Bearer " + firstUserToken
                                )
                )
                .andExpect(status().isNotFound());
    }
}
