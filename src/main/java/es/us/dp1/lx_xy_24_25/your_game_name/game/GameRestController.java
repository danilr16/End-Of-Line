package es.us.dp1.lx_xy_24_25.your_game_name.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCardService;
import java.util.ArrayList;

import java.util.List;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("api/v1/games")
@SecurityRequirement(name = "bearerAuth")
class GameRestController {

    private final GameService gameService;
    private final UserService userService;
    private final PlayerService playerService;
    private final HandService handService;
    private final TableCardService tableService;
    private final PackCardService packCardService;

    @Autowired
    public GameRestController(GameService gameService, UserService userService, 
        PlayerService playerService, HandService handService, 
        TableCardService tableService, PackCardService packCardService){
        this.gameService = gameService;
        this.userService = userService;
        this.playerService = playerService;
        this.handService = handService;
        this.tableService = tableService;
        this.packCardService = packCardService;
    }

    @GetMapping
    public ResponseEntity<List<Game>> findAll(){
        List<Game> res = (List<Game>) gameService.findAll();
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Game> create(@RequestBody @Valid Game game) {
        User currentUser = userService.findCurrentUser();
        Hand initialUserHand = handService.saveVoidHand();
        Player userPlayer = playerService.saveUserPlayerbyUser(currentUser,initialUserHand);
        TableCard tableCard = tableService.creaTableCard(game.getNumPlayers(), game.getGameMode(), userPlayer);
        packCardService.creaPackCard(userPlayer);
        game.setHost(currentUser);
        game.setPlayers(new ArrayList<>(List.of(userPlayer)));
        game.setTable(tableCard);
        Game savedGame = gameService.saveGame(game);
        return new ResponseEntity<>(savedGame, HttpStatus.CREATED);
    }
    
    @GetMapping("/current")
    public ResponseEntity<List<Game>> findJoinableGames(){
        List<Game> res = gameService.findJoinableGames();
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping("/createdGame")
    public ResponseEntity<Game> findCreatedGame(){
        User host = userService.findCurrentUser();
        Game game = gameService.findCreatedGameByHost(host);
        return new ResponseEntity<>(game,HttpStatus.OK);
    }

    @GetMapping(value = "{gameCode}")
    public ResponseEntity<Game> findGameByGameCode(@PathVariable("gameCode") String gameCode ){
        Game res = gameService.findGameByGameCode(gameCode);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }
}
