/*
 *  Copyright (c) 2018 Gabriele Corso
 *
 *  Distributed under the MIT software license, see the accompanying
 *  file LICENSE or http://www.opensource.org/licenses/mit-license.php.
 */

package com.gcorso.academy.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gcorso.academy.persistence.LessonsLDH;
import com.gcorso.academy.objects.Section;
import com.gcorso.academy.R;

public class SectionActivity extends AppCompatActivity {

    TextView pageTv;
    ImageView navprecBt;
    ImageView navnextBt;
    ImageView closeBt;
    TextView sectiontitleTv;
    ImageView sectionimage;
    TextView sectiontextTv;
    Section section;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);

        Intent intent = getIntent();
        final int lessonid = intent.getIntExtra("lessonid", 0);
        final int sectionn = intent.getIntExtra("sectionn", 0);

        LessonsLDH lessonsLDH = LessonsLDH.getInstance(this);
        section = lessonsLDH.getSection(lessonid, sectionn);

        if(section == null){
            Toast.makeText(SectionActivity.this, "Problems while loading section "
                    + Integer.toString(sectionn), Toast.LENGTH_LONG).show();
        }

        pageTv = findViewById(R.id.page);
        navprecBt  = findViewById(R.id.navprec);
        navnextBt = findViewById(R.id.navnext);
        closeBt = findViewById(R.id.closebt);
        sectiontitleTv = findViewById(R.id.sectiontitle);
        sectionimage = findViewById(R.id.sectionimage);
        sectiontextTv = findViewById(R.id.sectiontext);

        sectiontitleTv.setText(section.getTitle());
        String imagename = "s" + Integer.toString(lessonid) + Integer.toString(sectionn);
        sectionimage.setImageResource(getResources().getIdentifier(imagename,
                "drawable", getPackageName()));
        sectiontextTv.setText(section.getText());

        String page = section.getLessonTitle() + "  " + Integer.toString(sectionn+1)
                + "/" + Integer.toString(section.getLessonSections());
        pageTv.setText(page);

        closeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SectionActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        if(sectionn == 0){
            navprecBt.setVisibility(View.GONE);
        } else {
            navprecBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    changeSection(lessonid, sectionn-1);
                }
            });
        }

        navnextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(sectionn<section.getLessonSections()-1){
                changeSection(lessonid, sectionn+1);
            } else {
                startQuiz(lessonid);
            }
            }
        });
    }

    void changeSection(int lessonid, int sectionn){
        Intent intent = new Intent(SectionActivity.this, SectionActivity.class);
        intent.putExtra("sectionn", sectionn);
        intent.putExtra("lessonid", lessonid);
        startActivity(intent);
    }

    void startQuiz(int lessonid){
        Intent intent = new Intent(SectionActivity.this, QuizActivity.class);
        intent.putExtra("lessonid", lessonid);
        startActivity(intent);
    }
}
