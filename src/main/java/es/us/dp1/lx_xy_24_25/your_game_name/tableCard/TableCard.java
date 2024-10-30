package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name="appTableCards")
public class TableCard extends BaseEntity{

    public enum TypeTable {
        JUGADORES_1,JUGADORES_2,JUGADORES_3,JUGADORES_4,JUGADORES_5,JUGADORES_6,JUGADORES_7,JUGADORES_8
    }

    TypeTable type;

    Integer numRow;

    Integer numColum;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @NotNull
    @JoinColumn(name="table_id")
    List<Row> rows;

    public static record nodeCoordinates(Integer f, Integer c, Integer rotation) {
        public static nodeCoordinates of(Integer f, Integer c, Integer rotation) {
            return new nodeCoordinates(f, c, rotation);
        }
    }

    public static Map<Integer, List<nodeCoordinates>> homeNodes() {//Coordenadas de los nodos de inicio en función del número de jugadores en partida
        Map<Integer, List<nodeCoordinates>> mp = Map.of(1, List.of(nodeCoordinates.of(5, 3, 0)),
            2, List.of(nodeCoordinates.of(7, 3, 0), nodeCoordinates.of(7, 5, 0)),
            3, List.of(nodeCoordinates.of(7, 3, 3), nodeCoordinates.of(6, 4, 0), nodeCoordinates.of(7, 5, 1)),
            4, List.of(nodeCoordinates.of(4, 5, 0), nodeCoordinates.of(5, 4, 3), nodeCoordinates.of(6, 5, 2), 
                nodeCoordinates.of(5, 6, 1)),
            5, List.of(nodeCoordinates.of(6, 5, 0),nodeCoordinates.of(7, 4, 3),nodeCoordinates.of(9, 4, 3),
                nodeCoordinates.of(7, 6, 1),nodeCoordinates.of(9, 6, 1)),
            6, List.of(nodeCoordinates.of(5, 5, 0),nodeCoordinates.of(5, 7, 0),nodeCoordinates.of(6, 8, 1),
                nodeCoordinates.of(7, 7, 2),nodeCoordinates.of(7, 5, 2),nodeCoordinates.of(6, 4, 3)),
            7, List.of(nodeCoordinates.of(7, 4, 3),nodeCoordinates.of(5, 4, 3),nodeCoordinates.of(4, 5, 0),
                nodeCoordinates.of(4, 7, 0),nodeCoordinates.of(5, 8, 1),nodeCoordinates.of(7, 8, 1),
                nodeCoordinates.of(7, 6, 2)),
            8, List.of(nodeCoordinates.of(5, 6, 0),nodeCoordinates.of(5, 8, 0),nodeCoordinates.of(6, 9, 1),
                nodeCoordinates.of(8, 9, 1),nodeCoordinates.of(9, 8, 2),nodeCoordinates.of(9, 6, 2),
                nodeCoordinates.of(8, 5, 3),nodeCoordinates.of(6, 5, 3)));
        return mp;
    }
}
