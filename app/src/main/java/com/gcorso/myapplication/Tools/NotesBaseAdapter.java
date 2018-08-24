package com.gcorso.myapplication.Tools;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gcorso.myapplication.Objects.Note;
import com.gcorso.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class NotesBaseAdapter extends BaseAdapter {
    private List<Note> notesList = new ArrayList<Note>();

    public NotesBaseAdapter(List<Note> notesList){
        this.notesList = notesList;
    }


    @Override
    public int getCount() {
        return notesList.size();
    }

    @Override
    public Object getItem(int position) {
        return notesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view==null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.note_item, parent, false);
        }

        Note note = notesList.get(position);

        TextView titleTv = (TextView) view.findViewById(R.id.notetitle);
        titleTv.setText(note.getTitle());

        TextView textTv = (TextView) view.findViewById(R.id.notetext);
        textTv.setText(note.getText());

        return view;


    }

    public void setNotesList(List<Note> notesList) {
        this.notesList = notesList;
    }
}
