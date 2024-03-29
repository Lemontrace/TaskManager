package com.example.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

class TaskCardViewHolderDateToday extends TaskCardViewHolderWithDateView {

    TaskCardViewHolderDateToday(View view) {
        super(view);
    }

    void bindTo(final Task task) {
        super.bindTo(task);
        dateView.setText(R.string.today);
    }

    static class TaskCardViewHolderDateTodayFactory implements TaskCardViewHolderFactory {

        Integer titleColor;
        Integer dateColor;
        TaskCardViewHolderDateTodayFactory(Integer titleColor,Integer dateColor) {
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

            TaskCardViewHolderDateToday taskCardViewHolderDateToday = new TaskCardViewHolderDateToday(card);
            taskCardViewHolderDateToday.titleColor=titleColor;
            taskCardViewHolderDateToday.dateColor=dateColor;
            return taskCardViewHolderDateToday;
        }
    }

}
