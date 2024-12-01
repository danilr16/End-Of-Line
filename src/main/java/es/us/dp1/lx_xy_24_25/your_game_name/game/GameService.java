package es.us.dp1.lx_xy_24_25.your_game_name.game;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.springframework.beans.BeanUtils;
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
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.InvalidIndexOfTableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
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

    @Autowired
    public GameService(GameRepository gameRepository, PackCardService packCardService, HandService handService
        , PlayerService playerService, CardService cardService, TableCardService tableCardService,
        CellService cellService){
        this.gameRepository = gameRepository;
        this.packCardService = packCardService;
        this.handService = handService;
        this.playerService = playerService;
        this.cardService = cardService;
        this.tableCardService = tableCardService;
        this.cellService = cellService;
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
    public Game updateGame(@Valid Game game, Integer idToUpdate) {
        Game toUpdate = findGame(idToUpdate);
		BeanUtils.copyProperties(game, toUpdate, "id");
		gameRepository.save(toUpdate);
		return toUpdate;
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
    public List<ChatMessage> sendChatMessage(ChatMessage cm){
        Game game = gameRepository.findGameByGameCode(cm.getGameCode()).orElseThrow(() -> new ResourceNotFoundException("Game","gameCode",cm.getGameCode()));
        game.getChat().add(cm);
        List<ChatMessage> newChat = game.getChat();
        this.updateGame(game, game.getId());
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
        this.updateGame(game, game.getId());
        return game; //Se ha añadido que devuelva game para la comprobación de los test
    }

    @Transactional
    public Game decideTurns(Game game, List<Player> ls) {//Decido los turnos a partir de la ronda 1
        List<Integer> orderTurn = decideTurns(game, ls, new ArrayList<>(), 0);
        Integer turn = orderTurn.stream().findFirst().get();
        Player start = playerService.findPlayer(turn);
        start.setTurnStarted(LocalDateTime.now());
        playerService.updatePlayer(start, start.getId());
        game.setTurn(turn);
        game.setOrderTurn(orderTurn);
        this.updateGame(game, game.getId());
        return game; //Se ha añadido que devuelva game para la comprobación de los test
    }

    private List<Integer> decideTurns(Game game, List<Player> ls, List<Integer> orderTurn, Integer indexCard) {
        List<Player> players = new ArrayList<>(ls);
        List<Card> cards = new ArrayList<>();
        for (Player player: players) {//Cogemos las ultimas cartas jugadas de cada jugador o las anteriores (llamada recursiva que va comparando cartas hasta el caso base, llegada a nodo de inicio)
            Card card = cardService.findCard(player.getPlayedCards().get(player.getPlayedCards().size() - 1 - indexCard));
            if (card.getType().equals(TypeCard.INICIO)) {
                return game.getInitialTurn();
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
        hand.getCards().removeAll(cards);
        hand.setNumCards(hand.getNumCards() - cards.size());
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
        playerService.updatePlayer(player, player.getId());
    }

    @Transactional
    public void manageTurnOfPlayer(Game game, Player playing) { //Gestiona las acciones del jugador al que le toca
        if (Duration.between(playing.getTurnStarted(), LocalDateTime.now()).toMinutes() >= 5 || cantContinuePlaying(game, playing)) {
            playing.setState(PlayerState.LOST);
            playerService.updatePlayer(playing, playing.getId());
            if (!game.getGameMode().equals(GameMode.PUZZLE_SINGLE)) {
                nextTurn(game, playing);
            }
        } else if (playing.getCardsPlayedThisTurn() >= 2) {
            nextTurn(game, playing);
        }
    }

    public Boolean cantContinuePlaying(Game game, Player playing) {//si el jugador no tiene acciones posibles actuales y no puede usar poder
        TableCard tableCard = game.getTable();
        Card lastPlaced = cardService.getLastPlaced(playing);
        List<Map<String,Integer>> possiblePositions = tableCardService.getPossiblePositionsForPlayer(tableCard, playing, lastPlaced);
        if (possiblePositions.isEmpty() && playing.getEnergy() == 0) {
            return true;
        } else if (possiblePositions.isEmpty() && playing.getPlayedCards().size() >= 2) {
            Card possibleGoBack = cardService.findCard(playing.getPlayedCards().get(playing.getPlayedCards().size()-2));
            if (tableCardService.getPossiblePositionsForPlayer(tableCard, playing, possibleGoBack).isEmpty()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Transactional
    public void nextTurn(Game game, Player playing) {//Lógica para turno del siguiente jugador o empiza siguiente ronda
        Integer i = game.getOrderTurn().indexOf(game.getTurn());
        playing.setCardsPlayedThisTurn(0);
        playing.setTurnStarted(null);
        playing.setEnergyUsedThisRound(false);
        playerService.updatePlayer(playing, playing.getId());
        if (i == game.getOrderTurn().size()-1) {
            game.setNTurn(game.getNTurn() + 1);
            List<Player> players = game.getPlayers().stream().filter(p -> !p.getState().equals(PlayerState.LOST))
                .collect(Collectors.toList());
            decideTurns(game, players);
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
            playerService.updatePlayer(nextPlaying, nextPlaying.getId());
            game.setTurn(nextPlaying.getId());
            this.updateGame(game, game.getId());
        }
    }

    @Transactional
    private void turn0(Game game, List<Player> players, User currentUser) {
        initialTurn(game);
            for (Player player : players) {
                User user = player.getUser();
                if (user.equals(currentUser)) {
                    Hand hand = player.getHand();
                    while (hand.getNumCards() < 5) {
                        this.takeACard(player);
                    }
                    break;
                }
            }
            Boolean todosConCartas = true;
            for (Player player: players) {
                if (player.getHand().getNumCards() != 5) {
                    todosConCartas = false;
                    break;
                }
            }
            if (todosConCartas) {
                Player start = playerService.findPlayer(game.getTurn());
                start.setTurnStarted(LocalDateTime.now());
                playerService.updatePlayer(start, start.getId());
                game.setNTurn(1);
            }
            this.updateGame(game, game.getId());
    }

    @Transactional
    public void gameInProcess(Game game, User currentUser) {//Lógica de gestión de una partida VERSUS en progreso
        List<Player> players = game.getPlayers().stream().filter(p -> !p.getState().equals(PlayerState.LOST))
            .collect(Collectors.toList());
        if (game.getNTurn() == 0) {
            this.turn0(game, players, currentUser);
        } else {
            if (players.size() == 1) {
                Player winner = players.stream().findFirst().get();
                winner.setState(PlayerState.WON);
                playerService.updatePlayer(winner, winner.getId());
                game.setGameState(GameState.END);
                game.setDuration(Duration.between(game.getStarted(), LocalDateTime.now()).toMinutesPart());
                this.updateGame(game, game.getId());
            } else {
                Player playing = playerService.findPlayer(game.getTurn());
                if (playing.getUser().equals(currentUser)) {
                    manageTurnOfPlayer(game, playing);
                }
                if (playing.getState().equals(PlayerState.LOST)) {
                    nextTurn(game, playing);
                }
            }
        }
    }

    @Transactional
    public void gameInProcessSingle(Game game, User currentUser) {//Revisar se puede jugar
        List<Player> players = game.getPlayers().stream().filter(p -> !p.getState().equals(PlayerState.LOST))
            .collect(Collectors.toList());
        if (game.getNTurn() == 0) {
            this.turn0(game, players, currentUser);
        } else {
            if (players.isEmpty()) {
                game.setGameState(GameState.END);
                game.setDuration(Duration.between(game.getStarted(), LocalDateTime.now()).toMinutesPart());
                this.updateGame(game, game.getId());
            } else if (tableCardService.tableCardFull(game.getTable())) {
                Player winner = players.stream().findFirst().get();
                winner.setState(PlayerState.WON);
                Integer sumInicHand = winner.getHand().getCards().stream().mapToInt(c -> c.getIniciative()).sum();
                winner.setScore(sumInicHand + winner.getEnergy());
                playerService.updatePlayer(winner, winner.getId());
                game.setGameState(GameState.END);
                game.setDuration(Duration.between(game.getStarted(), LocalDateTime.now()).toMinutesPart());
                this.updateGame(game, game.getId());
            } else {
                Player playing = playerService.findPlayer(game.getTurn());
                if (playing.getUser().equals(currentUser)) {
                    manageTurnOfPlayer(game, playing);
                }
            }
        }
    }

    @Transactional
    public void gameInProcessCoop(Game game, User currentUser) {//Revisar se puede jugar
        List<Player> players = game.getPlayers().stream().filter(p -> !p.getState().equals(PlayerState.LOST))
            .collect(Collectors.toList());
        if (game.getNTurn() == 0) {
            this.turn0(game, players, currentUser);
        } else {
            if (players.size() < 2) {
                for (Player loser:players) {
                    loser.setState(PlayerState.LOST);
                    playerService.updatePlayer(loser, loser.getId());
                }
                game.setGameState(GameState.END);
                game.setDuration(Duration.between(game.getStarted(), LocalDateTime.now()).toMinutesPart());
                this.updateGame(game, game.getId());
            } else if (tableCardService.tableCardFull(game.getTable())) {
                for (Player winner:players) {
                    winner.setState(PlayerState.WON);
                    Integer sumInicHand = winner.getHand().getCards().stream().mapToInt(c -> c.getIniciative()).sum();
                    winner.setScore(sumInicHand + winner.getEnergy());
                    playerService.updatePlayer(winner, winner.getId());
                }
                game.setGameState(GameState.END);
                game.setDuration(Duration.between(game.getStarted(), LocalDateTime.now()).toMinutesPart());
                this.updateGame(game, game.getId());
            } else {
                Player playing = playerService.findPlayer(game.getTurn());
                if (playing.getUser().equals(currentUser)) {
                    manageTurnOfPlayer(game, playing);
                }
            }
        }
    }

    @Transactional
    public void gameInProcessTeam(Game game, User currentUser) {
        
    }

    @Transactional
    public void useAccelerate(Player player) {
        player.setEnergy(player.getEnergy()-1);
        player.setEnergyUsedThisRound(true);
        player.setCardsPlayedThisTurn(player.getCardsPlayedThisTurn() - 1);
        player.getUsedPowers().add(PowerType.ACCELERATE);
        playerService.updatePlayer(player, player.getId());
    }

    @Transactional
    public void useBrake(Player player) {
        player.setEnergy(player.getEnergy()-1);
        player.setEnergyUsedThisRound(true);
        player.setCardsPlayedThisTurn(player.getCardsPlayedThisTurn() + 1);
        player.getUsedPowers().add(PowerType.BRAKE);
        playerService.updatePlayer(player, player.getId());
    }

    @Transactional
    public void useBackAway(Player player, List<Map<String, Integer>> newPossiblePositions) throws InvalidIndexOfTableCard, UnfeasibleToPlaceCard {
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
        playerService.updatePlayer(player, player.getId());
    }

    @Transactional
    public void useExtraGas(Player player) {
        player.setEnergy(player.getEnergy()-1);
        player.setEnergyUsedThisRound(true);
        this.takeACard(player);
        player.getUsedPowers().add(PowerType.EXTRA_GAS);
        playerService.updatePlayer(player, player.getId());

    }

    @Transactional
    public void placeCard(User currentUser, String gameCode, Integer index, Card cardToPlace) 
        throws InvalidIndexOfTableCard, UnfeasibleToPlaceCard {
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
        if (!turnOfPlayer.equals(currentPlayer) || !(currentPlayer.getCardsPlayedThisTurn() < 2)) {
            throw new AccessDeniedException("You can't place this card, because it's not your turn");
        }
    }

    private void updatePlaceCard(List<Map<String, Integer>> possiblePositions, Card cardToPlace, 
        Player currentPlayer, TableCard currentTable, Integer index, Integer i) {
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
            cardToPlace);
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
        playerService.updatePlayer(currentPlayer, currentPlayer.getId());
    }

    @Transactional
    public void manageGame(Game game, User currentUser) {
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
    public ResponseEntity<MessageResponse> manageUseOfEnergy(PowerType powerType, Player player, Game game) throws InvalidIndexOfTableCard, UnfeasibleToPlaceCard {
        switch (powerType) {
            case ACCELERATE:
                this.useAccelerate(player);
                return new ResponseEntity<>(new MessageResponse("You have used " + powerType.toString() + " successfully"), HttpStatus.ACCEPTED);
            case BRAKE:
                this.useBrake(player);
                return new ResponseEntity<>(new MessageResponse("You have used " + powerType.toString() + " successfully"), HttpStatus.ACCEPTED);
            case BACK_AWAY:
                if (player.getPlayedCards().size() >= 2) {
                    Card cardToBackAway = cardService
                            .findCard(player.getPlayedCards().get(player.getPlayedCards().size() - 2));
                    List<Map<String, Integer>> newPossiblePositions = tableCardService
                            .getPossiblePositionsForPlayer(game.getTable(), player, cardToBackAway);
                    if (newPossiblePositions.isEmpty()) {
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
            case EXTRA_GAS:
                PackCard packCard = player.getPackCards().stream().findFirst().get();
                if (packCard.getNumCards() > 0) {
                    this.useExtraGas(player);
                    return new ResponseEntity<>(new MessageResponse("You have used " + powerType.toString() + " successfully"), HttpStatus.ACCEPTED);
                } else {
                    return new ResponseEntity<>(new MessageResponse("You can not take a card now, because your deck is empty"), HttpStatus.BAD_REQUEST);
                }
            default:
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
