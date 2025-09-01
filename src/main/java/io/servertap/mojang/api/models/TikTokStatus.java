package io.servertap.mojang.api.models;

public class TikTokStatus {
    private boolean live;

    public TikTokStatus(boolean live) {
        this.live = live;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }
}