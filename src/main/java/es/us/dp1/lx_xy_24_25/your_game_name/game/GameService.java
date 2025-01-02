package es.us.dp1.lx_xy_24_25.your_game_name.game;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;

import jakarta.validation.Valid;
import es.us.dp1.lx_xy_24_25.your_game_name.auth.payload.response.MessageResponse;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.TypeCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.AccessDeniedException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ConflictException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.InvalidIndexOfTableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UnfeasibleToJumpTeam;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.UnfeasibleToPlaceCard;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCard;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PowerType;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.Cell;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.CellService;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.team.Team;
import es.us.dp1.lx_xy_24_25.your_game_name.team.TeamService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player.PlayerState;
import java.time.Duration;
import java.util.Collections;
import java.time.LocalDateTime;

@Service
public class GameService {

    private GameRepository gameRepository;
    private PackCardService packCardService;
    private HandService handService;
    private PlayerService playerService;
    private CardService cardService;
    private TableCardService tableCardService;
    private CellService cellService;
    private TeamService teamService;

    @Autowired
    public GameService(GameRepository gameRepository, PackCardService packCardService, HandService handService
        , PlayerService playerService, CardService cardService, TableCardService tableCardService,
        CellService cellService, TeamService teamService){
        this.gameRepository = gameRepository;
        this.packCardService = packCardService;
        this.handService = handService;
        this.playerService = playerService;
        this.cardService = cardService;
        this.tableCardService = tableCardService;
        this.cellService = cellService;
        this.teamService = teamService;
    }

    @Transactional(readOnly = true)
    public Iterable<Game> findAll() {
        return gameRepository.findAll();

    }

    @Transactional
    public Game saveGame(Game game) throws DataAccessException {
        gameRepository.save(game);
        return game;
    }

    @Transactional(readOnly = true)
    public Game findGame(Integer id){
        return gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game","id",id));
    }

    @Transactional
    public Game findGameByGameCode(String gameCode){
        Game rawGame = gameRepository.findGameByGameCode(gameCode).orElseThrow(() -> new ResourceNotFoundException("Game","gameCode",gameCode));
       return rawGame;
    }

    @Transactional(readOnly = true)
    public List<Game> findJoinableGames(){
        List<GameState> validStates = List.of(GameState.IN_PROCESS,GameState.WAITING);
        return gameRepository.findByGameStateIn(validStates);
    }

    @Transactional
    public Game updateGame(@Valid Game game) {
        gameRepository.save(game);
        return game;
    }

    @Transactional
    public void deleteGame(Integer id) {
        Game toDelete = this.findGame(id);
        this.gameRepository.delete(toDelete);
    }


    @Transactional(readOnly = true)
    public List<ChatMessage> getGameChat(String gameCode){
        return gameRepository.findGameChat(gameCode).orElseThrow(() -> new ResourceNotFoundException("Game","gameCode",gameCode));
    }
    @Transactional
    public List<ChatMessage> sendChatMessage(ChatMessage cm) {
        Game game = gameRepository.findGameByGameCode(cm.getGameCode()).orElseThrow(() -> new ResourceNotFoundException("Game","gameCode",cm.getGameCode()));
        game.getChat().add(cm);
        List<ChatMessage> newChat = game.getChat();
        this.updateGame(game);
        return newChat;

    }

    //Par para devolver player y card en takeCard para el test
    public static class Pair<P, C> {
        private Player player;
        private Card card;

        public Pair(Player p, Card c) {
            this.player = p;
            this.card = c;
        }

        public Player getPlayer() {
            return player;
        }

        public void setPlayer(Player p) {
            this.player = p;
        }

        public Card getCard() {
            return card;
        }

        public void setSecond(Card c) {
            this.card = c;
        }
    }

    @Transactional
    public Pair<Player,Card> takeACard(Player player) {
        PackCard packCard = player.getPackCards().stream().findFirst().get();
        SecureRandom rand = new SecureRandom();
        Integer i = rand.nextInt(packCard.getNumCards());
        Card card = packCard.getCards().get(i);
        packCard.getCards().remove(card);
        packCard.setNumCards(packCard.getNumCards() - 1);
        packCardService.updatePackCard(packCard, packCard.getId());
        Hand hand = player.getHand();
        hand.getCards().add(card);
        hand.setNumCards(hand.getNumCards() + 1);
        handService.updateHand(hand, hand.getId());
        // Actualizo player para devolverlo junto a la carta
        List<PackCard> pcUpdated = new ArrayList<>();
        pcUpdated.add(packCard);
        player.setPackCards(pcUpdated);
        player.setHand(hand);
        Pair<Player, Card> res = new Pair<>(player, card);
        return res;
    }

