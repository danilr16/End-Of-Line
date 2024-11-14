package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.time.LocalDateTime;
import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCard;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player.PlayerState;

public class PlayerDTO {

    private Integer score; //solo para los modos solitarios
    private Integer energy;
    private PlayerState playerState;
    private UserDTO user;
    private List<Integer> playedCards;
    private LocalDateTime turnStarted;
    private Boolean handChanged;
    private Integer cardsPlayedThisTurn;
    private  Hand hand;
    private List<PackCard> packCards;

    public PlayerDTO(Integer score,Integer energy,PlayerState playerState,UserDTO user,
    List<Integer> playedCards,LocalDateTime turnStarted,Boolean handChanged,
    Integer cardsPlayedThisTurn,Hand hand,List<PackCard> packCards){

    this.score = score;
    this.energy = energy;
    this.playerState = playerState;
    this.user = user;
    this. playedCards = playedCards;
    this.turnStarted = turnStarted;
    this.handChanged = handChanged;
    this.cardsPlayedThisTurn = cardsPlayedThisTurn;
    this.hand = hand;
    this.packCards = packCards;
    }


}
