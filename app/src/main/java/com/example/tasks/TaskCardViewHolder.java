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

    void bindTo(final TaskDataProvider taskData) {
        card.setClickable(true);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo make it possible to display all task types
                TaskDisplayActivity.launchActivity(TaskCardViewHolder.this.card.getContext(), taskData.getTaskType(), taskData.getId());
            }
        });
        if (taskData.getTitle().isEmpty()) {
            titleView.setText(R.string.no_title);
        } else {
            titleView.setText(taskData.getTitle());
        }
        if (titleColor != null) titleView.setTextColor(titleColor);
    }
}