    @Transactional
    public Game initialTurn(Game game) {//Decide el turno inicial de partida
        List<Integer> players = game.getPlayers().stream().filter(p -> !p.getState().equals(PlayerState.LOST))
            .map(p -> p.getId()).collect(Collectors.toList());
        Collections.shuffle(players);
        game.setTurn(players.stream().findFirst().get());
        game.setOrderTurn(players);
        game.setInitialTurn(players);
        return game; //Se ha añadido que devuelva game para la comprobación de los test
    }

    @Transactional
    public Game decideTurns(Game game, List<Player> ls) {//Decido los turnos a partir de la ronda 1
        List<Integer> orderTurn = decideTurns(game, ls, new ArrayList<>(), 0);
        Integer turn = orderTurn.stream().findFirst().get();
        Player start = playerService.findPlayer(turn);
        start.setTurnStarted(LocalDateTime.now());
        playerService.updatePlayer(start);
        game.setTurn(turn);
        game.setOrderTurn(orderTurn);
        return game; //Se ha añadido que devuelva game para la comprobación de los test
    }

    private List<Integer> decideTurns(Game game, List<Player> ls, List<Integer> orderTurn, Integer indexCard) {
        List<Player> players = new ArrayList<>(ls);
        List<Card> cards = new ArrayList<>();
        for (Player player: players) {//Cogemos las ultimas cartas jugadas de cada jugador o las anteriores (llamada recursiva que va comparando cartas hasta el caso base, llegada a nodo de inicio)
            Card card = cardService.findCard(player.getPlayedCards().get(player.getPlayedCards().size() - 1 - indexCard));
            if (card.getType().equals(TypeCard.INICIO)) {
                List<Integer> res = game.getInitialTurn().stream().map(id -> playerService.findPlayer(id))//Conseguir el orden de la ronda inicial de los players
                    .filter(p -> players.contains(p)).map(p -> p.getId()).collect(Collectors.toList());   //que quedan por decidir
                return res;
            }
            cards.add(card);
        }
        cards.sort(Comparator.comparing(Card::getIniciative));//Las ordenamos de menor a mayor iniciativa
        Integer maxIniciative = cards.get(cards.size()-1).getIniciative();
        Map<Integer, List<Player>> iniciative_player = cards.stream().collect(Collectors.groupingBy(Card::getIniciative, //Keys las iniciativas sacadas y valores lista de los jugadores que la han sacado
            Collectors.mapping(Card::getPlayer, Collectors.toList())));
        for(int i=maxIniciative; i >= 0; i--) {//Si varios jugadores tienen la misma iniciativa, se hace llamada recursiva para decidir el orden entre esos jugadores y si no se añade a la lista del orden final
            if (iniciative_player.get(i) != null) {
                List<Player> sameIniciative = iniciative_player.get(i);
                if (sameIniciative.size() == 1) {
                    orderTurn.add(0, sameIniciative.stream().findFirst().get().getId());
                } else {
                    List<Integer> newOrder = decideTurns(game, new ArrayList<>(sameIniciative), new ArrayList<>(), indexCard + 1);
                    if (newOrder.equals(game.getInitialTurn())) {//Si en algun momento se llega al caso base se devuelve el order de la ronda 1
                        return game.getInitialTurn();
                    } else {
                        orderTurn.addAll(0, newOrder);
                    }
                }
            }
        }
        return orderTurn;
    }

    @Transactional
    public void returnCards(Player player) {//Devuelve todas las cartas de la mano de un jugador al mazo
        Hand hand = player.getHand();
        List<Card> cards = hand.getCards();
        PackCard packCard = player.getPackCards().stream().findFirst().get();
        packCard.getCards().addAll(cards);
        packCard.setNumCards(packCard.getNumCards() + cards.size());
        hand.setNumCards(hand.getNumCards() - cards.size());
        hand.getCards().removeAll(cards);
        packCardService.updatePackCard(packCard, packCard.getId());
        handService.updateHand(hand, hand.getId());
    }

