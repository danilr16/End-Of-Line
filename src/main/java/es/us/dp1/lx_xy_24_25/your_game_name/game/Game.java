package es.us.dp1.lx_xy_24_25.your_game_name.game;


import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.player.Player;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.tableCard.TableCard;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.persistence.Version;

import java.time.LocalDateTime;

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

    @Version
    private Integer version;

    @Column(unique = true)
    String gameCode;

    @ManyToOne
    User host;

    Boolean isPublic;

    Integer numPlayers;

    @ElementCollection
    List<ChatMessage> chat; 

    Integer nTurn;

    Integer duration;

    LocalDateTime started;

    Integer turn; //Id del jugador que tiene el turno

    List<Integer> orderTurn; //Lista de ids de jugadores, representa el orden de los turnos de la ronda actual

    List<Integer> initialTurn; //Lista de ids de jugadores, representa el orden de los turnos de la primera ronda

    @Enumerated(EnumType.STRING)
    GameMode gameMode;

    @Enumerated(EnumType.STRING)
    GameState gameState;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn
    List<Player> spectators;

    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name="game_id")
    List<Player> players;

    @OneToOne(cascade = CascadeType.REMOVE)
    TableCard table;

    public Game(Game g){
        this.gameCode = g.getGameCode();
        this.host = g.getHost();
        this.isPublic = g.getIsPublic();
        this.numPlayers = g.getNumPlayers();
        this.chat = g.getChat();
        this.nTurn = g.getNTurn();
        this.duration = g.getDuration();
        this.started = g.getStarted();
        this.turn = g.getTurn(); 
        this.orderTurn = g.getOrderTurn();
        this.initialTurn = g.getInitialTurn();
        this.gameMode = g.getGameMode();
        this.gameState = g.getGameState();
        this.spectators = g.getSpectators();
        this.players = g.getPlayers();
        this.table = g.getTable();
    }

    public Game(){}

    @PrePersist
    @PreUpdate
    private void prePersist() {
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
            chat = new ArrayList<>();
        }
        if (nTurn == null) {
            nTurn = 0;
        }
        if (orderTurn == null) {
            orderTurn = new ArrayList<>();
        }
    }
}
