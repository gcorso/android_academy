package com.gcorso.myapplication.Objects;

public class Section {
    private int lessonid;
    private int sectionn;
    private String title;
    private String text;
    private String lessontitle;
    private int lessonsections;

    public Section(int lessonid, int sectionn, String title, String text, String lessontitle, int lessonsections) {
        this.lessonid = lessonid;
        this.sectionn = sectionn;
        this.title = title;
        this.text = text;
        this.lessontitle = lessontitle;
        this.lessonsections = lessonsections;
    }


    public int getLessonid() {
        return lessonid;
    }

    public void setLessonid(int lessonid) {
        this.lessonid = lessonid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLessontitle() {
        return lessontitle;
    }

    public void setLessontitle(String lessontitle) {
        this.lessontitle = lessontitle;
    }

    public int getSectionn() {
        return sectionn;
    }

    public void setSectionn(int sectionn) {
        this.sectionn = sectionn;
    }

    public void setLessonsections(int lessonsections) {
        this.lessonsections = lessonsections;
    }

    public int getLessonsections() {
        return lessonsections;
    }
}
