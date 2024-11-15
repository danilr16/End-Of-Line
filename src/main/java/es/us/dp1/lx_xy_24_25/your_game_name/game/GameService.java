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
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import jakarta.validation.Valid;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.TypeCard;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCard;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PlayerService;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCardService;
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

    @Autowired
    public GameService(GameRepository gameRepository, PackCardService packCardService, HandService handService
        ,PlayerService playerService, CardService cardService, TableCardService tableCardService){
        this.gameRepository = gameRepository;
        this.packCardService = packCardService;
        this.handService = handService;
        this.playerService = playerService;
        this.cardService = cardService;
        this.tableCardService = tableCardService;
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
       return gameRepository.findGameByGameCode(gameCode).orElseThrow(() -> new ResourceNotFoundException("Game","gameCode",gameCode));
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


    @Transactional(readOnly = true)
    public List<ChatMessage> getGameChat(String gameCode){
        return gameRepository.findGameChat(gameCode).orElseThrow(() -> new ResourceNotFoundException("Game","gameCode",gameCode));
    }

    @Transactional
    public Card takeACard(Player player) {
        PackCard packCard = player.getPackCards().stream().findFirst().get();
        if (packCard.getNumCards() != 0) {
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
            return card;
        } else {
            return null;
        }
    }

    @Transactional
    public void initialTurn(Game game) {//Decide el turno inicial de partida
        List<Integer> players = game.getPlayers().stream().filter(p -> !p.getState().equals(PlayerState.LOST))
            .map(p -> p.getId()).collect(Collectors.toList());
        Collections.shuffle(players);
        game.setTurn(players.stream().findFirst().get());
        game.setOrderTurn(players);
        game.setInitialTurn(players);
        this.updateGame(game, game.getId());
    }

    @Transactional
    public void decideTurns(Game game, List<Player> ls) {//Decido los turnos a partir de la ronda 1
        List<Integer> orderTurn = decideTurns(game, ls, new ArrayList<>(), 0);
        Integer turn = orderTurn.stream().findFirst().get();
        Player start = playerService.findPlayer(turn);
        start.setTurnStarted(LocalDateTime.now());
        playerService.updatePlayer(start, start.getId());
        game.setTurn(turn);
        game.setOrderTurn(orderTurn);
        this.updateGame(game, game.getId());
    }

    private List<Integer> decideTurns(Game game, List<Player> ls, List<Integer> orderTurn, Integer indexCard) {
        List<Player> players = new ArrayList<>(ls);
        List<Card> cards = new ArrayList<>();
        for (Player player: players) {
            Card card = cardService.findCard(player.getPlayedCards().get(player.getPlayedCards().size() - 1 - indexCard));
            if (card.getType().equals(TypeCard.INICIO)) {
                return game.getInitialTurn();
            }
            cards.add(card);
        }
        cards.sort(Comparator.comparing(Card::getIniciative));
        Integer minIniciative = cards.stream().findFirst().get().getIniciative();
        Integer aux = cards.size();
        for (int i = cards.size()-1;i >= 0; i--) {
            Card card = cards.get(i);
            if (card.getIniciative() != minIniciative) {
                orderTurn.add(0, card.getPlayer().getId());
                aux--;
            }
        }
        if (aux == 1) {
            Card card = cards.stream().findFirst().get();
            orderTurn.add(0, card.getPlayer().getId());
            return orderTurn;
        } else {
            List<Player> remaining = players.stream().filter(p -> !orderTurn.contains(p.getId()))
                .collect(Collectors.toList());
            return decideTurns(game, remaining, orderTurn, indexCard + 1);
        }
    }

    @Transactional
    public void returnCards(Player player) {//Devuelve todas las cartas de la mano de un jugador al mazo
        Hand hand = player.getHand();
        Card card = hand.getCards().stream().findFirst().get();
        PackCard packCard = player.getPackCards().stream().findFirst().get();
        packCard.getCards().add(card);
        packCard.setNumCards(packCard.getNumCards() + 1);
        hand.getCards().remove(card);
        hand.setNumCards(hand.getNumCards() - 1);
        packCardService.updatePackCard(packCard, packCard.getId());
        handService.updateHand(hand, hand.getId());
        if (!hand.getCards().isEmpty()) {
            returnCards(player);
        }
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
    public void manageTurnOfPlayer(Game game) { //Gestiona las acciones del jugador al que le toca
        Player playing = playerService.findPlayer(game.getTurn());
        if (Duration.between(playing.getTurnStarted(), LocalDateTime.now()).toMinutes() >= 5 || cantContinuePlaying(game, playing)) {
            playing.setState(PlayerState.LOST);
            playerService.updatePlayer(playing, playing.getId());
            nextTurn(game, playing);
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
        } else if (possiblePositions.isEmpty()) {
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
                Integer n = player.getHand().getNumCards();
                if (n < 5) {
                    for (int j = 1; j <= (5-n); j++) {
                        this.takeACard(player);
                    }
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
    public void gameInProcess(Game game) {//Lógica de gestión de una partida VERSUS en progreso
        List<Player> players = game.getPlayers().stream().filter(p -> !p.getState().equals(PlayerState.LOST))
            .collect(Collectors.toList());
        if (game.getNTurn() == 0) {
            initialTurn(game);
            for (Player player : players) {
                for (int i = 1; i <= 5; i++) {
                    this.takeACard(player);
                }
            }
            Player start = playerService.findPlayer(game.getTurn());
            start.setTurnStarted(LocalDateTime.now());
            playerService.updatePlayer(start, start.getId());
            game.setNTurn(1);
            this.updateGame(game, game.getId());
        } else {
            if (players.size() == 1) {
                Player winner = players.stream().findFirst().get();
                winner.setState(PlayerState.WON);
                playerService.updatePlayer(winner, winner.getId());
                game.setGameState(GameState.END);
                game.setDuration(Duration.between(game.getStarted(), LocalDateTime.now()).toMinutesPart());
                this.updateGame(game, game.getId());
            } else {
                manageTurnOfPlayer(game);
            }
        }
    }

    @Transactional
    public void gameInProcessSingle(Game game) {

    }

    @Transactional
    public void gameInProcessCoop(Game game) {
        
    }

    @Transactional
    public void gameInProcessTeam(Game game) {
        
    }

    @Transactional
    public void useAccelerate(Player player) {
        player.setEnergy(player.getEnergy()-1);
        player.setEnergyUsedThisRound(true);
        player.setCardsPlayedThisTurn(player.getCardsPlayedThisTurn() - 1);
        playerService.updatePlayer(player, player.getId());

    }

    @Transactional
    public void useBrake(Player player) {
        player.setEnergy(player.getEnergy()-1);
        player.setEnergyUsedThisRound(true);
        player.setCardsPlayedThisTurn(player.getCardsPlayedThisTurn() + 1);
        playerService.updatePlayer(player, player.getId());
    }

    @Transactional
    public void useBackAway(Player player) {
        
    }

    @Transactional
    public void useExtraGas(Player player) {
        player.setEnergy(player.getEnergy()-1);
        player.setEnergyUsedThisRound(true);
        this.takeACard(player);
        playerService.updatePlayer(player, player.getId());

    }
}
