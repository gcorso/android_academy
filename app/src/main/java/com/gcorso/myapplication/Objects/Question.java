package com.gcorso.myapplication.Objects;

public class Question {

    private int nquestion;
    private int sectionid;
    private int[] results;
    private String title;
    private String[] answers;


    public Question(int nquestion, int sectionid, String title, String[] answers) {
        this.nquestion = nquestion;
        this.sectionid = sectionid;
        this.title = title;
        this.answers = answers;
    }

    public int getNquestion() {
        return nquestion;
    }

    public void setNquestion(int nquestion) {
        this.nquestion = nquestion;
    }

    public int getSectionid() {
        return sectionid;
    }

    public void setSectionid(int sectionid) {
        this.sectionid = sectionid;
    }

    public int[] getResults() {
        return results;
    }

    public void setResults(int[] results) {
        this.results = results;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getAnswers() {
        return answers;
    }

    public void setAnswers(String[] answers) {
        this.answers = answers;
    }
}
