package com.upwork.docker.dto;

public class CreateContainerRequest {
    private String image;
    private String[] cmd;

    // getters and setters

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String[] getCmd() {
        return cmd;
    }

    public void setCmd(String[] cmd) {
        this.cmd = cmd;
    }
}

