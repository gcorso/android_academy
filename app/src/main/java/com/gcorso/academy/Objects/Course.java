package com.gcorso.academy.Objects;

import java.util.List;

public class Course {
    private int courseid;
    private String title;
    private List<Lesson> lessons;
    private int percentageProgress;

    public Course(int courseid, String title) {
        this.courseid = courseid;
        this.title = title;
    }

    public Course(int courseid, String title, List<Lesson> lessons) {
        this.courseid = courseid;
        this.title = title;
        this.lessons = lessons;
    }

    public Course(String title, int percentageProgress) {
        this.title = title;
        this.percentageProgress = percentageProgress;
    }

    public int getCourseid() {
        return courseid;
    }

    public void setCourseid(int courseid) {
        this.courseid = courseid;
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
