package es.us.dp1.lx_xy_24_25.your_game_name.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player.PlayerState;
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

    @InitBinder("game")
    public void initGameBinder(WebDataBinder dataBinder) {
        dataBinder.setValidator(new GameValidator());
    }

    @GetMapping
    public ResponseEntity<List<Game>> findAll(){
        List<Game> res = (List<Game>) gameService.findAll();
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Game> create(@RequestBody @Valid Game game) {
        Game newGame = new Game();
        User currentUser = userService.findCurrentUser();
        Hand initialUserHand = handService.saveVoidHand();
        Player userPlayer = playerService.saveUserPlayerbyUser(currentUser,initialUserHand);
        newGame.setHost(currentUser);
        newGame.setPlayers(new ArrayList<>(List.of(userPlayer)));
        newGame.setIsPublic(game.getIsPublic());
        newGame.setNumPlayers(game.getNumPlayers());
        newGame.setGameMode(game.getGameMode());
        Game savedGame = gameService.saveGame(newGame);
        return new ResponseEntity<>(savedGame, HttpStatus.CREATED);
    }
    
    @GetMapping("/current")
    public ResponseEntity<List<Game>> findJoinableGames(){
        List<Game> res = gameService.findJoinableGames();
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping(value = "{gameCode}")
    public ResponseEntity<Game> findGameByGameCode(@PathVariable("gameCode") @Valid String gameCode ){
        Game game = gameService.findGameByGameCode(gameCode);
        if (game.getGameState().equals(GameState.IN_PROCESS)) {
            //Aquí se añade logica de partida (orden,rondas,etc)
        }
        return new ResponseEntity<>(game,HttpStatus.OK);
    }

    @PatchMapping("/{gameCode}/joinAsPlayer")
    public ResponseEntity<MessageResponse> joinAsPlayer(@PathVariable("gameCode") @Valid String gameCode) {
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        if (game.getGameState().equals(GameState.WAITING)
            && game.getPlayers().size() < game.getNumPlayers()
            && game.getPlayers().stream().allMatch(p -> !p.getUser().equals(user))
            && game.getSpectators().stream().allMatch(p -> !p.getUser().equals(user))) {
                Hand initialUserHand = handService.saveVoidHand();
                Player userPlayer = playerService.saveUserPlayerbyUser(user,initialUserHand);
                game.getPlayers().add(userPlayer);
                gameService.updateGame(game, game.getId());
                return new ResponseEntity<>(new MessageResponse("You have joined successfully"), HttpStatus.ACCEPTED);
        } else {
            throw new AccessDeniedException("You can't join this room");
        }
    }

    @PatchMapping("/{gameCode}/joinAsSpectator")
    public ResponseEntity<MessageResponse> joinAsSpectator(@PathVariable("gameCode") @Valid String gameCode) {
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        if ((game.getGameState().equals(GameState.WAITING) || game.getGameState().equals(GameState.IN_PROCESS))
            && game.getPlayers().stream().allMatch(p -> !p.getUser().equals(user))
            && game.getSpectators().stream().allMatch(p -> !p.getUser().equals(user))) {
                Hand initialUserHand = handService.saveVoidHand();
                Player userPlayer = playerService.saveUserPlayerbyUser(user,initialUserHand);
                game.getSpectators().add(userPlayer);
                gameService.updateGame(game, game.getId());
                return new ResponseEntity<>(new MessageResponse("You have joined successfully"), HttpStatus.ACCEPTED);
        } else {
            throw new AccessDeniedException("You can't join this room");
        }
    }

    @PatchMapping("/{gameCode}/startGame")
    public ResponseEntity<MessageResponse> startGame(@PathVariable("gameCode") @Valid String gameCode) {
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

    @PatchMapping("/{gameCode}/leaveAsPlayer")
    public ResponseEntity<MessageResponse> leaveAsPlayer(@PathVariable("gameCode") @Valid String gameCode) {
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        if (game.getGameState().equals(GameState.IN_PROCESS) &&
            game.getPlayers().stream().filter(p -> !p.getState().equals(PlayerState.LOST)).anyMatch(p -> p.getUser().equals(user))) {
                Player player = game.getPlayers().stream().filter(p -> p.getUser().equals(user)).findFirst().get();
                player.setState(PlayerState.LOST);
                playerService.updatePlayer(player, player.getId());
                return new ResponseEntity<>(new MessageResponse("You have left this game"), HttpStatus.ACCEPTED);
        } else {
            throw new AccessDeniedException("You can't leave this game");
        }
    }

    @PatchMapping("/{gameCode}/leaveAsSpectator")
    public ResponseEntity<MessageResponse> leaveAsSpectator(@PathVariable("gameCode") @Valid String gameCode) {
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        if (game.getGameState().equals(GameState.IN_PROCESS) &&
            game.getSpectators().stream().filter(p -> !p.getState().equals(PlayerState.LOST)).anyMatch(p -> p.getUser().equals(user))) {
                Player player = game.getSpectators().stream().filter(p -> p.getUser().equals(user)).findFirst().get();
                player.setState(PlayerState.LOST);
                playerService.updatePlayer(player, player.getId());
                return new ResponseEntity<>(new MessageResponse("You have left this game"), HttpStatus.ACCEPTED);
        } else {
            throw new AccessDeniedException("You can't leave this game");
        }
    }

    @PatchMapping("/{gameCode}/placeCard")
    public ResponseEntity<MessageResponse> placeCard(@PathVariable("gameCode") @Valid String gameCode, @Valid Card card, Integer f, Integer c) {
        return null;
    }

    @PatchMapping("/{gameCode}/useEnergy")
    public ResponseEntity<MessageResponse> useEnergy(@PathVariable("gameCode") @Valid String gameCode) {
        return null;
    }
}
