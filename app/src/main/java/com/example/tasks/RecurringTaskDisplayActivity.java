package com.example.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class RecurringTaskDisplayActivity extends AppCompatActivity {

    static final String EXTRA_TASK_ID = "task_id";


    private RecurringTask task;

    static void launchActivity(Context context, int recurringTaskId) {
        Intent intent = new Intent(context, RecurringTaskDisplayActivity.class);
        intent.putExtra(EXTRA_TASK_ID, recurringTaskId);
        context.startActivity(intent);
    }

    private boolean deleteClickedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recurring_task_display);
        //set up appbar
        Toolbar appbar = findViewById(R.id.appbar);
        appbar.inflateMenu(R.menu.menu_task_display);
        //'up' button
        appbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecurringTaskDisplayActivity.this.finish();
            }
        });
        //get the task to display
        int recurringTaskId = getIntent().getIntExtra(EXTRA_TASK_ID, 0);
        task = DatabaseHolder.getDatabase(getApplicationContext()).getRecurringTaskDao().selectRecurringTaskById(recurringTaskId);
        //display it
        displayRecurringTask(task);
        //set appbar title
        appbar.setTitle(task.title);
    }

    private void displayRecurringTask(@NonNull RecurringTask task) {
        //title
        TextView titleView = findViewById(R.id.title);
        titleView.setText(task.title);

        //body
        TextView bodyView = findViewById(R.id.body);
        bodyView.setText(task.body);

        //date
        TextView dateView = findViewById(R.id.date);
        if (task.date == null) {
            dateView.setText(R.string.date_not_set);
        } else {
            dateView.setText(task.date.toString());
        }

        //recurring days
        ViewGroup days = findViewById(R.id.days);
        days.setVisibility(View.VISIBLE);
        List<Boolean> onDay = task.onDay;
        for (int i = 0; i < 7; i++) {
            ((CheckBox) days.getChildAt(i)).setChecked(onDay.get(i));
        }
    }

    public void onEditButtonClick(MenuItem item) {
        TaskEditActivity.launchActivity(this, TaskDataProvider.TASK_TYPE_RECURRING_TASK, task.id);
        finish();
    }

    public void onDeleteButtonClick(View view) {
        //delete Task when the button is clicked twice
        if (deleteClickedOnce) {
            DatabaseHolder.getDatabase(getApplicationContext()).getRecurringTaskDao().deleteRecurringTask(task);
            finish();
        } else {
            view.setBackgroundColor(getColor(R.color.colorPrimaryDark));
            ((TextView) view).setTextColor(getColor(R.color.colorMenu));
            ((TextView) view).setCompoundDrawableTintList(getColorStateList(R.color.colorMenu));
            deleteClickedOnce = true;
        }
    }

    public void onCompletedButtonClick(View view) {
        //todo
    }

}