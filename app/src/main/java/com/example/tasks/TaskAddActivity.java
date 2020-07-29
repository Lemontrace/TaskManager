package com.example.tasks;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

public class TaskAddActivity extends FragmentActivity {


    public void onDateSetButtonClick(View view) {
        if (date == null) {
            //date is not set : setting
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.show(getSupportFragmentManager(), "Pick a date");
        } else {
            //date is set : unsetting
            date = null;
            TextView dateView = findViewById(R.id.date);
            dateView.setText(Date.getDateString(date));
        }

        updateDateSetButton();
    }

    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);
        //set up appbar
        Toolbar appbar = findViewById(R.id.appbar_memo_add);
        //'up' button
        appbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskAddActivity.this.setResult(RESULT_CANCELED);
                TaskAddActivity.this.finish();
            }
        });
        updateDateSetButton();

        //initially, the task is not recurring
        findViewById(R.id.days).setVisibility(View.GONE);
    }

    void updateDateSetButton() {
        Button setDateButton = findViewById(R.id.set_date);
        //if date it set, 'set' button will unset the date
        if (date != null) {
            setDateButton.setText(R.string.edit_unset_date);
        } else {
            setDateButton.setText(R.string.edit_set_date);
        }
    }

    public void onRecurringTaskCheckBoxClick(View view) {
        ViewGroup days = findViewById(R.id.days);
        TextView date = findViewById(R.id.edit_date);

        CheckBox checkBox = (CheckBox) view;
        if (checkBox.isChecked()) {
            days.setVisibility(View.VISIBLE);
            date.setText(R.string.edit_starting_date);
        } else {
            days.setVisibility(View.GONE);
            date.setText(R.string.edit_date);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            Date today = Date.getToday();

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(requireContext(), this, today.year, today.month, today.day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Date date = new Date(year, month, day);
            ((TaskAddActivity) requireActivity()).date = date;
            //update dateView
            TextView dateView = requireActivity().findViewById(R.id.date);
            dateView.setText(Date.getDateString(date));
            ((TaskAddActivity) requireActivity()).updateDateSetButton();
        }
    }

    public void onConfirmButtonClick(View view) {

        CheckBox checkBox = findViewById(R.id.checkBoxRecurringTask);
        boolean isRecurring = checkBox.isChecked();
        if (isRecurring) {
            RecurringTask recurringTask = new RecurringTask();
            //set task attributes
            EditText titleView = findViewById(R.id.title);
            recurringTask.title = titleView.getText().toString();//set title
            EditText bodyView= findViewById(R.id.body);
            recurringTask.body=bodyView.getText().toString();//set body
            recurringTask.date=date; //set date


            ViewGroup days=findViewById(R.id.days);
            List<Boolean> onDay = new ArrayList<>();

            //check checked state and save it to onDay
            for (int i = 0; i < 7; i++) {
                onDay.add(((CheckBox)days.getChildAt(i)).isChecked());
            }
            recurringTask.onDay=onDay;

            //add the recurring task object
            DatabaseHolder.getDatabase(getApplicationContext()).getRecurringTaskDao().insertRecurringTask(recurringTask);
        } else {
            Task task=new Task();
            //set task attributes
            EditText titleView= findViewById(R.id.title);
            task.title=titleView.getText().toString();//set title
            EditText bodyView= findViewById(R.id.body);
            task.body=bodyView.getText().toString();//set body
            task.date=date; //set date
            // add the task object
            DatabaseHolder.getDatabase(getApplicationContext()).getTaskDao().insertTask(task);
        }

        //finish activity
        finish();
    }

    static void launchActivity(Context context) {
        Intent intent=new Intent(context, TaskAddActivity.class);
        context.startActivity(intent);
    }
}

