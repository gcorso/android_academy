package com.gcorso.academy.objects;

import java.util.List;

public class Course {
    private int id;
    private String title;
    private List<Lesson> lessons;
    private int percentageProgress;

    public Course(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Course(int id, String title, List<Lesson> lessons) {
        this.id = id;
        this.title = title;
        this.lessons = lessons;
    }

    public Course(String title, int percentageProgress) {
        this.title = title;
        this.percentageProgress = percentageProgress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public int getPercentageProgress() {
        return percentageProgress;
    }

    public void setPercentageProgress(int percentageProgress) {
        this.percentageProgress = percentageProgress;
    }
}
