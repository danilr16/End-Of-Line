package es.us.dp1.lx_xy_24_25.your_game_name.dto;

import java.util.ArrayList;
import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.Cell;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.Row;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RowDTO {

    private List<CellDTO> cells;
    
    public RowDTO(){}

    public RowDTO(List<CellDTO> cells){
        this.cells = cells;
    }

    public static RowDTO rowToDTO(Row r){
        if(r==null){
            return null;
        }
        List<CellDTO> dtoCells = new ArrayList<>();
        for(Cell c: r.getCells()){
            CellDTO dtoCell = CellDTO.cellToDTO(c);
            dtoCells.add(dtoCell);
        }
        RowDTO res = new RowDTO(dtoCells);
        return res;
    }

}
