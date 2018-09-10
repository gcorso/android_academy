package com.gcorso.cyberacademy.vault;

public class Note {
    private int noteid;
    private String title;
    private byte[] data;
    private String text;

    public Note(int noteid, String title, byte[] data) {
        this.noteid = noteid;
        this.title = title;
        this.data = data;
    }

    public int getNoteid() {
        return noteid;
    }

    public void setNoteid(int noteid) {
        this.noteid = noteid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
