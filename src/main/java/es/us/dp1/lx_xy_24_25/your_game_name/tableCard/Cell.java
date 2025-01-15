package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="appCells")
public class Cell extends BaseEntity{

    @NotNull
    Boolean isFull;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn
    Card card;

    @PrePersist
    @PreUpdate
    private void prePersist() {
        if (isFull == null) {
            isFull = false;
        }
    }
    
}
