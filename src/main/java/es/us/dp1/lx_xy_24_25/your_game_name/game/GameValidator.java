package es.us.dp1.lx_xy_24_25.your_game_name.game;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class GameValidator implements Validator {

    @Override
    public void validate(Object obj, Errors errors) {
        Game game = (Game) obj;
        GameMode gameMode = game.getGameMode();
        Integer numPlayer = game.getNumPlayers();
        if (numPlayer >= 2 && gameMode.equals(GameMode.PUZZLE_SINGLE)) {
            errors.rejectValue("gameMode", "players_gameMode", 
                "The gameMode can't be " + gameMode + " with two or more players");
        }

        if (numPlayer == 1 && !gameMode.equals(GameMode.PUZZLE_SINGLE)) {
            errors.rejectValue("gameMode", "players_gameMode", 
                "The gameMode can't be " + gameMode + " with one player");
        }
    }
    
    @Override
    public boolean supports(Class<?> clazz) {
        return Game.class.isAssignableFrom(clazz);
    }
}
