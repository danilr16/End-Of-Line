package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCard;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player.PlayerState;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class PlayerDTO {

    private Integer score; //solo para los modos solitarios
    private Integer energy;
    private PlayerState playerState;

    
    private UserDTO user;

    private List<Integer> playedCards;
    private LocalDateTime turnStarted;
    private Boolean handChanged;
    private Integer cardsPlayedThisTurn;
    private  HandDTO hand;
    private List<PackCardDTO> packCards;

    public PlayerDTO(){
        
    }

    public PlayerDTO(Integer score,Integer energy,PlayerState playerState,UserDTO user,
    List<Integer> playedCards,LocalDateTime turnStarted,Boolean handChanged,
    Integer cardsPlayedThisTurn,HandDTO hand,List<PackCardDTO> packCards){

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

    public static PlayerDTO PlayertoDTO(Player p){
        UserDTO userDTO = UserDTO.convertUserToDTO(p.getUser());
        HandDTO handDTO = HandDTO.handToDTO(p.getHand());
        List<PackCardDTO> pcsDTO = new ArrayList<>();
        if(!p.getPackCards().isEmpty()){
            for(PackCard pc: p.getPackCards()){
                PackCardDTO packCardDTO = PackCardDTO.packCardToDTO(pc);
                pcsDTO.add(packCardDTO);
            }
        }
        PlayerDTO playerDTO = new PlayerDTO(p.getScore(), p.getEnergy(), p.getState(), userDTO, p.getPlayedCards(), p.getTurnStarted(), p.getHandChanged(), p.getCardsPlayedThisTurn(),handDTO,pcsDTO);
        return playerDTO;
    }


}
