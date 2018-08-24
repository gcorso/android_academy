package com.gcorso.myapplication.Explore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gcorso.myapplication.Objects.FitDoughnut;
import com.gcorso.myapplication.Objects.Level;
import com.gcorso.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    String[] questions;
    String lessontitle;
    LessonsLDH lessonsLDH;
    int curq;
    int lessonid;
    public static final String Q_SEP = "<<-->>";
    public static final String A_SEP = "<->";
    boolean[] results;

    TextView lessontitleTv;
    TextView questiontitleTv;
    Button[] answersBt;
    ImageView[] resqIv;
    RelativeLayout emptyview;
    ImageView closeBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();
        lessonid = intent.getIntExtra("lessonid", 0);

        lessonsLDH = new LessonsLDH(this);
        String q = lessonsLDH.getQuestion(lessonid);
        lessontitle = lessonsLDH.getLessonTitle(lessonid);

        questions = q.split(Q_SEP);
        curq = 0;

        lessontitleTv = findViewById(R.id.lesson_title);
        questiontitleTv = findViewById(R.id.question_title);

        answersBt = new Button[]{findViewById(R.id.answer1), findViewById(R.id.answer2), findViewById(R.id.answer3), findViewById(R.id.answer4)};
        resqIv = new ImageView[]{findViewById(R.id.resq0), findViewById(R.id.resq1), findViewById(R.id.resq2),
                findViewById(R.id.resq3), findViewById(R.id.resq4),findViewById(R.id.resq5),findViewById(R.id.resq6),
                findViewById(R.id.resq7),findViewById(R.id.resq8),findViewById(R.id.resq9)};
        emptyview = findViewById(R.id.emptyview);
        emptyview.setVisibility(View.GONE);

        lessontitleTv.setText(lessontitle);

        results = new boolean[]{false, false, false, false, false, false, false, false, false, false};

        emptyview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestion();
            }
        });

        setCurrentQuestion();

        closeBt = findViewById(R.id.closebt);
        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void nextQuestion(){
        if(curq == 9){
            int tot = 0;
            for(int i = 0; i<10; i++){
                if(results[i]) {
                    tot++;
                }
            }

            int ch = 5*lessonsLDH.updateResult(lessonid, tot);

            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.result_dialog, null);

            final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(QuizActivity.this).setView(dialogView).show();

            TextView tvCompl = dialogView.findViewById(R.id.tvcompl);
            TextView tvScore = dialogView.findViewById(R.id.tvscore);
            TextView tvResult = dialogView.findViewById(R.id.tvresult);

            if(tot == 10){
                tvCompl.setText("Perfetto!");
            } else if (tot>=7){
                tvCompl.setText("Complimenti!");
            } else {
                tvCompl.setText("Riprova!");
                tvCompl.setTextColor(getResources().getColor(R.color.wrong));
                TextView tvExtra = dialogView.findViewById(R.id.tvextra);
                tvExtra.setVisibility(View.VISIBLE);
            }

            String res = "Punteggio:\n" + Integer.toString(tot) + "/10";
            tvResult.setText(res);

            String change = "+" + Integer.toString(ch) + "\nCyberPower";
            tvScore.setText(change);

            // level
            Level level = lessonsLDH.getLevel();
            FitDoughnut doughnut = (FitDoughnut) dialogView.findViewById(R.id.doughnuttot);
            doughnut.animateSetPercent((float) level.getPerctot());
            TextView tvperctot = dialogView.findViewById(R.id.tvpercentage);
            String p = Integer.toString(level.getPerctot())+ "%";
            tvperctot.setText(p);
            TextView tvLev = dialogView.findViewById(R.id.tvlevel);
            tvLev.setText(level.getLiv());
            TextView tvProg = dialogView.findViewById(R.id.tvprogress);
            String prog = Integer.toString(level.getProg()) + " / " + Integer.toString(level.getTot());
            tvProg.setText(prog);

            Button btRiprova = dialogView.findViewById(R.id.btRiprova);
            btRiprova.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(QuizActivity.this, SectionActivity.class);
                    intent.putExtra("sectionn", 0);
                    intent.putExtra("lessonid", lessonid);
                    startActivity(intent);
                }
            });

            Button btHome = dialogView.findViewById(R.id.btHome);
            btHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Intent intent = new Intent(QuizActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
            });

        } else {
            emptyview.setVisibility(View.GONE);
            curq++;
            setCurrentQuestion();
        }
    }

    private void setCurrentQuestion(){
        String[] parts = questions[curq].split(A_SEP);
        questiontitleTv.setText(parts[0]);
        final int correctans;

        if(parts.length == 2){
            // vero o falso
            answersBt[0].setBackground(getResources().getDrawable(R.drawable.answerbox));
            answersBt[1].setBackground(getResources().getDrawable(R.drawable.answerbox));
            answersBt[0].setText("Vero");
            answersBt[1].setText("Falso");
            answersBt[2].setVisibility(View.GONE);
            answersBt[3].setVisibility(View.GONE);

            if(parts[1].charAt(0)== 'V'){
                correctans = 0;
            } else {
                correctans = 1;
            }
        } else {
            // 4 opzioni

            List<Integer> list = new ArrayList<Integer>();
            list.add(0);
            list.add(1);
            list.add(2);
            list.add(3);
            java.util.Collections.shuffle(list);

            correctans = list.indexOf(0);

            for(int i = 0; i<4; i++){
                answersBt[i].setVisibility(View.VISIBLE);
                answersBt[i].setBackground(getResources().getDrawable(R.drawable.answerbox));
                answersBt[i].setText(parts[list.get(i)+1]);
            }
        }

        answersBt[correctans].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answersBt[correctans].setBackground(getResources().getDrawable(R.drawable.answerboxcorrect));
                results[curq] = true;
                resqIv[curq].setBackground(getResources().getDrawable(R.drawable.qcorrect));
                emptyview.setVisibility(View.VISIBLE);
            }
        });

        for(int i = 0; i<4; i++){
            if(i != correctans){
                final int finalI = i;
                answersBt[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        answersBt[correctans].setBackground(getResources().getDrawable(R.drawable.answerboxcorrect));
                        answersBt[finalI].setBackground(getResources().getDrawable(R.drawable.answerboxwrong));
                        resqIv[curq].setBackground(getResources().getDrawable(R.drawable.qwrong));
                        emptyview.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }
}
