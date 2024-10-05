package es.us.dp1.lx_xy_24_25.your_game_name.player;

import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player extends User{

    Integer score;

    Integer energy;

    public Boolean canUseEnergy() {
        if (this.energy > 0) {
            return true;
        } else {
            return false;
        }
    }
}
