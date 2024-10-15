package es.us.dp1.lx_xy_24_25.your_game_name.game;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "appgames")
public class Game extends BaseEntity {

    @Column(unique = true)
    String gameCode;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (gameCode == null ) {
            gameCode = UUID.randomUUID().toString().substring(0, 8);
        }
    }


    Integer numPlayers;

    String chat; 

    Integer nTurn;


    Integer duration;

    GameMode gameMode;

    GameState gameState;

	

    

}
