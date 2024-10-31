package es.us.dp1.lx_xy_24_25.your_game_name.player;

import java.util.List;

import org.hibernate.validator.constraints.Range;

import es.us.dp1.lx_xy_24_25.your_game_name.hand.Hand;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.packCards.PackCard;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "appPlayers")
public class Player extends BaseEntity{

    public enum PlayerState {
        PLAYING, WON, LOST
    }

    @Range(min = 0)
    Integer score;

    @Range(min = 0, max = 3)
    @NotNull
    Integer energy;

    @Enumerated(EnumType.STRING)
    PlayerState state;

    @NotNull
    @ManyToOne(optional = false)
    User user;

    List<Integer> playedCards;

    @NotNull
    @OneToOne(optional = false, cascade = CascadeType.REMOVE)
    Hand hand;

    @OneToMany(cascade = CascadeType.REMOVE)
    @NotNull
    @JoinColumn(name = "player_id")
    List<PackCard> packCards;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (score == null) {
            score = 0;
        }
        if (energy == null) {
            energy = 3;
        }
        if (playedCards == null) {
            playedCards = new ArrayList<>();
        }
        if (state == null) {
            state = PlayerState.PLAYING;
        }
        if (packCards == null) {
            packCards = new ArrayList<>();
        }
    }

    public Boolean canUseEnergy() {
        if (this.energy > 0) {
            return true;
        } else {
            return false;
        }
    }
}
