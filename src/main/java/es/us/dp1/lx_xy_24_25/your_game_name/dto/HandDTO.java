package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.util.ArrayList;
import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HandDTO {

    private Integer numCards;
    private List<CardDTO> cards;

    public HandDTO(){}

    public HandDTO(Integer numCards,List<CardDTO> cards){
        this.numCards = numCards;
        this.cards = cards;
    }

    public static HandDTO handToDTO(Hand h){
        if(h==null)return null;
        List<CardDTO> cards = cardsToDTO(h.getCards());
        HandDTO res = new HandDTO(h.getNumCards(), cards);
        return res;
    }

    public static List<CardDTO> cardsToDTO(List<Card> cs){
        List<CardDTO> cards = new ArrayList<>();
        for(Card c: cs){
            CardDTO cDTO = CardDTO.cardToDTO(c);
            cards.add(cDTO);
        }
        return cards;
    }
}
