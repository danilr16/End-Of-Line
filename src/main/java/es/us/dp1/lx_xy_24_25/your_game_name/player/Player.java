package es.us.dp1.lx_xy_24_25.your_game_name.player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "appPlayers")
public class Player extends BaseEntity{

    public enum PlayerState {
        PLAYING, WON, LOST , SPECTATING
    }

    @Min(0)
    Integer score;

    @Min(0)
    @Max(3)
    @NotNull
    Integer energy;

    @Enumerated(EnumType.STRING)
    PlayerState state;

    @NotNull
    @ManyToOne(optional = false)
    User user;

    List<Integer> playedCards;

    LocalDateTime turnStarted; //Fecha de inicio de mi turno

    Boolean handChanged; //Cambio inicial de la mano

    Integer cardsPlayedThisTurn; //Numero de cartas jugadas esta ronda

    Boolean energyUsedThisRound;

    List<Integer> possiblePositions;
    //Listas cuyos valores van a pares: valor índice 0 de possiblePositions va relacionado con valor índice 0 de possibleRotations
    //Indican las posiciones posibles y rotaciones necesarias donde el jugador puede colocar una carta
    List<Integer> possibleRotations;

    @NotNull
    @OneToOne(optional = false, cascade = CascadeType.REMOVE)
    Hand hand;

    @OneToMany(cascade = CascadeType.REMOVE)
    @NotNull
    @JoinColumn(name = "player_id")
    List<PackCard> packCards;

    @PrePersist
    @PreUpdate
    private void prePersist() {
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
        if (handChanged == null) {
            handChanged = false;
        }
        if (cardsPlayedThisTurn == null) {
            cardsPlayedThisTurn = 0;
        }
        if (energyUsedThisRound == null) {
            energyUsedThisRound = false;
        }
        if (possiblePositions == null) {
            possiblePositions = new ArrayList<>();
        }
        if (possibleRotations == null) {
            possibleRotations = new ArrayList<>();
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
