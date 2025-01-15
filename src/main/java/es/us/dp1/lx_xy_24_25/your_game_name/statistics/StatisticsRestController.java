package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.DurationGames;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.MyGamesStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.NumGames;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.NumPlayers;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsClasses.Ranking;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/statistics")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Statistics", description = "The Game Statistics API based on JWT")
public class StatisticsRestController {

    private UserService userService;
    private StatisticsService statisticsService;

    @Autowired
    public StatisticsRestController(UserService userService, StatisticsService statisticsService) {
        this.userService = userService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/games")
    public ResponseEntity<NumGames> findGamesStatistics(){
        User currentUser = userService.findCurrentUser();
        NumGames res = statisticsService.findNumGames(currentUser);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping("/time")
    public ResponseEntity<DurationGames> findTimeStatistics(){
        User user = userService.findCurrentUser();
        DurationGames res = statisticsService.findTime(user);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping("/players")
    public ResponseEntity<NumPlayers> findPlayersStatistics(){
        User user = userService.findCurrentUser();
        NumPlayers res = statisticsService.findNumPlayers(user);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping("/ranking")
    public ResponseEntity<Ranking> findRankingStatistics(){
        Ranking res = statisticsService.findRanking();
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping("/myGames")
    public ResponseEntity<MyGamesStatistics> findMyGamesStatistics(){
        User user = userService.findCurrentUser();
        MyGamesStatistics res = statisticsService.findMyGamesStatistics(user);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

}
