package com.cleutonsampaio.yamlab;

import java.util.Map;

public class Config {
    public String serverUrl;
    public int port;
    public Map<String, User> users;

    @Override
    public String toString() {
        return "Config{" + "serverUrl='" + serverUrl + '\'' + ", port=" + port + ", users=" + users + '}';
    }
}
