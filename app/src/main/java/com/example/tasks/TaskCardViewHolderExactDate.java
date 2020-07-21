package com.example.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

class TaskCardViewHolderExactDate extends TaskCardViewHolderWithDateView {

    TaskCardViewHolderExactDate(View view) {
        super(view);
    }

    @Override
    void bindTo(final TaskDataProvider taskData) {
        super.bindTo(taskData);
        if (taskData.getDate() == null) {
            dateView.setText(R.string.date_not_set);
        } else {
            dateView.setText(Date.getDateString(taskData.getDate()));
        }
    }

    static class TaskCardViewHolderExactDateFactory implements TaskCardViewHolderFactory {


        Integer titleColor;
        Integer dateColor;

        TaskCardViewHolderExactDateFactory(Integer titleColor,Integer dateColor) {
            this.titleColor=titleColor;
            this.dateColor=dateColor;
        }

        @Override
        public TaskCardViewHolder create(ViewGroup parent) {
            //params for making cardview fill the space
            RecyclerView.LayoutParams params=new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT
            );
            //create cardview and apply params
            CardView card=new CardView(parent.getContext());
            card.setLayoutParams(params);

            //inflate card body
            LayoutInflater.from(parent.getContext()).inflate(R.layout.task_view_card,card);

            //get viewholder instance and set colors
            TaskCardViewHolderExactDate taskCardViewHolderExactDate = new TaskCardViewHolderExactDate(card);
            taskCardViewHolderExactDate.titleColor=titleColor;
            taskCardViewHolderExactDate.dateColor=dateColor;

            return taskCardViewHolderExactDate;
        }
    }
}
