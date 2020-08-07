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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

public class TaskEditActivity extends FragmentActivity {

    static final String EXTRA_TASK_TYPE = "task_type";
    static final String EXTRA_TASK_ID = "task_id";
    Date date;

    int taskId;
    int originalTaskType;

    static void launchActivity(Context context, int taskType, int taskId) {
        Intent intent = new Intent(context, TaskEditActivity.class);
        intent.putExtra(EXTRA_TASK_TYPE, taskType);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        //set up appbar
        Toolbar appbar = findViewById(R.id.appbar);
        //'up' button
        appbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskEditActivity.this.finish();
            }
        });
        Intent intent = getIntent();
        originalTaskType = intent.getIntExtra(EXTRA_TASK_TYPE, TaskDataProvider.TASK_TYPE_TASK);
        taskId = intent.getIntExtra(EXTRA_TASK_ID, 0);

        if (originalTaskType == TaskDataProvider.TASK_TYPE_TASK) {
            Task task = DatabaseHolder.getDatabase(getApplicationContext()).getTaskDao().selectTaskById(taskId);
            date = task.date;
            displayTask(task);
        } else if (originalTaskType == TaskDataProvider.TASK_TYPE_RECURRING_TASK) {
            RecurringTask recurringTask = DatabaseHolder.getDatabase(getApplicationContext()).getRecurringTaskDao().selectRecurringTaskById(taskId);
            date = recurringTask.date;
            displayRecurringTask(recurringTask);
        }

    }

    private void displayTask(Task task) {
        //title
        EditText titleView = findViewById(R.id.title);
        titleView.setText(task.title);

        //body
        EditText bodyView = findViewById(R.id.body);
        bodyView.setText(task.body);

        //date
        TextView dateView = findViewById(R.id.date);
        dateView.setText(Date.getDateStringInContext(this, task.date));

        updateDateSetButton();
    }

    private void displayRecurringTask(RecurringTask task) {
        //title
        TextView titleView = findViewById(R.id.title);
        titleView.setText(task.title);

        //body
        TextView bodyView = findViewById(R.id.body);
        bodyView.setText(task.body);

        //date
        TextView dateView = findViewById(R.id.date);
        dateView.setText(Date.getDateStringInContext(this, task.date));
        updateDateSetButton();

        //check "recurring" checkBox
        CheckBox checkBox = findViewById(R.id.checkBoxRecurringTask);
        checkBox.setChecked(true);

        //recurring days
        ViewGroup days = findViewById(R.id.days);
        days.setVisibility(View.VISIBLE);
        List<Boolean> onDay = task.onDay;
        for (int i = 0; i < 7; i++) {
            ((CheckBox) days.getChildAt(i)).setChecked(onDay.get(i));
        }

    }

    public void onDateSetButtonClick(View view) {
        if (date == null) {
            //date is not set : setting
            DatePickerFragment fragment = new DatePickerFragment();
            fragment.show(getSupportFragmentManager(), "Pick a date");
        } else {
            //date is set : un-setting
            date = null;
            TextView dateView = findViewById(R.id.date);
            dateView.setText(Date.getDateStringInContext(this, date));
        }
        updateDateSetButton();
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

    public void onConfirmButtonClick(View view) {

        CheckBox checkBox = findViewById(R.id.checkBoxRecurringTask);
        boolean isRecurring = checkBox.isChecked();

        AppDataBase dataBase = DatabaseHolder.getDatabase(getApplicationContext());
        if (isRecurring) {
            if (date == null) {
                Toast.makeText(this, R.string.edit_starting_date_required, Toast.LENGTH_SHORT).show();
                return;
            }
            RecurringTask recurringTask = new RecurringTask();
            //set task attributes
            recurringTask.id = taskId;
            EditText titleView = findViewById(R.id.title);
            recurringTask.title = titleView.getText().toString();//set title
            EditText bodyView = findViewById(R.id.body);
            recurringTask.body = bodyView.getText().toString();//set body
            recurringTask.date = date; //set date


            ViewGroup days = findViewById(R.id.days);
            List<Boolean> onDay = new ArrayList<>();

            //check checked state and save it to onDay
            for (int i = 0; i < 7; i++) {
                onDay.add(((CheckBox) days.getChildAt(i)).isChecked());
            }
            recurringTask.onDay = onDay;
            if (originalTaskType == TaskDataProvider.TASK_TYPE_TASK) {
                //delete original task
                dataBase.getTaskDao().deleteTask(dataBase.getTaskDao().selectTaskById(taskId));
                //add the recurring task
                dataBase.getRecurringTaskDao().insertRecurringTask(recurringTask);
            } else {
                dataBase.getRecurringTaskDao().updateRecurringTask(recurringTask);
            }

        } else {
            Task task = new Task();
            //set task attributes
            task.id = taskId;
            EditText titleView = findViewById(R.id.title);
            task.title = titleView.getText().toString();//set title
            EditText bodyView = findViewById(R.id.body);
            task.body = bodyView.getText().toString();//set body
            task.date = date; //set date

            if (originalTaskType == TaskDataProvider.TASK_TYPE_RECURRING_TASK) {
                //delete original task
                dataBase.getRecurringTaskDao().deleteRecurringTask(dataBase.getRecurringTaskDao().selectRecurringTaskById(taskId));
                //add the recurring task
                dataBase.getTaskDao().insertTask(task);
            } else {
                //update task
                dataBase.getTaskDao().updateTask(task);
            }
        }

        //finish activity
        finish();
    }

    static public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //Use memo's date as default. If it's not set use current date.
            Date date = Date.getToday();

            int year = date.year;
            int month = date.month;
            int day = date.day;

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(requireActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            //get activity
            TaskEditActivity activity = ((TaskEditActivity) requireActivity());
            //set date
            activity.date = new Date(year, month, day);
            //update views
            TextView dateView = requireActivity().findViewById(R.id.date);
            dateView.setText(Date.getDateStringInContext(requireContext(), activity.date));
            activity.updateDateSetButton();
        }
    }

}
