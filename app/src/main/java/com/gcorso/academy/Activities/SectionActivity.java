package com.gcorso.academy.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gcorso.academy.LessonsLDH;
import com.gcorso.academy.Objects.Section;
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

        String page = section.getLessontitle() + "  " + Integer.toString(sectionn+1) + "/" + Integer.toString(section.getLessonsections());
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
                    Intent intent = new Intent(SectionActivity.this, SectionActivity.class);
                    intent.putExtra("sectionn", sectionn-1);
                    intent.putExtra("lessonid", lessonid);
                    startActivity(intent);
                }
            });
        }

        navnextBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sectionn<section.getLessonsections()-1){
                    Intent intent = new Intent(SectionActivity.this, SectionActivity.class);
                    intent.putExtra("sectionn", sectionn+1);
                    intent.putExtra("lessonid", lessonid);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SectionActivity.this, QuizActivity.class);
                    intent.putExtra("lessonid", lessonid);
                    startActivity(intent);
                }
            }
        });
    }
}
