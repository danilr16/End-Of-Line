package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.Cell;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CellDTO {

    private Boolean isFull;
    private CardDTO card;

    public CellDTO(){}

    public CellDTO(Boolean isFull,CardDTO card){
        this.isFull = isFull;
        this.card = card;
    }

    public static CellDTO cellToDTO(Cell c){
        if(c==null) return null;
        CardDTO card = CardDTO.cardToDTO(c.getCard());
        CellDTO res = new CellDTO(c.getIsFull(), card);
        return res;
    }

}
