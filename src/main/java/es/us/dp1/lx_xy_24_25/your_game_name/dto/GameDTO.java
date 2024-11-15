package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.util.ArrayList;
import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.game.Game;
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameMode;
import es.us.dp1.lx_xy_24_25.your_game_name.game.GameState;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameDTO {

    private String gameCode;
    private Boolean isPublic;
    private Integer numPlayers;
    private Integer turn; 
    private Integer duration;
    private List<Integer> orderTurn;
    private List<Integer> initialTurn;
    private List<UserDTO> spectators;
    private List<PlayerDTO> players;
    private TableCard tableCard;
    private UserDTO host;
    private GameMode gameMode;
    private GameState gameState;

    public GameDTO(){
        
    }

    public GameDTO( String gameCode, Boolean isPublic, Integer numPlayers,Integer turn,
        Integer duration, List<Integer> orderTurn, List<Integer> initialTurn,List<UserDTO> spectators,
        List<PlayerDTO> players, TableCard tableCard, UserDTO host, GameMode gameMode, GameState gameState ){
        this.gameCode = gameCode;
        this.isPublic = isPublic;
        this.numPlayers = numPlayers;
        this.turn = turn;
        this.duration = duration;
        this.orderTurn = orderTurn;
        this.initialTurn = initialTurn;
        this.spectators = spectators;
        this.players = players; 
        this.tableCard = tableCard;
        this.host = host;
        this.gameMode = gameMode;
        this.gameState = gameState;
    }


    public static GameDTO convertGameToDTO(Game g){

        List<UserDTO> spectatorsConverted = new ArrayList<>();
        //Convertir espectadores
        if(!g.getSpectators().isEmpty()){
            for(Player s:g.getSpectators()){
                User spectatorUser = s.getUser();
                UserDTO userDTO = UserDTO.convertUserToDTO(spectatorUser);
                spectatorsConverted.add(userDTO);
            }
        }
        List<PlayerDTO> playersConverted = new ArrayList<>();
        //Convertir jugadores
        if(!g.getPlayers().isEmpty()){
            for(Player p:g.getPlayers()){
                User playerUser = p.getUser();
                UserDTO userDTO = UserDTO.convertUserToDTO(playerUser);
                PlayerDTO playerDTO = new PlayerDTO(p.getScore(), p.getEnergy(), p.getState(), userDTO, p.getPlayedCards(), p.getTurnStarted(), p.getHandChanged(), p.getCardsPlayedThisTurn(),p.getHand(), p.getPackCards());
                playersConverted.add(playerDTO);
            }
        }
        //Convertir usuario
        UserDTO hostConverted = UserDTO.convertUserToDTO(g.getHost());

        GameDTO res = new GameDTO(g.getGameCode(),g.getIsPublic(),g.getNumPlayers(),g.getTurn(),g.getDuration(),g.getOrderTurn(),
        g.getInitialTurn(),spectatorsConverted,playersConverted,g.getTable(),hostConverted,g.getGameMode(),g.getGameState());
        return res;

        }


}
