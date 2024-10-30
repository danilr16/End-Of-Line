package es.us.dp1.lx_xy_24_25.your_game_name.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
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
import java.time.LocalDateTime;

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
        game.setHost(currentUser);
        game.setPlayers(new ArrayList<>(List.of(userPlayer)));
        Game savedGame = gameService.saveGame(game);
        return new ResponseEntity<>(savedGame, HttpStatus.CREATED);
    }
    
    @GetMapping("/current")
    public ResponseEntity<List<Game>> findJoinableGames(){
        List<Game> res = gameService.findJoinableGames();
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping(value = "{gameCode}")
    public ResponseEntity<Game> findGameByGameCode(@PathVariable("gameCode") String gameCode ){
        Game res = gameService.findGameByGameCode(gameCode);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping("/{gameCode}/players")
    public ResponseEntity<List<Player>> findPlayersByGameCode(@PathVariable("gameCode") String gameCode) {
        List<Player> players = gameService.findPlayersByGameCode(gameCode);
        return new ResponseEntity<>(players,HttpStatus.OK);
    }

    @PatchMapping("/{gameCode}/joinAsPlayer")
    public ResponseEntity<MessageResponse> joinAsPlayer(@PathVariable("gameCode") String gameCode) {
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        List<Player> players = (List<Player>) userService.findAllPlayerByUser(user);
        if (game.getGameState().equals(GameState.WAITING)
            && game.getPlayers().size() < game.getNumPlayers()
            && game.getPlayers().stream().allMatch(p -> !players.contains(p))
            && game.getSpectators().stream().allMatch(p -> !players.contains(p))) {
                Hand initialUserHand = handService.saveVoidHand();
                Player userPlayer = playerService.saveUserPlayerbyUser(user,initialUserHand);
                game.getPlayers().add(userPlayer);
                gameService.updateGame(game, game.getId());
                return new ResponseEntity<>(new MessageResponse("You have joined successfully"), HttpStatus.ACCEPTED);
        } else {
            throw new AccessDeniedException("You can't join the room");
        }
    }

    @PatchMapping("/{gameCode}/joinAsSpectator")
    public ResponseEntity<MessageResponse> joinAsSpectator(@PathVariable("gameCode") String gameCode) {
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        List<Player> players = (List<Player>) userService.findAllPlayerByUser(user);
        if ((game.getGameState().equals(GameState.WAITING) || game.getGameState().equals(GameState.IN_PROCESS))
            && game.getPlayers().stream().allMatch(p -> !players.contains(p))
            && game.getSpectators().stream().allMatch(p -> !players.contains(p))) {
                Hand initialUserHand = handService.saveVoidHand();
                Player userPlayer = playerService.saveUserPlayerbyUser(user,initialUserHand);
                game.getSpectators().add(userPlayer);
                gameService.updateGame(game, game.getId());
                return new ResponseEntity<>(new MessageResponse("You have joined successfully"), HttpStatus.ACCEPTED);
        } else {
            throw new AccessDeniedException("You can't join the room");
        }
    }

    @PatchMapping("/{gameCode}/startGame")
    public ResponseEntity<MessageResponse> startGame(@PathVariable("gameCode") String gameCode) {
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        if (game.getGameState().equals(GameState.WAITING) && game.getHost().equals(user)) {
            game.setNumPlayers(game.getPlayers().size());
            game.setStarted(LocalDateTime.now());
            game.setGameState(GameState.IN_PROCESS);
            packCardService.creaPackCards(game.getPlayers());
            TableCard tableCard = tableService.creaTableCard(game.getNumPlayers(), game.getGameMode(),
                    game.getPlayers());
            game.setTable(tableCard);
            gameService.updateGame(game, game.getId());
            return new ResponseEntity<>(new MessageResponse("Game started!"), HttpStatus.ACCEPTED);
        } else {
            throw new AccessDeniedException("You can't start this game");
        }
    }

    @Async
    public void espera() {
        try {
            System.out.println("iniciando");
            Thread.sleep(10000);
            System.out.println("iniciado");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Error durante la espera: " + e.getMessage());
        }
    }
}
