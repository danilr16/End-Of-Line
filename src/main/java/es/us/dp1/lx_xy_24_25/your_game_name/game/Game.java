package es.us.dp1.lx_xy_24_25.your_game_name.game;


import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "appgames")
public class Game extends BaseEntity {

    @Column(unique = true)
    String gameCode;

    @ManyToOne
    User host;

    Boolean isPublic;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (gameCode == null) {
            String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            Random random = new Random();
            StringBuilder a = new StringBuilder(5);
            for (int i = 0; i < 5; i++) {
                int index = random.nextInt(letters.length());
                a.append(letters.charAt(index));
            }
            gameCode = a.toString();
        }
        if (duration == null) {
            duration = 0;
        }
        if (gameState == null) {
            gameState = GameState.WAITING; 
        }
        if (spectators == null) {
            spectators = new ArrayList<>();
        }
        if (isPublic == null) {
            isPublic = true;
        }
        if (chat == null) {
            chat = "";
        }
        if (nTurn == null) {
            nTurn = 0;
        }
    }

    @NotNull
    Integer numPlayers;

    String chat; 

    Integer nTurn;

    Integer duration;

    GameMode gameMode;

    GameState gameState;

    @OneToMany
    @JoinColumn(name="game_id")
    List<Player> spectators;

    @OneToMany
    @JoinColumn(name="game_id")
    List<Player> players;//autocompletar con jugador de user

    @OneToOne
    TableCard table;

}
