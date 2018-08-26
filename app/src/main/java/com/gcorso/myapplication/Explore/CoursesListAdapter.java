package com.gcorso.myapplication.Explore;

import android.content.Context;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gcorso.myapplication.Objects.Course;
import com.gcorso.myapplication.R;

import java.util.List;

/**
 * Adapter to manage and display the list view used for searched courses in the Search activity
 */

public class CoursesListAdapter extends BaseAdapter {

    private Context context;
    private List<Course> courses;

    public CoursesListAdapter(Context context, List<Course> courses) {
        this.context = context;
        this.courses = courses;
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public Object getItem(int position) {
        return courses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.course_item, parent, false);

        TextView tvCourseTitle = view.findViewById(R.id.course_title);
        tvCourseTitle.setText(courses.get(position).getTitle());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Click course", Toast.LENGTH_SHORT).show();
            }
        });

        LessonsHorizAdapter lessonsAdapter = new LessonsHorizAdapter(context, courses.get(position).getLessons(), courses.get(position).getCourseid());
        HorizontalGridView lessonsView = view.findViewById(R.id.gridlessons);
        lessonsView.setAdapter(lessonsAdapter);

        return view;
    }
}