    @Transactional
    public void changeInitialHand(Player player) {//Cambia la mano de un jugador
        this.returnCards(player);
        for (int i = 1; i <= 5; i++) {
            this.takeACard(player);
        }
        player.setHandChanged(true);
        playerService.updatePlayer(player);
    }

    @Transactional
    public void manageTurnOfPlayer(Game game, Player playing) throws ConflictException, UnfeasibleToJumpTeam { //Gestiona las acciones del jugador al que le toca
        if (playing.getCardsPlayedThisTurn() >= 2 || (playing.getCardsPlayedThisTurn() >= 1 && game.getNTurn() == 1)) {
            nextTurn(game, playing);
        } else {
            if (playing.getTurnStarted() == null) {
                throw new ConflictException("Another transaction has modified the player. Please retry.");
            }
            if (Duration.between(playing.getTurnStarted(), LocalDateTime.now()).toMinutes() >= 2 || cantContinuePlaying(game, playing)) {
                playing.setState(PlayerState.LOST);
                playerService.updatePlayer(playing);
            }
        }
    }

    public Boolean cantContinuePlaying(Game game, Player playing) throws UnfeasibleToJumpTeam {//si el jugador no tiene acciones posibles actuales y no puede usar poder
        TableCard tableCard = game.getTable();
        Card lastPlaced = cardService.getLastPlaced(playing);
        List<Map<String,Integer>> possiblePositions = tableCardService.getPossiblePositionsForPlayer(tableCard, playing, lastPlaced, null, false);
        if (playing.getPackCards().stream().findFirst().get().getNumCards() == 0 && playing.getHand().getNumCards() == 0) {// Si te quedas sin cartas para colocar pierdes
            return true;
        } else {
            if (possiblePositions.isEmpty() && playing.getEnergy() == 0) {
                return true;
            } else if (possiblePositions.isEmpty() && game.getNTurn() >= 3) {
                if (!playing.getEnergyUsedThisRound()) {
                    return cantUsePowers(playing, game, tableCard, lastPlaced);
                } else {
                    PowerType pwr = playing.getUsedPowers().get(playing.getUsedPowers().size()-1);
                    return !(pwr.equals(PowerType.BACK_AWAY) || pwr.equals(PowerType.JUMP_TEAM));
                }
            } else if (possiblePositions.isEmpty() && game.getNTurn() < 3) {
                return true;
            } else {
                return false;
            }
        }
    }

    private Boolean cantUsePowers(Player playing, Game game, TableCard tableCard, Card lastPlaced) throws UnfeasibleToJumpTeam {
        Boolean canGoBack = false;
        Boolean canJump = false;
        if (playing.getPlayedCards().size() >= 2) {//Comprobar que puedes usar back_away
            Card possibleGoBack = cardService.findCard(playing.getPlayedCards().get(playing.getPlayedCards().size()-2));
            if (!tableCardService.getPossiblePositionsForPlayer(tableCard, playing, possibleGoBack, null, false).isEmpty()) {
                canGoBack = true;
            }
        }
        if (game.getGameMode().equals(GameMode.TEAM_BATTLE)) {//Si estamos en Team Battle comprobamos si el jugador puede saltar a su compañero
            Team team = game.getTeams().stream().filter(t -> t.getPlayer1().equals(playing) || t.getPlayer2().equals(playing))
                .findFirst().get();
            Player playerTeam = null;
            if (team.getPlayer1().equals(playing)) {
                playerTeam = team.getPlayer2();
            } else {
                playerTeam = team.getPlayer1();
            }
            try {
                if (!tableCardService.getPossiblePositionsForPlayer(tableCard, playing, lastPlaced, playerTeam, true).isEmpty()) {
                    canJump = true;
                }
            } catch (UnfeasibleToJumpTeam ex) {
                canJump = false;
            }
        }
        return !(canGoBack || canJump);
    }

