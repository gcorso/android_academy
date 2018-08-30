/*
 *  Copyright (c) 2018 Gabriele Corso
 *
 *  Distributed under the MIT software license, see the accompanying
 *  file LICENSE or http://www.opensource.org/licenses/mit-license.php.
 */

package com.gcorso.academy.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gcorso.academy.Activities.SectionActivity;
import com.gcorso.academy.Layout.FitDoughnut;
import com.gcorso.academy.Objects.Lesson;
import com.gcorso.academy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter to manage and display the horizontal scroll list view used for lessons in the Home activity and Browse activity
 */

public class LessonsHorizAdapter extends RecyclerView.Adapter<LessonsHorizAdapter.SimpleViewHolder>{

    private Context context;
    private List<Lesson> lessons;
    private int courseid;

    public LessonsHorizAdapter(Context context, List<Lesson> elements, int courseid){
        this.context = context;
        this.lessons = elements;
        this.courseid = courseid;
        if (lessons == null){
            lessons = new ArrayList<>();
        }
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvLessonTitle;
        public final RelativeLayout allView;
        public final ImageView iconLesson;
        public final FitDoughnut doughnut;

        public SimpleViewHolder(View view) {
            super(view);
            tvLessonTitle = (TextView) view.findViewById(R.id.lesson_title);
            allView = (RelativeLayout) view.findViewById(R.id.allview);
            doughnut = (FitDoughnut) view.findViewById(R.id.doughnutres);
            iconLesson = view.findViewById(R.id.lesson_icon);
        }
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(this.context).inflate(R.layout.lesson_item_h, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        holder.tvLessonTitle.setText(lessons.get(position).getTitle());

        String backgroundname = "course" + Integer.toString(courseid);
        holder.iconLesson.setBackgroundResource(context.getResources().getIdentifier(backgroundname,
                "drawable", context.getPackageName()));

        String iconname = "z" + Integer.toString(lessons.get(position).getLessonid());
        holder.iconLesson.setImageResource(context.getResources().getIdentifier(iconname,
                "drawable", context.getPackageName()));

        if(lessons.get(position).getResult()>0){
            holder.doughnut.setVisibility(View.VISIBLE);
            holder.doughnut.animateSetPercent(((float) lessons.get(position).getResult()*10) - 0.01f);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lessons.get(position).getNsections() == 0){
                    Toast.makeText(context, "Ci dispiace questa lezione non è ancora disponibile. Prova invece le lezioni su passwords, virus, VPN e GDPR.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(context, SectionActivity.class);
                    intent.putExtra("sectionn", 0);
                    intent.putExtra("lessonid", lessons.get(position).getLessonid());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.lessons.size();
    }
}