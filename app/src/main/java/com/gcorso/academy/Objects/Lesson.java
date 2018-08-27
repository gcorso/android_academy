package com.gcorso.academy.Objects;

import java.util.List;

public class Lesson {
    private int lessonid;
    private String title;
    private List<String> sections;
    private int result;
    private int courseid;
    private int nsections;

    public Lesson(int lessonid, String title, List<String> sections, int result, int courseid) {
        this.lessonid = lessonid;
        this.title = title;
        this.sections = sections;
        this.result = result;
        this.courseid = courseid;
    }

    public Lesson(int lessonid, String title) {
        this.lessonid = lessonid;
        this.title = title;
    }

    public Lesson(int lessonid, String title, int result, int nsections) {
        this.lessonid = lessonid;
        this.title = title;
        this.result = result;
        this.nsections = nsections;
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

    public List<String> getSections() {
        return sections;
    }

    public void setSections(List<String> sections) {
        this.sections = sections;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getCourseid() {
        return courseid;
    }

    public void setCourseid(int courseid) {
        this.courseid = courseid;
    }

    public int getNsections() {
        return nsections;
    }

    public void setNsections(int nsections) {
        this.nsections = nsections;
    }
}