    @Transactional
    public void nextTurn(Game game, Player playing) {//Lógica para turno del siguiente jugador o empiza siguiente ronda
        Integer i = game.getOrderTurn().indexOf(game.getTurn());
        playing.setCardsPlayedThisTurn(0);
        playing.setTurnStarted(null);
        playing.setEnergyUsedThisRound(false);
        playerService.updatePlayer(playing);
        if (i.equals(game.getOrderTurn().size()-1)) {
            game.setNTurn(game.getNTurn() + 1);
            List<Player> players = game.getPlayers().stream().filter(p -> !p.getState().equals(PlayerState.LOST))
                .collect(Collectors.toList());
            if (!players.isEmpty()) {
                game = decideTurns(game, players);
            }
            for (Player player : players) {
                Hand hand = player.getHand();
                PackCard packCard = player.getPackCards().stream().findFirst().get();
                while (hand.getNumCards() < 5 && packCard.getNumCards() > 0) {
                    this.takeACard(player);
                }
            }
        } else {
            i++;
            Player nextPlaying = playerService.findPlayer(game.getOrderTurn().get(i));
            nextPlaying.setTurnStarted(LocalDateTime.now());
            playerService.updatePlayer(nextPlaying);
            game.setTurn(nextPlaying.getId());
        }
        this.updateGame(game);
    }

    @Transactional
    private void turn0(Game game, List<Player> players, User currentUser) {
        for (Player player : players) {
            Hand hand = player.getHand();
            while (hand.getNumCards() < 5) {
                this.takeACard(player);
            }
        }
        Boolean todosConCartas = true;
        for (Player player : players) {
            if (player.getHand().getNumCards() != 5) {
                todosConCartas = false;
                break;
            }
        }
        if (todosConCartas) {
            game = initialTurn(game);
            Player start = playerService.findPlayer(game.getTurn());
            start.setTurnStarted(LocalDateTime.now());
            playerService.updatePlayer(start);
            game.setNTurn(1);
            this.updateGame(game);
        }
    }

    @Transactional
    public void gameInProcess(Game game, User currentUser) throws ConflictException, UnfeasibleToJumpTeam {// Lógica de gestión de una partida VERSUS en progreso
        List<Player> players = game.getPlayers().stream().filter(p -> !p.getState().equals(PlayerState.LOST))
                .collect(Collectors.toList());
        if (game.getNTurn() == 0) {
            this.turn0(game, players, currentUser);
        } else {
            if (players.size() == 1) {
                Player winner = players.stream().findFirst().get();
                winner.setState(PlayerState.WON);
                playerService.updatePlayer(winner);
                updateStreaks(game);
                game.setGameState(GameState.END);
                game.setDuration(Duration.between(game.getStarted(), LocalDateTime.now()).toMinutesPart());
                this.updateGame(game);
            } else {
                Player playing = playerService.findPlayer(game.getTurn());
                manageTurnOfPlayer(game, playing);
                if (playing.getState().equals(PlayerState.LOST)) {
                    nextTurn(game, playing);
                }
            }
        }
    }

    @Transactional
    public void gameInProcessSingle(Game game, User currentUser) throws ConflictException, UnfeasibleToJumpTeam {
        List<Player> players = game.getPlayers().stream().filter(p -> !p.getState().equals(PlayerState.LOST))
            .collect(Collectors.toList());
        if (game.getNTurn() == 0) {
            this.turn0(game, players, currentUser);
        } else {
            if (players.isEmpty()) {
                game.setGameState(GameState.END);
                game.setDuration(Duration.between(game.getStarted(), LocalDateTime.now()).toMinutesPart());
                this.updateGame(game);
            } else if (tableCardService.tableCardFull(game.getTable())) {
                Player winner = players.stream().findFirst().get();
                winner.setState(PlayerState.WON);
                Integer sumInicHand = winner.getHand().getCards().stream().mapToInt(c -> c.getIniciative()).sum();
                winner.setScore(sumInicHand + winner.getEnergy());
                playerService.updatePlayer(winner);
                updateStreaks(game);
                game.setGameState(GameState.END);
                game.setDuration(Duration.between(game.getStarted(), LocalDateTime.now()).toMinutesPart());
                this.updateGame(game);
            } else {
                Player playing = playerService.findPlayer(game.getTurn());
                manageTurnOfPlayer(game, playing);
            }
        }
    }

