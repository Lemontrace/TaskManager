package com.example.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TaskDisplayActivity extends AppCompatActivity {

    static final String EXTRA_TASK_ID = "task_id";
    private Task task;

    static void launchActivity(Context context, int taskId) {
        Intent intent = new Intent(context, TaskDisplayActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        context.startActivity(intent);
    }

    private void displayTask(Task task) {
        TextView titleView = findViewById(R.id.title);
        titleView.setText(task.title);

        TextView bodyView = findViewById(R.id.body);
        bodyView.setText(task.body);

        TextView dateView = findViewById(R.id.date);
        dateView.setText(Date.getDateStringInContext(this, task.date));
    }

    public void onEditButtonClick(MenuItem item){
        TaskEditActivity.launchActivity(this, TaskDataProvider.TASK_TYPE_TASK, task.id);
        finish();
    }

    private boolean deleteClickedOnce=false;
    public void onDeleteButtonClick(View view){
        //delete Task when the button is clicked twice
        if (deleteClickedOnce) {
            DatabaseHolder.getDatabase(getApplicationContext()).getTaskDao().deleteTask(task);
            finish();
        } else {
            view.setBackgroundColor(getColor(R.color.colorPrimaryDark));
            ((TextView)view).setTextColor(getColor(R.color.colorMenu));
            ((TextView)view).setCompoundDrawableTintList(getColorStateList(R.color.colorMenu));
            deleteClickedOnce=true;
        }
    }

    public void onCompletedButtonClick(View view) {
        //mark task as completed
        task.completed = !task.completed;
        DatabaseHolder.getDatabase(getApplicationContext()).getTaskDao().updateTask(task);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_display);
        //set up appbar
        Toolbar appbar = findViewById(R.id.appbar);
        appbar.inflateMenu(R.menu.menu_task_display);
        //'up' button
        appbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskDisplayActivity.this.finish();
            }
        });
        //get the task to display
        int taskId = getIntent().getIntExtra(EXTRA_TASK_ID, 0);
        task = DatabaseHolder.getDatabase(getApplicationContext()).getTaskDao().selectTaskById(taskId);
        //display it
        displayTask(task);
        //set appbar title
        appbar.setTitle(task.title);


        //mark as complete/incomplete button
        TextView button = findViewById(R.id.confirm);
        if (task.completed) {
            button.setText(R.string.task_mark_as_incomplete);
            button.setTextColor(getColor(R.color.colorCompletedTask));
            button.setCompoundDrawableTintList(getColorStateList(R.color.colorCompletedTask));
            Toast.makeText(this, R.string.toast_task_deleted, Toast.LENGTH_SHORT).show();
        } else {
            button.setText(R.string.task_mark_as_complete);
        }


    }
}
