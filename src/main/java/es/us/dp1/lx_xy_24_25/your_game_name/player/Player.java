package es.us.dp1.lx_xy_24_25.your_game_name.player;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "appPlayers")
public class Player extends BaseEntity{

    Integer score;

    Integer energy;

    User user;

    public Boolean canUseEnergy() {
        if (this.energy > 0) {
            return true;
        } else {
            return false;
        }
    }
}
