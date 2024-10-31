package es.us.dp1.lx_xy_24_25.your_game_name.game;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import jakarta.validation.Valid;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.HandService;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCard;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCardService;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;

@Service
public class GameService {

    private GameRepository gameRepository;
    private PackCardService packCardService;
    private HandService handService;

    @Autowired
    public GameService(GameRepository gameRepository, PackCardService packCardService, HandService handService){
        this.gameRepository = gameRepository;
        this.packCardService = packCardService;
        this.handService = handService;
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

    @Transactional
    public Card takeACard(Player player) {
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
        return card;
    }
}
