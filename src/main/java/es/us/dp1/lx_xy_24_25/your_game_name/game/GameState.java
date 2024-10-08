package es.us.dp1.lx_xy_24_25.your_game_name.game;

public enum GameState {
    IN_PROCESS(0),END(1);

    private final int value;

    GameState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
