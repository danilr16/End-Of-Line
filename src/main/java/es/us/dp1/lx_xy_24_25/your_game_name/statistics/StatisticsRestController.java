package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.game.Game;
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameService;
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsRecords.DurationGames;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsRecords.MyGamesStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsRecords.NumGames;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsRecords.NumPlayers;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsRecords.Ranking;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/statistics")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Statistics", description = "The Game Statistics API based on JWT")
public class StatisticsRestController {

    private GameService gameService;
    private UserService userService;

    @Autowired
    public StatisticsRestController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping("/games")
    public ResponseEntity<NumGames> findGamesStatistics(){
        List<Game> games = (List<Game>) gameService.findAll();
        User user = userService.findCurrentUser();
        List<Game> myGames = userService.findAllGamesByUserHost(user);
        List<Game> filteredGames = games.stream().filter(g -> !g.getGameState().equals(GameState.WAITING)).collect(Collectors.toList());
        List<Game> filteredMyGames = myGames.stream().filter(g -> !g.getGameState().equals(GameState.WAITING)).collect(Collectors.toList());
        NumGames res = BuildStatistics.buildNumGames(filteredGames, filteredMyGames);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping("/time")
    public ResponseEntity<DurationGames> findTimeStatistics(){
        List<Game> games = (List<Game>) gameService.findAll();
        User user = userService.findCurrentUser();
        List<Game> myGames = userService.findAllGamesByUserHost(user);
        List<Game> filteredGames = games.stream().filter(g -> !g.getGameState().equals(GameState.WAITING)).collect(Collectors.toList());
        List<Game> filteredMyGames = myGames.stream().filter(g -> !g.getGameState().equals(GameState.WAITING)).collect(Collectors.toList());
        DurationGames res = BuildStatistics.buildDurationGames(filteredGames, filteredMyGames);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping("/players")
    public ResponseEntity<NumPlayers> findPlayersStatistics(){
        List<Game> games = (List<Game>) gameService.findAll();
        User user = userService.findCurrentUser();
        List<Game> myGames = userService.findAllGamesByUserHost(user);
        List<Game> filteredGames = games.stream().filter(g -> !g.getGameState().equals(GameState.WAITING)).collect(Collectors.toList());
        List<Game> filteredMyGames = myGames.stream().filter(g -> !g.getGameState().equals(GameState.WAITING)).collect(Collectors.toList());
        NumPlayers res = BuildStatistics.buildNumPlayers(filteredGames, filteredMyGames);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping("/ranking")
    public ResponseEntity<Ranking> findRankingStatistics(){
        List<Game> games = (List<Game>) gameService.findAll();
        List<Game> filteredGames = games.stream().filter(g -> !g.getGameState().equals(GameState.WAITING)).collect(Collectors.toList());
        Ranking res = BuildStatistics.buildRankings(filteredGames);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping("/myGames")
    public ResponseEntity<MyGamesStatistics> findMyGamesStatistics(){
        User user = userService.findCurrentUser();
        List<Game> myGames = userService.findAllGamesByUserHost(user);
        List<Game> filteredMyGames = myGames.stream().filter(g -> !g.getGameState().equals(GameState.WAITING) 
            && !g.getGameState().equals(GameState.IN_PROCESS)).collect(Collectors.toList());
        MyGamesStatistics res = BuildStatistics.buildMyGamesStatistics(filteredMyGames, user);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }
    
}
