package es.us.dp1.lx_xy_24_25.your_game_name.player;

import java.util.List;

import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "appPlayers")
public class Player extends BaseEntity{

    Integer score;

    Integer energy;

    @NotNull
    @ManyToOne(optional = false)
    User user;

    List<Integer> playedCards;

    public Boolean canUseEnergy() {
        if (this.energy > 0) {
            return true;
        } else {
            return false;
        }
    }

    @NotNull
    @OneToOne(optional = false)
    Hand hand;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (score == null) {
            score = 0;
        }
        if (energy == null) {
            energy = 3;
        }
    }

}
