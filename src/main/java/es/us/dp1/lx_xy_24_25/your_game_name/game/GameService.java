package es.us.dp1.lx_xy_24_25.your_game_name.game;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;


@Service
public class GameService {

    private GameRepository gameRepository;

    @Autowired
    public GameService(GameRepository gameRepository){
        this.gameRepository = gameRepository;
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
    public Game findCreatedGameByHost(User host){
        List<Game> actualGames = findJoinableGames();
        Game hostGame = actualGames.stream().filter(g -> g.host.equals(host)).findFirst().orElse(null);
        return hostGame;
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


}
