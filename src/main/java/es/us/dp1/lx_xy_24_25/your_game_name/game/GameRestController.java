package es.us.dp1.lx_xy_24_25.your_game_name.game;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.dto.GameDTO;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ConflictException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.InvalidIndexOfTableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UnfeasibleToJumpTeam;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UnfeasibleToPlaceCard;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player.PlayerState;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PowerType;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.team.Team;
import es.us.dp1.lx_xy_24_25.your_game_name.team.TeamService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/games")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Game", description = "The Game API based on JWT")
class GameRestController {

    private final GameService gameService;
    private final UserService userService;
    private final PlayerService playerService;
    private final HandService handService;
    private final TableCardService tableService;
    private final PackCardService packCardService;
    private final CardService cardService;
    private final TeamService teamService;

    @Autowired
    public GameRestController(GameService gameService, UserService userService, 
        PlayerService playerService, HandService handService, 
        TableCardService tableService, PackCardService packCardService, 
        CardService cardService, TeamService teamService){
        this.gameService = gameService;
        this.userService = userService;
        this.playerService = playerService;
        this.handService = handService;
        this.tableService = tableService;
        this.packCardService = packCardService;
        this.cardService = cardService;
        this.teamService = teamService;
    }

    @InitBinder("game")
    public void initGameBinder(WebDataBinder dataBinder) {
        dataBinder.setValidator(new GameValidator());
    }

