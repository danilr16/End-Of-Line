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
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.Output;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.InvalidIndexOfTableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.InvalidRotation;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UnfeasibleToPlaceCard;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player.PlayerState;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.Cell;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.CellService;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCardService;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.stream.Collectors;

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
    private final CardService cardService;
    private final CellService cellService;

    @Autowired
    public GameRestController(GameService gameService, UserService userService, 
        PlayerService playerService, HandService handService, 
        TableCardService tableService, PackCardService packCardService, CardService cardService,
        CellService cellService){
        this.gameService = gameService;
        this.userService = userService;
        this.playerService = playerService;
        this.handService = handService;
        this.tableService = tableService;
        this.packCardService = packCardService;
        this.cardService = cardService;
        this.cellService = cellService;
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

    @PatchMapping("/{gameCode}/placeCard")// Falta por añadir una comprobación para no poder colocar una carta en cualquier momento, solo en tu turno
    public ResponseEntity<MessageResponse> placeCard(@PathVariable("gameCode") @Valid String gameCode, Integer cardId, Integer f, Integer c) throws UnfeasibleToPlaceCard, InvalidIndexOfTableCard {
        Card savedCard = cardService.findCard(cardId);
        Game game = gameService.findGameByGameCode(gameCode);
        TableCard tableCard = game.getTable();
        User user = userService.findCurrentUser();
        Player player = game.getPlayers().stream().filter(p -> p.getUser().equals(user)).findFirst().orElse(null);
        if (player == null) {
            throw new AccessDeniedException("You can't place this card, because you aren't in this game");
        }
        Hand hand = player.getHand();
        if (f > tableCard.getNumRow() || f < 1) {
            throw new InvalidIndexOfTableCard("f is the number of rows and must be in range 1 to " + tableCard.getNumRow());
        }
        if (c > tableCard.getNumColum() || c < 1) {
            throw new InvalidIndexOfTableCard("c is the number of columns and must be in range 1 to " + tableCard.getNumColum());
        }
        Cell cell = tableCard.getRows().get(f-1).getCells().get(c-1);
        if (!savedCard.getPlayer().equals(player) || !player.getHand().getCards().contains(savedCard) 
            || !game.getGameState().equals(GameState.IN_PROCESS) || !player.getState().equals(PlayerState.PLAYING)) {
            throw new AccessDeniedException("You can't place this card");
        }
        Card lastPlacedCard = cardService.findCard(player.getPlayedCards().get(player.getPlayedCards().size()-1));
        if (!cardService.checkLineToPlaceCard(savedCard, lastPlacedCard, game.getTable(), player, f, c)) {
            throw new UnfeasibleToPlaceCard();
        }
        hand.getCards().remove(savedCard);
        hand.setNumCards(hand.getNumCards() - 1);
        handService.updateHand(hand, hand.getId());
        player.getPlayedCards().add(savedCard.getId());
        playerService.updatePlayer(player, player.getId());
        cell.setCard(savedCard);
        cell.setIsFull(true);
        cellService.updateCell(cell, cell.getId());
        return new ResponseEntity<>(new MessageResponse("You have placed the card successfully"), HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{gameCode}/useEnergy")
    public ResponseEntity<MessageResponse> useEnergy(@PathVariable("gameCode") @Valid String gameCode) {
        return null; //Aquí se añade el uso de poderes
    }

    @PatchMapping("/{gameCode}/rotateCard")
    public ResponseEntity<MessageResponse> rotateCard(@PathVariable("gameCode") @Valid String gameCode, Integer cardId, Integer rotation) throws InvalidRotation {
        if (rotation < 0) {
            throw new InvalidRotation();
        }
        Card savedCard = cardService.findCard(cardId);
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        Player player = game.getPlayers().stream().filter(p -> p.getUser().equals(user)).findFirst().orElse(null);
        if (player == null) {
            throw new AccessDeniedException("You can't place this card, because you aren't in this game");
        }
        if (!savedCard.getPlayer().equals(player) || !player.getHand().getCards().contains(savedCard) 
            || !game.getGameState().equals(GameState.IN_PROCESS) || !player.getState().equals(PlayerState.PLAYING)) {
            throw new AccessDeniedException("You can't rotate this card");
        }
        List<Integer> newOutputs = savedCard.getOutputs().stream()
            .map(o -> (o + rotation) % 4).collect(Collectors.toList());
        Integer newInput = rotation + savedCard.getRotation();
        savedCard.setRotation((rotation + savedCard.getRotation()) % 4);
        savedCard.setOutput(Output.of(newOutputs, newInput));
        savedCard.setOutputs(newOutputs);
        savedCard.setInput(newInput);
        cardService.updateCard(savedCard, savedCard.getId());
        return new ResponseEntity<>(new MessageResponse("You have rotated the card successfully"), HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{gameCode}/robarCartaProvisional")//Metodo provisional para probar backend, hay que borrarlo
    public ResponseEntity<MessageResponse> takeCard(@PathVariable("gameCode") @Valid String gameCode) {
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        Player player = game.getPlayers().stream().filter(p -> p.getUser().equals(user)).findFirst().get();
        gameService.takeACard(player);
        return new ResponseEntity<>(new MessageResponse("You have taken a card successfully"), HttpStatus.ACCEPTED);
    }
}