    @Transactional
    public void gameInProcessCoop(Game game, User currentUser) throws ConflictException, UnfeasibleToJumpTeam {//Revisar se puede jugar
        List<Player> players = game.getPlayers().stream().filter(p -> !p.getState().equals(PlayerState.LOST))
            .collect(Collectors.toList());
        if (game.getNTurn() == 0) {
            this.turn0(game, players, currentUser);
        } else {
            if (players.size() < 2) {
                for (Player loser:players) {
                    loser.setState(PlayerState.LOST);
                    playerService.updatePlayer(loser);
                }
                updateStreaks(game);
                game.setGameState(GameState.END);
                game.setDuration(Duration.between(game.getStarted(), LocalDateTime.now()).toMinutesPart());
                this.updateGame(game);
            } else if (tableCardService.tableCardFull(game.getTable())) {
                for (Player winner:players) {
                    winner.setState(PlayerState.WON);
                    Integer sumInicHand = winner.getHand().getCards().stream().mapToInt(c -> c.getIniciative()).sum();
                    winner.setScore(sumInicHand + winner.getEnergy());
                    playerService.updatePlayer(winner);
                }
                game.setGameState(GameState.END);
                game.setDuration(Duration.between(game.getStarted(), LocalDateTime.now()).toMinutesPart());
                this.updateGame(game);
            } else {
                Player playing = playerService.findPlayer(game.getTurn());
                manageTurnOfPlayer(game, playing);
                if (playing.getState().equals(PlayerState.LOST)) {
                    nextTurn(game, playing);
                }
            }
        }
    }

    @Transactional
    public void gameInProcessTeam(Game game, User currentUser) throws ConflictException, UnfeasibleToJumpTeam {
        List<Player> players = game.getPlayers().stream().filter(p -> !p.getState().equals(PlayerState.LOST))
                .collect(Collectors.toList());
        List<Team> teams = game.getTeams().stream().filter(t -> !t.lostTeam()).collect(Collectors.toList());
        if (game.getNTurn() == 0) {
            this.turn0(game, players, currentUser);
        } else {
            if (teams.size() == 1) {
                Team winners = teams.stream().findFirst().get();
                winners.getPlayer1().setState(PlayerState.WON);
                winners.getPlayer2().setState(PlayerState.WON);
                playerService.updatePlayer(winners.getPlayer1());
                playerService.updatePlayer(winners.getPlayer2());
                int t = game.getTeams().size()-1;
                while (t >= 0) {
                    Team team = game.getTeams().remove(t);
                    teamService.deleteTeam(team);
                    t--;
                }
                updateStreaks(game);
                game.setGameState(GameState.END);
                game.setDuration(Duration.between(game.getStarted(), LocalDateTime.now()).toMinutesPart());
                this.updateGame(game);
            } else {
                Player playing = playerService.findPlayer(game.getTurn());
                manageTurnOfPlayer(game, playing);
                if (playing.getState().equals(PlayerState.LOST)) {
                    nextTurn(game, playing);
                }
            }
        }
    }

    @Transactional
    public void useAccelerate(Player player) {
        player.setEnergy(player.getEnergy()-1);
        player.setEnergyUsedThisRound(true);
        player.setCardsPlayedThisTurn(player.getCardsPlayedThisTurn() - 1);
        player.getUsedPowers().add(PowerType.ACCELERATE);
        playerService.updatePlayer(player);
    }

    @Transactional
    public void useBrake(Player player) {
        player.setEnergy(player.getEnergy()-1);
        player.setEnergyUsedThisRound(true);
        player.setCardsPlayedThisTurn(player.getCardsPlayedThisTurn() + 1);
        player.getUsedPowers().add(PowerType.BRAKE);
        playerService.updatePlayer(player);
    }

    @Transactional
    public void useBackAway(Player player, List<Map<String, Integer>> newPossiblePositions) {
        player.setEnergy(player.getEnergy()-1);
        player.setEnergyUsedThisRound(true);
        player.getUsedPowers().add(PowerType.BACK_AWAY);
        List<Integer> positions = new ArrayList<>();
        List<Integer> rotations = new ArrayList<>();
        for (Map<String,Integer> mp:newPossiblePositions) {
            positions.add(mp.get("position"));
            rotations.add(mp.get("rotation"));
        }
        player.setPossiblePositions(positions);
        player.setPossibleRotations(rotations);
        playerService.updatePlayer(player);
    }

