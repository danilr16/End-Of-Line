package es.us.dp1.lx_xy_24_25.your_game_name.team;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player.PlayerState;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "appteam")
public class Team extends BaseEntity {

    @OneToOne(optional = false)
    Player player1;

    @OneToOne
    Player player2;

    public Boolean lostTeam() {
        Boolean res = false;
        if (this.getPlayer1() != null && this.getPlayer2() != null) {
            res = res || (this.getPlayer1().getState().equals(PlayerState.LOST) && this.getPlayer2().getState().equals(PlayerState.LOST));
        }
        return res;
    }
    
}
