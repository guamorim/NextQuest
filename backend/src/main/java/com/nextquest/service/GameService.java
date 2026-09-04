package com.nextquest.service;

import com.nextquest.dto.CreateGameRequest;
import com.nextquest.dto.GameResponse;
import com.nextquest.exception.DuplicateResourceException;
import com.nextquest.exception.ResourceNotFoundException;
import com.nextquest.model.Game;
import com.nextquest.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

	private final GameRepository gameRepository;

	public GameService(GameRepository gameRepository) {
		this.gameRepository = gameRepository;
	}

	public GameResponse createGame(CreateGameRequest request) {
		if (gameRepository.existsByNameIgnoreCaseAndPlatformIgnoreCase(request.getName(), request.getPlatform())) {
			throw new DuplicateResourceException("Game already registered for this platform");
		}

		Game game = new Game(
				request.getName(),
				request.getGenre(),
				request.getPlatform(),
				request.getReleaseDate());

		Game savedGame = gameRepository.save(game);
		return new GameResponse(
				savedGame.getId(),
				savedGame.getName(),
				savedGame.getGenre(),
				savedGame.getPlatform(),
				savedGame.getReleaseDate());
	}

	public List<GameResponse> getAllGames() {
		return gameRepository.findAll()
				.stream()
				.map(this::toResponse)
				.toList();
	}

	public GameResponse getGameById(Long id) {
		Game game = gameRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Game not found: " + id));

		return toResponse(game);
	}

	private GameResponse toResponse(Game game) {
		return new GameResponse(
				game.getId(),
				game.getName(),
				game.getGenre(),
				game.getPlatform(),
				game.getReleaseDate());
	}
}