    @Transactional
    public void useExtraGas(Player player) {
        player.setEnergy(player.getEnergy()-1);
        player.setEnergyUsedThisRound(true);
        this.takeACard(player);
        player.getUsedPowers().add(PowerType.EXTRA_GAS);
        playerService.updatePlayer(player);
    }

    @Transactional
    public void useJumpTeam(Player player, List<Map<String, Integer>> newPossiblePositions) {
        player.setEnergy(player.getEnergy()-1);
        player.setEnergyUsedThisRound(true);
        player.getUsedPowers().add(PowerType.JUMP_TEAM);
        List<Integer> positions = new ArrayList<>();
        List<Integer> rotations = new ArrayList<>();
        for (Map<String,Integer> mp:newPossiblePositions) {
            positions.add(mp.get("position"));
            rotations.add(mp.get("rotation"));
        }
        player.setPossiblePositions(positions);
        player.setPossibleRotations(rotations);
        playerService.updatePlayer(player);
    }

    @Transactional
    public void placeCard(User currentUser, String gameCode, Integer index, Card cardToPlace) 
        throws InvalidIndexOfTableCard, UnfeasibleToPlaceCard, UnfeasibleToJumpTeam {
        Game currentGame = this.findGameByGameCode(gameCode);
        TableCard currentTable = currentGame.getTable();
        //Buscamos el jugador asociado al usuario actual
        Player currentPlayer = currentGame.getPlayers().stream().filter(p -> p.getUser().equals(currentUser)).findFirst().orElse(null);
        //Excepciones relacionadas con permisos y roles
        checkConditionsPlaceCard(currentPlayer, cardToPlace, currentGame, currentTable, index);

        // A continuación comprobamos que la posición de la carta está entre las posibles
        List<Map<String, Integer>> possiblePositions = tableCardService.fromListsToPossiblePositions(currentPlayer.getPossiblePositions(), 
            currentPlayer.getPossibleRotations());
        Boolean cardCanBePlaced = false; 
        Integer i = null;
        for(Map<String, Integer> position: possiblePositions){
            if(position.get("position").equals(index)) {
                cardCanBePlaced = true; 
                i = possiblePositions.indexOf(position);
                break;
            }
        }

        if(!cardCanBePlaced) throw new UnfeasibleToPlaceCard();
        //Una vez que hemos comprobado que se puede colocar la carta actualizamos los datos correspondientes en la base de datos
        updatePlaceCard(possiblePositions, cardToPlace, currentPlayer, currentTable, index, i);
    }
    
    private void checkConditionsPlaceCard(Player currentPlayer, Card cardToPlace, Game currentGame, 
        TableCard currentTable, Integer index) throws InvalidIndexOfTableCard {
        if (currentPlayer == null) {
            throw new AccessDeniedException("You can't place this card, because you aren't in this game");
        }
        if (!cardToPlace.getPlayer().equals(currentPlayer) || !currentPlayer.getHand().getCards().contains(cardToPlace) 
            || !currentGame.getGameState().equals(GameState.IN_PROCESS) || !currentPlayer.getState().equals(PlayerState.PLAYING)) {
            throw new AccessDeniedException("You can't place this card");
        }
        //Excepcion del index del tablero
        if(index > currentGame.getTable().getNumColum()*currentGame.getTable().getNumRow() || index <= 0){
            throw new InvalidIndexOfTableCard("The number of the index cant be superior to:" + currentGame.getTable().getNumColum()*currentGame.getTable().getNumRow() + "or lower equals to 0");
        }
        // Comprobamos que es el turno del jugador y puede colocar carta
        Player turnOfPlayer = playerService.findPlayer(currentGame.getTurn());
        if (!turnOfPlayer.equals(currentPlayer) || !(currentPlayer.getCardsPlayedThisTurn() < 2) || 
            (!(currentPlayer.getCardsPlayedThisTurn() < 1) && currentGame.getNTurn() == 1)) {
            throw new AccessDeniedException("You can't place this card, because it's not your turn");
        }
    }

