package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.util.ArrayList;
import java.util.List;


import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.Row;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard.TypeTable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableCardDTO {

    private TypeTable type;
    private  Integer numRow;
    private  Integer numColum;
    private  List<RowDTO> rows;

    public TableCardDTO(){}

    public TableCardDTO(TypeTable type,Integer numRow,Integer numColum,List<RowDTO> rows){
        this.type = type;
        this.numRow = numRow;
        this.numColum = numColum;
        this.rows = rows;
    }

    public static TableCardDTO tableCardToDTO(TableCard t){
        if(t==null)return null;
        
        List<RowDTO> dtoRows = new ArrayList<>();
        
        for(Row r: t.getRows()){
            RowDTO rDTO = RowDTO.rowToDTO(r);
            dtoRows.add(rDTO);
        }
        TableCardDTO res = new TableCardDTO(t.getType(), t.getNumRow(), t.getNumColum(), dtoRows);
        return res;
    }

}
