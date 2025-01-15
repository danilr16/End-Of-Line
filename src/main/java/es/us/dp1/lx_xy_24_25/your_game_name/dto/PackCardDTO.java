package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackCardDTO {

    private Integer numCards;
    private List<CardDTO> cards;

    public PackCardDTO(){}

    public PackCardDTO(Integer numCard,List<CardDTO> cards){
        this.numCards = numCard;
        this.cards = cards;
    }

    public static PackCardDTO packCardToDTO(PackCard pc){
        if(pc==null)return null;
        List<CardDTO> csDTO = HandDTO.cardsToDTO(pc.getCards());
        PackCardDTO res = new PackCardDTO(pc.getNumCards(), csDTO);
        return res; 
    }

}
