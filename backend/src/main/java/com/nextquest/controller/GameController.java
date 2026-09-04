package com.nextquest.controller;

import com.nextquest.dto.CreateGameRequest;
import com.nextquest.dto.GameResponse;
import com.nextquest.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

	private final GameService gameService;

	public GameController(GameService gameService) {
		this.gameService = gameService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public GameResponse createGame(@RequestBody @Valid CreateGameRequest request) {
		return gameService.createGame(request);
	}

	@GetMapping
	public List<GameResponse> getAllGames() {
		return gameService.getAllGames();
	}

	@GetMapping("/{id}")
	public GameResponse getGameById(@PathVariable Long id) {
		return gameService.getGameById(id);
	}
}
