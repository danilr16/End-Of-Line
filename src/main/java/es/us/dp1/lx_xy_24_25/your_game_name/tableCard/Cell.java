package es.us.dp1.lx_xy_24_25.your_game_name.tableCard;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="appCells")
public class Cell extends BaseEntity{

    Boolean isFull;

    @OneToOne
    @JoinColumn
    Card card;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (isFull == null) {
            isFull = false;
        }
    }
    
}
