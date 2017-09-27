package com.example.tools.byteman.beans;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("byteman")
public class BytemanProperties {
    private List<String> scriptFolders;
    private long pollingInterval=5000L;


    public List<String> getScriptFolders() {
        return scriptFolders;
    }

    public void setScriptFolders(List<String> scriptFolders) {
        this.scriptFolders = scriptFolders;
    }

    public long getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }
}
