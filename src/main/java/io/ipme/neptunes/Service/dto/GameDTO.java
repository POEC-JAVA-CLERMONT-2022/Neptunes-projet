package io.ipme.neptunes.Service.dto;

import io.ipme.neptunes.Model.GameMode;

public class GameDTO {

    private String gameUrl;
    private Boolean isPaused;
    private GameMode gameMode;

    public String getGameUrl() {
        return gameUrl;
    }

    public Boolean getPaused() { return isPaused; }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
    }

    public void setPaused(Boolean paused) {
        isPaused = paused;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
}
