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

import java.io.IOException;

public class TaskAddActivity extends FragmentActivity {


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            Date today = Date.getToday();

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, today.year, today.month, today.day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Date date=new Date(year,month,day);
            TaskAddActivity activity=((TaskAddActivity)getActivity());
            Task task=activity.task;
            //set date
            task.date=date;
            //update dateView
            TextView dateView=getActivity().findViewById(R.id.date);
            dateView.setText(task.getDateString());
            activity.updateDateSetButton();
        }
    }

    Task task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);
        //set up appbar
        Toolbar appbar=findViewById(R.id.appbar_memo_add);
        //'up' button
        appbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskAddActivity.this.setResult(RESULT_CANCELED);
                TaskAddActivity.this.finish();
            }
        });
        //task to add
        task=new Task();

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
            dateView.setText(task.getDateString());
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

    public void onConfirmButtonClick(View view) throws IOException {
        //set memo attributes
        EditText titleView= findViewById(R.id.title);
        task.title=titleView.getText().toString();//set title
        EditText bodyView= findViewById(R.id.body);
        task.body=bodyView.getText().toString();//set body
        //"date" is set with listener, so we don't need to worry about that

        //add the memo object
        DatabaseSingleton.getInstance(getApplicationContext()).dataBase.getTaskDao().insertTask(task);

        //finish activity
        finish();
    }

    static void launchActivity(Context context) {
        Intent intent=new Intent(context, TaskAddActivity.class);
        context.startActivity(intent);
    }
}

