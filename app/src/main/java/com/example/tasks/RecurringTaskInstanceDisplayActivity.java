package com.example.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RecurringTaskInstanceDisplayActivity extends AppCompatActivity {

    static final String EXTRA_TASK_ID = "task_id";

    static void launchActivity(Context context, int id) {
        Intent intent = new Intent(context, RecurringTaskInstanceDisplayActivity.class);
        intent.putExtra(EXTRA_TASK_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_display);
    }
}