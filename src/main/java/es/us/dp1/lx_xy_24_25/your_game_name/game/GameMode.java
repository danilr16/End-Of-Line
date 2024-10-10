package es.us.dp1.lx_xy_24_25.your_game_name.game;

public enum GameMode {
    VERSUS(0),CLASSIC_SOLITARY(1), SOLITARY_PUZZLE(2), COOPERATIVE_PUZZLE(3), TEAM_BATTLE(4);
    private final int value;

    GameMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
