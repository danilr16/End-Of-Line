package es.us.dp1.lx_xy_24_25.your_game_name.game;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.user.Authorities;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "appgames")
public class Game extends BaseEntity {

    @NotNull
    @Column
    Integer numPlayers;

    String chat; 

    Integer nTurn;

    //Map<User,Integer>  order;

    Integer duration;

    GameMode gameMode;

    GameState gameState;

	

    

}
