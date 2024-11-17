package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.Output;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card.TypeCard;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "player"
)
@Getter
@Setter
public class CardDTO {

    private TypeCard type;
    private Integer iniciative;
    private Integer rotation;
    private PlayerDTO player;
    private Output output;
    private List<Integer> outputs;
    private Integer input;

    public CardDTO(){}

    public CardDTO(TypeCard type,Integer inciative,Integer rotation,PlayerDTO player,Output output,List<Integer> outputs,Integer input){
        this.type = type;
        this.iniciative = inciative;
        this.rotation = rotation;
        this.player = player;
        this.output = output;
        this.outputs = outputs;
        this.input = input;
    }


    public static CardDTO cardToDTO(Card c){
        if(c==null)return null;
        PlayerDTO pDTO = PlayerDTO.PlayertoDTO(c.getPlayer());
        CardDTO  res = new CardDTO(c.getType(), c.getIniciative(), c.getRotation(), pDTO, c.getOutput(), c.getOutputs(),c.getInput());
        return res;
    }

}
