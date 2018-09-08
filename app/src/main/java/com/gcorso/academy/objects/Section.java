package com.gcorso.academy.objects;

public class Section {
    private int lessonId;
    private int sectionN;
    private String title;
    private String text;
    private String lessonTitle;
    private int lessonSections;

    public Section(int lessonId, int sectionN, String title, String text, String lessonTitle, int lessonSections) {
        this.lessonId = lessonId;
        this.sectionN = sectionN;
        this.title = title;
        this.text = text;
        this.lessonTitle = lessonTitle;
        this.lessonSections = lessonSections;
    }


    public int getLessonId() {
        return lessonId;
    }

    public void setLessonId(int lessonId) {
        this.lessonId = lessonId;
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

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public int getSectionN() {
        return sectionN;
    }

    public void setSectionN(int sectionN) {
        this.sectionN = sectionN;
    }

    public void setLessonSections(int lessonSections) {
        this.lessonSections = lessonSections;
    }

    public int getLessonSections() {
        return lessonSections;
    }
}
