package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import es.us.dp1.lx_xy_24_25.your_game_name.game.Game;
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.PowerType;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player.PlayerState;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsRecords.BasicStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsRecords.DurationGames;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsRecords.MyGamesStatistics;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsRecords.NumGames;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsRecords.NumPlayers;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.StatisticsRecords.Ranking;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

public class BuildStatistics {

    public static NumGames buildNumGames(List<Game> games, List<Game> myGames) {//Estadísticas sobre el número de partidas (globales y por usuario)
        BasicStatistics global = buildStatisticsNumGames(games);
        BasicStatistics user = buildStatisticsNumGames(myGames);
        NumGames res = NumGames.of(global, user);
        return res;
    }

    private static BasicStatistics buildStatisticsNumGames(List<Game> games) {
        List<Integer> scores = games.stream().filter(g -> g.getGameMode().equals(GameMode.PUZZLE_SINGLE) //Juegos filtrados cuyo modo de juego tienen puntos asociados
            || g.getGameMode().equals(GameMode.PUZZLE_COOP)).map(g -> g.getPlayers())
            .flatMap(List::stream).map(p -> p.getScore()).collect(Collectors.toList());
        Double averageScore = scores.stream().mapToInt(i -> i).average().orElse(0);
        Integer minScore = scores.stream().mapToInt(i -> i).min().orElse(0);
        Integer maxScore = scores.stream().mapToInt(i -> i).max().orElse(0);
        BasicStatistics res = BasicStatistics.of(games.size(), averageScore, minScore, maxScore);
        return res;
    }

    public static NumPlayers buildNumPlayers(List<Game> games, List<Game> myGames) {//Estadísticas sobre el número de jugadores de las partidas (globales y por usuario)
        GameMode mostPlayed = games.stream().map(g -> g.getGameMode())//Modo de juego más jugado
            .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
            .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        GameMode mostPlayedByUser = myGames.stream().map(g -> g.getGameMode())//Modo de juego más jugado por un usuario
            .collect(Collectors.groupingBy(e -> e, Collectors.counting()))
            .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        BasicStatistics global = buildStatisticsNumPlayers(games);
        BasicStatistics user = buildStatisticsNumPlayers(myGames);
        NumPlayers res = NumPlayers.of(global, user, mostPlayed, mostPlayedByUser);
        return res;
    }

    private static BasicStatistics buildStatisticsNumPlayers(List<Game> games) {
        List<Integer> numPlayers = games.stream().map(g -> g.getPlayers().size()).collect(Collectors.toList());
        Integer totalPlayers = numPlayers.stream().mapToInt(i -> i).sum();
        Double averagePlayers = numPlayers.stream().mapToInt(i -> i).average().orElse(0);
        Integer minPlayers = numPlayers.stream().mapToInt(i -> i).min().orElse(0);
        Integer maxPlayers = numPlayers.stream().mapToInt(i -> i).max().orElse(0);
        BasicStatistics res = BasicStatistics.of(totalPlayers, averagePlayers, minPlayers, maxPlayers);
        return res;
    }

    public static DurationGames buildDurationGames(List<Game> games, List<Game> myGames) {//Estadísticas de duración de las partidas (globales y por usuario)
        BasicStatistics global = buildStatisticsDuration(games);
        BasicStatistics user = buildStatisticsDuration(myGames);
        DurationGames res = DurationGames.of(global, user);
        return res;
    }

    private static BasicStatistics buildStatisticsDuration(List<Game> games) {
        List<Integer> duration = games.stream().map(g -> g.getDuration()).collect(Collectors.toList());
        Integer totalDuration = duration.stream().mapToInt(i -> i).sum();
        Double averageDuration = duration.stream().mapToInt(i -> i).average().orElse(0);
        Integer minDuration = duration.stream().mapToInt(i -> i).min().orElse(0);
        Integer maxDuration = duration.stream().mapToInt(i -> i).max().orElse(0);
        BasicStatistics res = BasicStatistics.of(totalDuration, averageDuration, minDuration, maxDuration);
        return res;
    }

    public static MyGamesStatistics buildMyGamesStatistics(List<Game> myGames, User currentUser) {//Estadísticas de mis partidas (victorias, derrotas y poderes mas usados)
        Long totalVictories = myGames.stream().filter(g -> iWonInThisGame(g, currentUser)).count();
        Double averageVictories = 0.;
        if (!myGames.isEmpty()) {
            averageVictories = (totalVictories.doubleValue()/myGames.size())*100;
        }
        BasicStatistics victories = BasicStatistics.of(totalVictories.intValue(), averageVictories, null, null);
        BasicStatistics defeats = BasicStatistics.of(myGames.size() - totalVictories.intValue(), 100 - averageVictories, null, null);
        Map<PowerType, Integer> mostUsedPowers = mostUsedPowers(myGames, currentUser);
        MyGamesStatistics res = MyGamesStatistics.of(victories, defeats, mostUsedPowers);
        return res;
    }

    private static Player getMyPlayerInGame(Game game, User currentUser) {
        Player currentPlayer = game.getPlayers().stream().filter(p -> p.getUser().equals(currentUser)).findFirst().get();
        return currentPlayer;
    }

    private static Boolean iWonInThisGame(Game game, User currentUser) {
        Player currentPlayer = getMyPlayerInGame(game, currentUser);
        return currentPlayer.getState().equals(PlayerState.WON);
    }

    private static Map<PowerType, Integer> mostUsedPowers(List<Game> myGames, User currentUser) {//Devuelve los 2 poderes más usados por un usuario en todas sus partidas
        Map<PowerType, Integer> powers = myGames.stream().map(g -> getMyPlayerInGame(g, currentUser)).map(p -> p.getUsedPowers()).flatMap(List::stream)//Relacionamos todos los poderes jugados
            .collect(Collectors.groupingBy(p -> p, Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));                     //con el número de veces que lo he usado en mis partidas
        Map<PowerType, Integer> mostUsedPowers = powers.entrySet().stream()//Consigo los 2 poderes más usados
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(2).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return mostUsedPowers;
    }

    public static Ranking buildRankings(List<Game> games) {//Estadísticas de ranking de jugadores
        Map<String, Integer> victories = games.stream().map(g -> g.getPlayers())//Calculamos los usuarios relacionados con el número de victorias que llevan
            .flatMap(List::stream).filter(p -> p.getState().equals(PlayerState.WON))
            .collect(Collectors.groupingBy(p -> p.getUser().getUsername(), 
            Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));
        Map<String, Integer> top10Winners = victories.entrySet().stream()//Calculamos los 10 usuarios con más victorias
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(10).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<Game> filterGames = games.stream().filter(g -> g.getGameMode().equals(GameMode.PUZZLE_SINGLE) 
            || g.getGameMode().equals(GameMode.PUZZLE_COOP)).collect(Collectors.toList());
        Map<String, Integer> scores = filterGames.stream().map(g -> g.getPlayers())//Calculamos los usuarios relacionados con la suma de puntos total que llevan
            .flatMap(List::stream).collect(Collectors.groupingBy(p -> p.getUser().getUsername(),
                Collectors.summingInt(Player::getScore)));
        Map<String, Integer> top10Scores = scores.entrySet().stream()//Calculamos los 10 usuarios con más puntos
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(10).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Ranking res = Ranking.of(top10Winners, top10Scores);
        return res;
    }
}