    @Transactional
    private void updatePlaceCard(List<Map<String, Integer>> possiblePositions, Card cardToPlace, 
        Player currentPlayer, TableCard currentTable, Integer index, Integer i) throws UnfeasibleToJumpTeam {
        Integer rotationToPlace = possiblePositions.get(i).get("rotation");
        cardToPlace.setRotation(rotationToPlace);
        cardService.updateCard(cardToPlace, cardToPlace.getId());
        Hand playerHand = currentPlayer.getHand();
        playerHand.getCards().remove(cardToPlace);
        playerHand.setNumCards(playerHand.getNumCards() - 1);
        handService.updateHand(playerHand, playerHand.getId());
        Integer c = Math.floorMod(index-1, currentTable.getNumColum()) + 1;
        Integer f = (index - 1) / currentTable.getNumColum() + 1; //calcular fila a partir del index.
        Cell cell = currentTable.getRows().get(f-1).getCells().get(c-1);
        cell.setCard(cardToPlace);
        cell.setIsFull(true);
        cellService.updateCell(cell, cell.getId());
        //Calculamos nuevas posiciones posibles del jugador
        List<Map<String, Integer>> newPossiblePositions = tableCardService.getPossiblePositionsForPlayer(currentTable, currentPlayer, 
            cardToPlace, null, false);
        List<Integer> positions = new ArrayList<>();
        List<Integer> rotations = new ArrayList<>();
        for (Map<String,Integer> mp:newPossiblePositions) {
            positions.add(mp.get("position"));
            rotations.add(mp.get("rotation"));
        }
        currentPlayer.setPossiblePositions(positions);
        currentPlayer.setPossibleRotations(rotations);
        currentPlayer.getPlayedCards().add(cardToPlace.getId());
        currentPlayer.setCardsPlayedThisTurn(currentPlayer.getCardsPlayedThisTurn() + 1);
        playerService.updatePlayer(currentPlayer);
    }

    @Transactional
    public void manageGame(Game game, User currentUser) throws ConflictException, UnfeasibleToJumpTeam {
        if (game.getGameState().equals(GameState.IN_PROCESS)) {
            GameMode gameMode = game.getGameMode();
            if (gameMode.equals(GameMode.PUZZLE_SINGLE)) {
                this.gameInProcessSingle(game, currentUser);
            } else if (gameMode.equals(GameMode.PUZZLE_COOP)) {
                this.gameInProcessCoop(game, currentUser);
            } else if (gameMode.equals(GameMode.TEAM_BATTLE)) {
                this.gameInProcessTeam(game, currentUser);
            } else {
                this.gameInProcess(game, currentUser);
            }
        }
    }

