package es.us.dp1.lx_xy_24_25.your_game_name.packCards;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="appPackCards")
public class PackCard extends BaseEntity{

    @Min(0)
    @Max(25)
    Integer numCards;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name="packCard_id")
    List<Card> cards;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (numCards == null) {
            numCards = 25;
        }
    }
    
}
