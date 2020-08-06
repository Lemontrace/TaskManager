package com.example.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RecurringTaskInstanceDisplayActivity extends AppCompatActivity {

    static final String EXTRA_TASK_ID = "task_id";

    static void launchActivity(Context context, int id) {
        Intent intent = new Intent(context, RecurringTaskInstanceDisplayActivity.class);
        intent.putExtra(EXTRA_TASK_ID, id);
        context.startActivity(intent);
    }

    RecurringTaskInstance taskInstance;
    private boolean completedButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_display);
        //set up appbar
        Toolbar appbar = findViewById(R.id.appbar);
        //appbar.inflateMenu(R.menu.menu_task_display);
        //'up' button
        appbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecurringTaskInstanceDisplayActivity.this.finish();
            }
        });
        //get the task to display
        int taskId = getIntent().getIntExtra(EXTRA_TASK_ID, 0);
        taskInstance = RecurringTaskInstance.instanceList.get(taskId);
        //display it
        displayTask(taskInstance);
        //set appbar title
        appbar.setTitle(taskInstance.getTitle());


        //mark as complete/incomplete button
        TextView button = findViewById(R.id.confirm);
        if (taskInstance.isCompleted()) {
            button.setText(R.string.task_mark_as_incomplete);
            button.setTextColor(getColor(R.color.colorCompletedTask));
            button.setCompoundDrawableTintList(getColorStateList(R.color.colorCompletedTask));
        } else {
            button.setText(R.string.task_mark_as_complete);
        }
    }

    public void onCompletedButtonClick(View view) {
        //prevents this method from being invoked multiple times
        if (!completedButtonClicked) {
            completedButtonClicked = true;
        } else {
            return;
        }

        //invert completed state
        taskInstance.setCompleted(DatabaseHolder.getDatabase(getApplicationContext()), !taskInstance.isCompleted());
        //finish activity
        finish();
    }

    private void displayTask(RecurringTaskInstance taskInstance) {
        TextView titleView = findViewById(R.id.title);
        titleView.setText(taskInstance.getTitle());

        TextView bodyView = findViewById(R.id.body);
        bodyView.setText(taskInstance.getBody());

        TextView dateView = findViewById(R.id.date);
        dateView.setText(Date.getDateStringInContext(this, taskInstance.getDate()));
    }


}