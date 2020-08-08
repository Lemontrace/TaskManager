package com.example.tasks;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;

abstract class TaskCardViewHolderWithDateView extends TaskCardViewHolder {
    final TextView dateView;
    @ColorInt
    Integer dateColor = null;

    TaskCardViewHolderWithDateView(View view){
        super(view);
        ViewGroup linearLayout=(ViewGroup) card.getChildAt(0);
        dateView=(TextView)  linearLayout.getChildAt(1);
    }

    @Override
    void bindTo(final TaskDataProvider taskData) {
        super.bindTo(taskData);
        if (dateColor != null) dateView.setTextColor(dateColor);
    }

}
