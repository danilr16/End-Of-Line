package es.us.dp1.lx_xy_24_25.your_game_name.game;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class GameValidator implements Validator {

    @Override
    public void validate(Object obj, Errors errors) {
        Game game = (Game) obj;
        GameMode gameMode = game.getGameMode();
        Integer numPlayer = game.getNumPlayers();
        if (game.getIsPublic() == null) {
            errors.rejectValue("isPublic", "isPublicNotNull", "isPublic must be true or false");
        }
        if (game.getNumPlayers() == null || game.getNumPlayers() < 1 || game.getNumPlayers() > 8) {
            errors.rejectValue("numPlayers", "rangeOfNumPlayers", "numPlayers must be in 1 to 8");
        }
        if (gameMode == null) {
            errors.rejectValue("gameMode", "gameModeNotNull", "gameMode can't be null");
        }
        if (gameMode != null) {
            if (numPlayer >= 2 && gameMode.equals(GameMode.PUZZLE_SINGLE)) {
                errors.rejectValue("gameMode", "players_gameMode", 
                    "The gameMode can't be " + gameMode + " with two or more players");
            }
        }
        if (numPlayer == 1 && !gameMode.equals(GameMode.PUZZLE_SINGLE)) {
            errors.rejectValue("gameMode", "players_gameMode", 
                "The gameMode can't be " + gameMode + " with one player");
        }
        if (numPlayer != 2 && gameMode.equals(GameMode.PUZZLE_COOP)) {
            errors.rejectValue("gameMode", "players_gameMode", 
                "The gameMode can't be " + gameMode + " with " + numPlayer + " players");
        }
        if (numPlayer <= 3 && gameMode.equals(GameMode.TEAM_BATTLE)) {
            errors.rejectValue("gameMode", "players_gameMode", 
                "The gameMode can't be " + gameMode + " with " + numPlayer + " players");
        }
    }
    
    @Override
    public boolean supports(Class<?> clazz) {
        return Game.class.isAssignableFrom(clazz);
    }
}
