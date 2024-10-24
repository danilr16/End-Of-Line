package es.us.dp1.lx_xy_24_25.your_game_name.game;

public enum GameState {
    WAITING(0),IN_PROCESS(1),END(2);

    private final int value;

    GameState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
