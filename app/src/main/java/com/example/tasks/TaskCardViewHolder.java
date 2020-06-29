package com.example.tasks;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

abstract class TaskCardViewHolder extends RecyclerView.ViewHolder {

    CardView card;
    TextView titleView;
    @ColorInt
    Integer titleColor=null;


    TaskCardViewHolder(View view) {
        super(view);
        card=(CardView) view;
        ViewGroup linearLayout=(ViewGroup) card.getChildAt(0);
        titleView=(TextView) linearLayout.getChildAt(0);
    }

    void bindTo(final Task task) {
        card.setClickable(true);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskDisplayActivity.launchActivity(TaskCardViewHolder.this.card.getContext(),task.id);
            }
        });
        if (task.title.isEmpty()) {
            titleView.setText(R.string.no_title);
        } else {
            titleView.setText(task.title);
        }
        if (titleColor!=null) titleView.setTextColor(titleColor);
    }
}
