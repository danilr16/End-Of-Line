package es.us.dp1.lx_xy_24_25.your_game_name.game;

public enum GameMode {
    VERSUS(0),CLASSIC_SINGLE(1), PUZZLE_COOP(2), TEAM_BATTLE(3);
    private final int value;

    GameMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
