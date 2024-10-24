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
@Table(name="appRows")
public class Row extends BaseEntity{

    @OneToMany
    @JoinColumn(name="row_id")
    @NotNull
    List<Cell> cells;
    
}
