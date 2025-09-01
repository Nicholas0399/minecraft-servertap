package io.servertap.api.v1.models;

import com.google.gson.annotations.Expose;

public class TikTokStatus {
    @Expose
    private String username;
    
    @Expose
    private Boolean streaming;
    
    @Expose
    private Boolean manual;
    
    @Expose
    private Long lastChecked;
    
    @Expose
    private Boolean isLive;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean isStreaming() {
        return streaming;
    }

    public void setStreaming(Boolean streaming) {
        this.streaming = streaming;
    }
    
    public Boolean isManual() {
        return manual;
    }
    
    public void setManual(Boolean manual) {
        this.manual = manual;
    }
    
    public Long getLastChecked() {
        return lastChecked;
    }
    
    public void setLastChecked(Long lastChecked) {
        this.lastChecked = lastChecked;
    }
    
    public Boolean isLive() {
        return isLive;
    }
    
    public void setLive(Boolean isLive) {
        this.isLive = isLive;
    }
}