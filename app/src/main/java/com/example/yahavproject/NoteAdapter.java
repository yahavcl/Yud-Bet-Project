package com.example.yahavproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class NoteAdapter extends ArrayAdapter<NoteItem> {
    Context context;
    List<NoteItem> objects;

    public NoteAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<NoteItem> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.objects = objects;
    }

    @SuppressLint("ResourceAsColor")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.item_note, parent, false);
        TextView tvTitle = (TextView)view.findViewById(R.id.tv_title_note);
        TextView tvTime = (TextView)view.findViewById(R.id.tv_time_note);
        ImageView ivClock = (ImageView)view.findViewById(R.id.iv_clock);
        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.lo_item_note);

        NoteItem temp = objects.get(position);


        tvTitle.setText(String.valueOf(temp.note_title));
        tvTime.setText(String.valueOf(temp.note_time));


        if (temp.noteColor == R.color.green_button)
            linearLayout.setBackgroundResource(R.drawable.note_shape_green);

        else if (temp.noteColor == R.color.blue_button)
            linearLayout.setBackgroundResource(R.drawable.note_shape_blue);

        else if (temp.noteColor == R.color.red_button)
            linearLayout.setBackgroundResource(R.drawable.note_shape_red);

        else if (temp.noteColor == R.color.yellow_button)
            linearLayout.setBackgroundResource(R.drawable.note_shape_yellow);

        else if (temp.noteColor == R.color.purple_button)
            linearLayout.setBackgroundResource(R.drawable.note_shape_purple);

        else if (temp.noteColor == R.color.white)
            linearLayout.setBackgroundResource(R.drawable.note_shape_white);

        else if (temp.noteDone == true){
            linearLayout.setBackgroundResource(R.drawable.note_shape_done);
            tvTime.setText("DONE");
            tvTime.setTextSize(24);
            tvTime.setTextColor(Color.WHITE);
            tvTitle.setTextColor(Color.WHITE);
            ivClock.setVisibility(View.INVISIBLE);
        }

        /*else if (SundayActivity.isDone == true ){
            linearLayout.setBackgroundResource(R.drawable.note_shape_done);
            tvTime.setText("DONE");
            tvTime.setTextSize(24);
            tvTime.setTextColor(Color.WHITE);
            tvTitle.setTextColor(Color.WHITE);
            ivClock.setVisibility(View.INVISIBLE);
        }*/



        /*tvTitle.setBackgroundResource(temp.noteColor);
        tvTime.setBackgroundResource(temp.noteColor);*/

        //linearLayout.setBackgroundColor(temp.noteColor);



        return view;
    }

}