    @Transactional
    public ResponseEntity<MessageResponse> manageUseOfEnergy(PowerType powerType, Player player, Game game) throws InvalidIndexOfTableCard, UnfeasibleToPlaceCard, UnfeasibleToJumpTeam {
        switch (powerType) {
            case ACCELERATE:
                this.useAccelerate(player);
                return new ResponseEntity<>(new MessageResponse("You have used " + powerType.toString() + " successfully"), HttpStatus.ACCEPTED);
            case BRAKE:
                this.useBrake(player);
                return new ResponseEntity<>(new MessageResponse("You have used " + powerType.toString() + " successfully"), HttpStatus.ACCEPTED);
            case BACK_AWAY:
                return this.manageUseOfBackAway(player, game, powerType);
            case EXTRA_GAS:
                PackCard packCard = player.getPackCards().stream().findFirst().get();
                if (packCard.getNumCards() > 0) {
                    this.useExtraGas(player);
                    return new ResponseEntity<>(new MessageResponse("You have used " + powerType.toString() + " successfully"), HttpStatus.ACCEPTED);
                } else {
                    return new ResponseEntity<>(new MessageResponse("You can not take a card now, because your deck is empty"), HttpStatus.BAD_REQUEST);
                }
            case JUMP_TEAM:
                if (game.getGameMode().equals(GameMode.TEAM_BATTLE)) {
                    return this.manageUseOfJumpTeam(player, game, powerType);
                } else {
                    return new ResponseEntity<>(new MessageResponse("You can only use this power in Team Battle"), HttpStatus.BAD_REQUEST);
                }
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<MessageResponse> manageUseOfBackAway(Player player, Game game, PowerType powerType) throws InvalidIndexOfTableCard, UnfeasibleToPlaceCard, UnfeasibleToJumpTeam {
        if (player.getPlayedCards().size() >= 2) {//Comprobamos que haya al menos dos cartas jugadas para poder hacer marcha atrás
            Card cardToBackAway = cardService
                    .findCard(player.getPlayedCards().get(player.getPlayedCards().size() - 2));
            List<Map<String, Integer>> newPossiblePositions = tableCardService
                    .getPossiblePositionsForPlayer(game.getTable(), player, cardToBackAway, null, false);
            if (newPossiblePositions.isEmpty()) {//Si la carta anterior no tiene posiciones posibles donde colocar cartas, no puedes usar marcha atrás
                return new ResponseEntity<>(new MessageResponse("You can not use back away right now"),
                        HttpStatus.BAD_REQUEST);
            } else {
                this.useBackAway(player, newPossiblePositions);
                return new ResponseEntity<>(new MessageResponse("You have used " + powerType.toString() + " successfully"),
                    HttpStatus.ACCEPTED);
            }
        } else {
            return new ResponseEntity<>(new MessageResponse("You can not use back away right now"),
                        HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<MessageResponse> manageUseOfJumpTeam(Player player, Game game, PowerType powerType) throws InvalidIndexOfTableCard, UnfeasibleToPlaceCard, UnfeasibleToJumpTeam {
        Card lastPlacedCard = cardService.getLastPlaced(player);
        Team team = game.getTeams().stream().filter(t -> t.getPlayer1().equals(player) || t.getPlayer2().equals(player))
                .findFirst().get();
        Player playerTeam = null;
        if (team.getPlayer1().equals(player)) {
            playerTeam = team.getPlayer2();
        } else {
            playerTeam = team.getPlayer1();
        }
        List<Map<String, Integer>> newPossiblePositions = tableCardService
            .getPossiblePositionsForPlayer(game.getTable(), player, lastPlacedCard, playerTeam, true);
        if (newPossiblePositions.isEmpty()) {
            return new ResponseEntity<>(new MessageResponse("You can not jump your team"),
                        HttpStatus.BAD_REQUEST);
        } else {
            this.useJumpTeam(player, newPossiblePositions);
            return new ResponseEntity<>(new MessageResponse("You have used " + powerType.toString() + " successfully"), HttpStatus.ACCEPTED);
        }
    }

    private void updateStreaks(Game game) {
        List<Player> players = game.getPlayers();
        for (Player player: players) {
            User user = player.getUser();
            if (user.getWinningStreak() == null) {
                user.setWinningStreak(0);
            }
            if (user.getMaxStreak() == null) {
                user.setMaxStreak(0);
            }
            if (player.getState().equals(PlayerState.LOST)) {
                user.setWinningStreak(0);
            } else if (player.getState().equals(PlayerState.WON)) {
                Integer userStreak = user.getWinningStreak() + 1;
                user.setWinningStreak(userStreak);
                if (userStreak > user.getMaxStreak()) {
                    user.setMaxStreak(userStreak);
                }
            }
        }
    }

    @Transactional
    public Game checkTeamBattle(Game game, User user) {
        if (game.getGameMode().equals(GameMode.TEAM_BATTLE)) {
            if (game.getPlayers().size() < 4) {//Cambiar el modo de juego si no hay suficientes jugadores
                game.setGameMode(GameMode.VERSUS);
                int t = game.getTeams().size()-1;
                while (t >= 0) {
                    Team team = game.getTeams().remove(t);
                    teamService.deleteTeam(team);
                    t--;
                }
            } else if (game.getPlayers().size() % 2 == 1) {//Cambiar ultimo jugador que se ha unido a espectador si el número de jugadores es impar
                Player player = game.getPlayers().get(game.getPlayers().size() - 1);
                game.getPlayers().remove(player);
                player.setState(PlayerState.SPECTATING);
                playerService.updatePlayer(player);
                game.getSpectators().add(player);
                Team team = game.getTeams().remove(game.getTeams().size()-1);
                teamService.deleteTeam(team);
            }
        }
        return game;
    }
}
