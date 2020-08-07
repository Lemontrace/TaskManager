package com.example.tasks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


class TaskCardViewHolderAutoDate extends TaskCardViewHolderWithDateView {

    Integer dateColorOverdue;
    Integer dateColorToday;
    Integer dateColorRecurring;

    TaskCardViewHolderAutoDate(View view) {
        super(view);
    }

    @Override
    void bindTo(final TaskDataProvider taskData) {
        super.bindTo(taskData);
        if (taskData.getTaskType().equals(TaskDataProvider.TASK_TYPE_RECURRING_TASK)) {
            dateView.setText(R.string.recurring_task);
            if (dateColorRecurring != null) dateView.setTextColor(dateColorRecurring);
        } else if (taskData.getDate() == null) {
            //date not set
            dateView.setText(R.string.date_not_set);
        } else if (Date.isEqual(taskData.getDate(), Date.getToday())) {
            //today
            dateView.setText(R.string.today);
            if (dateColorToday != null) dateView.setTextColor(dateColorToday);
        } else {
            dateView.setText(Date.getDateStringInContext(card.getContext(), taskData.getDate()));
            if (taskData.getDate().compareTo(Date.getToday()) < 0) {
                //overdue
                if (dateColorOverdue != null) dateView.setTextColor(dateColorOverdue);
            }
        }
    }

    static class TaskCardViewHolderAutoDateFactory implements TaskCardViewHolderFactory {

        final Integer titleColor;
        final Integer dateColor;
        final Integer dateColorOverdue;
        final Integer dateColorToday;
        final Integer dateColorRecurring;

        TaskCardViewHolderAutoDateFactory(Integer titleColor, Integer dateColor, Integer dateColorOverdue, Integer dateColorToday, Integer dateColorRecurring) {
            this.titleColor = titleColor;
            this.dateColor = dateColor;
            this.dateColorOverdue = dateColorOverdue;
            this.dateColorToday = dateColorToday;
            this.dateColorRecurring = dateColorRecurring;
        }

        @Override
        public TaskCardViewHolder create(ViewGroup parent) {
            //params for making cardView fill the space
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
            );
            //create cardView and apply params
            CardView card = new CardView(parent.getContext());
            card.setLayoutParams(params);

            //inflate card body
            LayoutInflater.from(parent.getContext()).inflate(R.layout.task_view_card, card);

            //return viewHolder instance

            TaskCardViewHolderAutoDate taskCardViewHolderAutoDate = new TaskCardViewHolderAutoDate(card);
            taskCardViewHolderAutoDate.titleColor = titleColor;
            taskCardViewHolderAutoDate.dateColor = dateColor;
            taskCardViewHolderAutoDate.dateColorOverdue = dateColorOverdue;
            taskCardViewHolderAutoDate.dateColorToday = dateColorToday;
            taskCardViewHolderAutoDate.dateColorRecurring = dateColorRecurring;

            return taskCardViewHolderAutoDate;
        }
    }

}
