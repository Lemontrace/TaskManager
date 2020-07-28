package com.example.tasks;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

public class TaskEditActivity extends FragmentActivity {

    static final String EXTRA_TASK_ID = "task_id";

    static void launchActivity(Context context, int taskId) {
        Intent intent = new Intent(context, TaskEditActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        context.startActivity(intent);
    }

    Task task;
    int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);
        //set up appbar
        Toolbar appbar = findViewById(R.id.appbar_memo_edit);
        //'up' button
        appbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskEditActivity.this.finish();
            }
        });
        taskId = getIntent().getIntExtra(EXTRA_TASK_ID, 0);
        task = DatabaseSingleton.getInstance(getApplicationContext()).dataBase.getTaskDao().selectTaskById(taskId);
        displayTask(task);
    }

    private void displayTask(Task task){
        EditText titleView=findViewById(R.id.title);
        titleView.setText(task.title);
        EditText bodyView=findViewById(R.id.body);
        bodyView.setText(task.body);
        TextView dateView=findViewById(R.id.date);
        dateView.setText(task.date.toString());
        updateDateSetButton();
    }

    public void onDateSetButtonClick(View view) {
        if (task.date==null) {
            //date is not set : setting
            DatePickerFragment fragment=new DatePickerFragment();
            fragment.show(getSupportFragmentManager(),"Pick a date");
        } else {
            //date is set : unsetting
            task.date=null;
            TextView dateView=findViewById(R.id.date);
            dateView.setText(task.date.toString());
        }
        updateDateSetButton();
    }

    void updateDateSetButton() {
        Button setDateButton=findViewById(R.id.set_date);
        //if date it set, 'set' button will unset the date
        if (task.date!=null) {
            setDateButton.setText(R.string.edit_unset_date);
        } else {
            setDateButton.setText(R.string.edit_set_date);
        }
    }

    public void onConfirmButtonClick(View view) {
        //set memo attributes
        EditText titleView = findViewById(R.id.title);
        task.title = titleView.getText().toString();//set title
        EditText bodyView = findViewById(R.id.body);
        task.body = bodyView.getText().toString();//set body
        //"date" is set with listener, so we don't need to worry about that

        DatabaseSingleton.getInstance(getApplicationContext()).dataBase.getTaskDao().updateTask(task);

        finish();
    }

    public static class DatePickerFragment extends DialogFragment
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
            Date date = new Date(year, month, day);
            TaskEditActivity activity = ((TaskEditActivity) requireActivity());
            Task task = activity.task;
            task.date = date;
            TextView dateView = requireActivity().findViewById(R.id.date);
            dateView.setText(task.date.toString());
            activity.updateDateSetButton();
        }
    }

}
