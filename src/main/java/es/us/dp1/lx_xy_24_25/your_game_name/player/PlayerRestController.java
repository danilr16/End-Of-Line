package es.us.dp1.lx_xy_24_25.your_game_name.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.game.Game;

import java.util.List;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/players")
@SecurityRequirement(name = "bearerAuth")
public class PlayerRestController {

    private final PlayerService playerService;

    @Autowired
	public PlayerRestController(PlayerService playerService) {
		this.playerService = playerService;
	}

    @GetMapping("/{id}/games")
    public ResponseEntity<List<Game>> findAllGameByPlayer(@PathVariable("id") Integer id) {
        Player player = playerService.findPlayer(id);
        List<Game> games = (List<Game>) playerService.findAllGameByPlayer(player);
	 	return new ResponseEntity<>(games, HttpStatus.OK);
    }

    @PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Player> create(@RequestBody @Valid Player player) {
		Player savedPlayer = playerService.savePlayer(player);
		return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
	}
    
}
