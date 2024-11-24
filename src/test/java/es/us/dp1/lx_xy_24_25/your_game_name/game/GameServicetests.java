package es.us.dp1.lx_xy_24_25.your_game_name.game;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;

@SpringBootTest
@AutoConfigureTestDatabase
public class GameServicetests {
    
    @Autowired
    private GameService gameService;

    //Test de consulta de Game

    @Test
    public void shouldFindAll(){
        List<Game> games = (List<Game>) gameService.findAll();
        //Esta comprobación depende del número de juegos creados inicialmente
        assertEquals(1, games.size());
    }


    @Test
    public void shouldFindGame(){
        Game gameToFind = gameService.findGame(1);
        assertNotNull(gameToFind);
        assertEquals(1, gameToFind.getId());
    }

    @Test
    public void shouldNotFindGame(){
        assertThrows(ResourceNotFoundException.class, () -> gameService.findGame(3423234));
    }

    @Test 
    public void shouldFindGameByGameCode(){
        Game gamebyGameCode = gameService.findGameByGameCode("ABCDE");
        assertNotNull(gamebyGameCode);
        assertEquals("ABCDE", gamebyGameCode.getGameCode());
    }

    @Test 
    public void shouldNotFindGameByGameCode(){
        assertThrows(ResourceNotFoundException.class, () -> gameService.findGameByGameCode("AAAAA"));
    }

    @Test
    public void shouldFindJoinableGames(){
        List<Game> games = gameService.findJoinableGames();
        assertFalse(games.isEmpty());
        assertEquals(1, games.size()); //Depende de las partidas de inicio en la base de datos.
    }







    @Test
    public void shouldSaveGame(){
        Game newGame = new Game();
        newGame.setDuration(20);;
        Game savedGame = gameService.saveGame(newGame);
        assertNotNull(newGame.getId());
        assertEquals(20, savedGame.getDuration());
    }

    @Test 
    public void shouldUpdateGame(){
        Game g = new Game();
        g.setDuration(10);
        Game gameToUpdated = gameService.saveGame(g);

        gameToUpdated.setDuration(20);
        Game gameUpdated = gameService.updateGame(gameToUpdated, gameToUpdated.getId());
        assertNotNull(gameUpdated);
        assertEquals(20,gameToUpdated.getDuration());      
    }

    @Test 
    public void shouldDeleteGame(){
        Game g = new Game();
        gameService.deleteGame(g.getId());
        assertThrows(ResourceNotFoundException.class, () -> gameService.findGame(g.getId()));
    }




}
