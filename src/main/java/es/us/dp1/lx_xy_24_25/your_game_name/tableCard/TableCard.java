package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

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

    @OneToMany
    @NotNull
    @JoinColumn(name="table_id")
    List<Row> rows;
}