    @GetMapping
    public ResponseEntity<List<GameDTO>> findAll(){
        List<Game> games = (List<Game>) gameService.findAll();
        List<GameDTO> res = games.stream().map(g -> GameDTO.convertGameToDTO(g)).collect(Collectors.toList());
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Game> create(@RequestParam(required = true) Boolean isPublic, 
        @RequestParam(required = true) Integer numPlayers, @RequestParam(required = true) @Valid GameMode gameMode) {
        Game newGame = new Game();
        User currentUser = userService.findCurrentUser();
        Hand initialUserHand = handService.saveVoidHand();
        Player userPlayer = playerService.saveUserPlayerbyUser(currentUser,initialUserHand);
        newGame.setHost(currentUser);
        newGame.setPlayers(new ArrayList<>(List.of(userPlayer)));
        newGame.setIsPublic(isPublic);
        newGame.setNumPlayers(numPlayers);
        newGame.setGameMode(gameMode);
        if (gameMode.equals(GameMode.TEAM_BATTLE)) {
            Team team1 = teamService.saveTeamByPlayers(userPlayer, null);
            newGame.setTeams(new ArrayList<>(List.of(team1)));
        }
        Game savedGame = gameService.saveGame(newGame);
        return new ResponseEntity<>(savedGame, HttpStatus.CREATED);
    }
    
    @GetMapping("/current")
    public ResponseEntity<List<GameDTO>> findJoinableGames(){
        List<Game> games = gameService.findJoinableGames();
        List<GameDTO> res = games.stream().map(g -> GameDTO.convertGameToDTO(g)).collect(Collectors.toList());
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping("/{gameCode}/chat")
    public ResponseEntity<List<ChatMessage>> findGameChat(@PathVariable("gameCode") @Valid String gameCode){
        List<ChatMessage> res = gameService.getGameChat(gameCode);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }


    @GetMapping(value = "{gameCode}")
    public ResponseEntity<GameDTO> findGameByGameCode(@PathVariable("gameCode") @Valid String gameCode ) throws ConflictException, UnfeasibleToJumpTeam {
        try {
            Game game = gameService.findGameByGameCode(gameCode);
            User currentUser = userService.findCurrentUser();
            gameService.manageGame(game, currentUser);
            if (game.getGameState().equals(GameState.END) &&
                    Duration.between(game.getStarted(), LocalDateTime.now()).toMinutes() < 1) {
                game.getPlayers().stream().map(p -> p.getUser()).forEach(u -> userService.updateUser(u, u.getId()));
                // Actualizamos los users para actualizar racha de victorias
            }
            GameDTO gameDTO = GameDTO.convertGameToDTO(game);
            return new ResponseEntity<>(gameDTO, HttpStatus.OK);            
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new ConflictException("Another transaction has modified the game. Please retry.");
        }
    }

    @PatchMapping("/{gameCode}/joinAsPlayer")
    public ResponseEntity<MessageResponse> joinAsPlayer(@PathVariable("gameCode") @Valid String gameCode) throws ConflictException {
        try {
            Game game = gameService.findGameByGameCode(gameCode);
            User user = userService.findCurrentUser();
            if (game.getGameState().equals(GameState.WAITING)
                && game.getPlayers().size() < game.getNumPlayers()
                && game.getPlayers().stream().allMatch(p -> !p.getUser().equals(user))
                && game.getSpectators().stream().allMatch(p -> !p.getUser().equals(user))) {
                    Hand initialUserHand = handService.saveVoidHand();
                    Player userPlayer = playerService.saveUserPlayerbyUser(user,initialUserHand);
                    game.getPlayers().add(userPlayer);
                    if (game.getGameMode().equals(GameMode.TEAM_BATTLE)) {
                        game = teamService.joinATeam(game, userPlayer);
                    }
                    gameService.updateGame(game);
                    return new ResponseEntity<>(new MessageResponse("You have joined successfully"), HttpStatus.ACCEPTED);
            } else {
                throw new AccessDeniedException("You can't join this room");
            }            
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new ConflictException("Another transaction has modified the game. Please retry.");
        }
    }

    @PatchMapping("/{gameCode}/joinAsSpectator")
    public ResponseEntity<MessageResponse> joinAsSpectator(@PathVariable("gameCode") @Valid String gameCode) {
        try{
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        if ((game.getGameState().equals(GameState.WAITING) || game.getGameState().equals(GameState.IN_PROCESS))
            && game.getPlayers().stream().allMatch(p -> !p.getUser().equals(user))
            && game.getSpectators().stream().allMatch(p -> !p.getUser().equals(user))) {
          
                if(game.getPlayers().stream().map(p->p.getUser()).filter(u->!u.equals(user)).anyMatch(u->!user.getFriends().contains(u)))
                    throw new AccessDeniedException("You are not friends with every player in this room");
                Hand initialUserHand = handService.saveVoidHand();
                Player userPlayer = playerService.saveUserPlayerbyUser(user,initialUserHand);
                userPlayer.setState(PlayerState.SPECTATING);
                game.getSpectators().add(userPlayer);
                gameService.updateGame(game);
                return new ResponseEntity<>(new MessageResponse("You have joined successfully"), HttpStatus.NO_CONTENT);

        } else {
            throw new AccessDeniedException("You can't join this room");
        }
        }
        catch(Exception e){
            return null;
        }
    }

    @PatchMapping("/{gameCode}/startGame")
    public ResponseEntity<MessageResponse> startGame(@PathVariable("gameCode") @Valid String gameCode) throws UnfeasibleToJumpTeam {
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        if (game.getGameState().equals(GameState.WAITING) && game.getHost().equals(user)) {
            if (game.getPlayers().size() == 1) {
                game.setGameMode(GameMode.PUZZLE_SINGLE);
            }
            game = gameService.checkTeamBattle(game, user);
            packCardService.creaPackCards(game.getPlayers());
            Integer tableCardId = tableService.creaTableCard(game.getPlayers()).getId();
            game.setNumPlayers(game.getPlayers().size());
            game.setStarted(LocalDateTime.now());
            game.setGameState(GameState.IN_PROCESS);
            game.setTable(tableService.findTableCard(tableCardId));
            gameService.updateGame(game);
            return new ResponseEntity<>(new MessageResponse("Game started!"), HttpStatus.ACCEPTED);
        } else {
            throw new AccessDeniedException("You can't start this game");
        }
    }

    @PatchMapping("/{gameCode}/leaveAsPlayer")
    public ResponseEntity<MessageResponse> leaveAsPlayer(@PathVariable("gameCode") @Valid String gameCode) throws ConflictException, UnfeasibleToJumpTeam {
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        if (game.getPlayers().stream().anyMatch(p -> p.getUser().equals(user))){
            Player player = game.getPlayers().stream().filter(p -> p.getUser().equals(user)).findFirst().get(); 
            if(game.getGameState().equals(GameState.WAITING)){
                game.getPlayers().remove(player);
                playerService.deletePlayer(player);
                }
            else if(game.getGameState().equals(GameState.IN_PROCESS) && player.getState() != PlayerState.LOST){
                player.setState(PlayerState.LOST);
                playerService.updatePlayer(player);
                gameService.manageGame(game, user);
            }
            return new ResponseEntity<>(new MessageResponse("You have left this game"), HttpStatus.ACCEPTED);
        } else {
            throw new AccessDeniedException("You can't leave this game");
        }
    }

    @PatchMapping("/{gameCode}/leaveAsSpectator")
    public ResponseEntity<MessageResponse> leaveAsSpectator(@PathVariable("gameCode") @Valid String gameCode) {
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        if (game.getSpectators().stream().anyMatch(p -> p.getUser().equals(user))) {
            Player player = game.getSpectators().stream().filter(p -> p.getUser().equals(user)).findFirst().get();
            game.spectators.remove(player);
            playerService.deletePlayer(player);
            return new ResponseEntity<>(new MessageResponse("You have left this game"), HttpStatus.ACCEPTED);
        } else {
            throw new AccessDeniedException("You can't leave this game");
        }
    }

    @PatchMapping("/{gameCode}/switchToSpectator")
    public ResponseEntity<MessageResponse> switchToSpectator(@PathVariable("gameCode") @Valid String gameCode){
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        if(game.getPlayers().stream().anyMatch(p -> p.getUser().equals(user)) &&
            (game.getGameState().equals(GameState.WAITING) || game.getGameState().equals(GameState.IN_PROCESS))
            && game.getPlayers().size() > 1){
                Player player = game.getPlayers().stream().filter(p -> p.getUser().equals(user)).findFirst().get();
                if(game.getPlayers().stream().filter(p->!p.equals(player)).map(p->p.getUser()).anyMatch(p->!player.getUser().getFriends().contains(p)))
                    throw new AccessDeniedException("You are not friends with every player in this room");
                player.setState(PlayerState.SPECTATING);
                game.players.remove(player);
                game.spectators.add(player);
                gameService.updateGame(game);
                return new ResponseEntity<>(new MessageResponse("You have succesfully switched to spectator"),HttpStatus.ACCEPTED);
        }
        else{
            throw new AccessDeniedException("You can't spectate this room right now");
        }
    }

    @PatchMapping("/{gameCode}/switchToPlayer")
    public ResponseEntity<MessageResponse> switchToPlayer(@PathVariable("gameCode") @Valid String gameCode){
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        if(game.getSpectators().stream().anyMatch(p -> p.getUser().equals(user)) && game.getPlayers().size() < game.getNumPlayers()
            && game.getGameState().equals(GameState.WAITING)){
                Player player = game.getSpectators().stream().filter(p -> p.getUser().equals(user)).findFirst().get();
                player.setState(PlayerState.PLAYING);
                game.getSpectators().remove(player);
                game.getPlayers().add(player);
                gameService.updateGame(game);
                return new ResponseEntity<>(new MessageResponse("You have succesfully switched to player"),HttpStatus.ACCEPTED);
        }
        else{
            throw new AccessDeniedException("You can't join this room right now");
        }
    }

    @PatchMapping("/{gameCode}/placeCard")
    public ResponseEntity<MessageResponse> placeCard(@PathVariable("gameCode") @Valid String gameCode, 
        Integer cardId, Integer index) throws UnfeasibleToPlaceCard, InvalidIndexOfTableCard, UnfeasibleToJumpTeam {
        Card cardToPlace = cardService.findCard(cardId);
        User currentUser = userService.findCurrentUser();
        gameService.placeCard(currentUser, gameCode, index, cardToPlace);
        return new ResponseEntity<>(new MessageResponse("You have placed the card successfully"), HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{gameCode}/useEnergy")
    public ResponseEntity<MessageResponse> useEnergy(@PathVariable("gameCode") @Valid String gameCode, @Valid @RequestParam(required = true)PowerType powerType)
        throws InvalidIndexOfTableCard, UnfeasibleToPlaceCard, UnfeasibleToJumpTeam {
        //Comprobamos condiciones para poder usar las energías
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        Player player = game.getPlayers().stream().filter(p -> p.getUser().equals(user)).findFirst().orElse(null);
        if (player == null) {
            throw new AccessDeniedException("You can't use energy, because you aren't in this game");
        }
        if (!game.getGameState().equals(GameState.IN_PROCESS) || game.getNTurn() < 3 
            || player.getEnergyUsedThisRound() || !game.getTurn().equals(player.getId()) || player.getEnergy() <= 0) {
            throw new AccessDeniedException("You can't use energy right now");
        }
        //Una vez comprobado gestionamos que energía se usa
        return gameService.manageUseOfEnergy(powerType, player, game);
    }

    @PatchMapping("/{gameCode}/changeInitialHand")//Permite cambiar tu mano inicial, en tu turno y en la primera ronda
    public ResponseEntity<MessageResponse> changeInitialHand(@PathVariable("gameCode") @Valid String gameCode) {
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        Player player = game.getPlayers().stream().filter(p -> p.getUser().equals(user)).findFirst().orElse(null);
        if (player == null) {
            throw new AccessDeniedException("You can't change your hand, because you aren't in this game");
        }
        if (game.getGameState().equals(GameState.IN_PROCESS)) {
            if (game.getNTurn() == 1 && playerService.findPlayer(game.getTurn()).equals(player) 
                && !player.getState().equals(PlayerState.LOST) && !player.getHandChanged()) {
                gameService.changeInitialHand(player);
            } else {
                throw new AccessDeniedException("You can't change your hand");
            }
        } else {
            throw new AccessDeniedException("You can't change your hand, because this game isn't in process");
        }
        return new ResponseEntity<>(new MessageResponse("You have changed your initial hand successfully"), HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{gameCode}/chat")
    public ResponseEntity<List<ChatMessage>> sendChatMessage(@PathVariable("gameCode") @Valid String gameCode, @RequestBody ChatMessage cm ) {
        Game game = gameService.findGameByGameCode(gameCode);
        User user = userService.findCurrentUser();
        if (game.getPlayers().stream().map(p -> p.getUser()).toList().contains(user) && cm.getUserName().equals(user.getUsername())) {
                game.getChat().add(cm);
                List<ChatMessage> newChat = gameService.updateGame(game).getChat();
                return new ResponseEntity<>(newChat, HttpStatus.ACCEPTED);
        } else {
            throw new AccessDeniedException("You can't chat in this room");
        }
    }

}
