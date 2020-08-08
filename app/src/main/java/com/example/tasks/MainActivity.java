package com.example.tasks;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    static final String PREF_SORTING = "sorting";
    //those values are set so that it matches the index of each mode in string-array resource
    static final int SORTING_DATE_ASC = 0;
    static final int SORTING_DATE_DESC = 1;
    static final int SORTING_TITLE_ASC = 2;
    static final int SORTING_TITLE_DESC = 3;

    static final int REQUEST_CODE_EXPORT_TASKS = 0;
    static final int REQUEST_CODE_IMPORT_TASKS = 1;

    static SharedPreferences appInfo;

    public static class SortingPickerFragment extends DialogFragment implements DialogInterface.OnClickListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setItems(R.array.sort_modes, this);
            builder.setTitle(R.string.menu_main_sort_by);
            return builder.create();
        }

        @Override
        public void onClick(DialogInterface dialog, int sortMode) {
            SharedPreferences.Editor editor = appInfo.edit();
            editor.putInt(PREF_SORTING, sortMode);
            editor.apply();
            ((MainActivity) requireActivity()).currentFragment.updateTaskList();
        }
    }

    public static class ExportImportSelectorFragment extends DialogFragment implements DialogInterface.OnClickListener {


        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setItems(R.array.export_or_import, this);
            builder.setTitle(R.string.menu_main_export_or_import);
            return builder.create();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == 0) {
                //export
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.setType("application/json");
                intent.putExtra(Intent.EXTRA_TITLE, "TaskExport.json");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                requireActivity().startActivityForResult(intent, REQUEST_CODE_EXPORT_TASKS);
            } else {
                //import
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("application/json");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                requireActivity().startActivityForResult(intent, REQUEST_CODE_IMPORT_TASKS);
            }
        }
    }

    BottomNavigationView botNav;
    TaskListFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get appInfo
        appInfo = getPreferences(Context.MODE_PRIVATE);
        //set up appbar
        Toolbar appbar = findViewById(R.id.appbar);
        appbar.inflateMenu(R.menu.menu_main);

        //set up bottomNavigationView
        botNav= findViewById(R.id.bottomNavigationView);
        botNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {

                    case R.id.bot_nav_completed:
                        currentFragment=new FragmentCompletedTasks();
                        break;
                    case R.id.bot_nav_pending:
                        currentFragment=new FragmentPendingTasks();
                        break;
                    case R.id.bot_nav_all:
                        currentFragment=new FragmentAllTasks();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
                transaction.replace(R.id.fragment_container,currentFragment);
                transaction.commit();
                return true;
            }
        });
        botNav.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            //do nothing
            public void onNavigationItemReselected(@NonNull MenuItem item) {
            }
        });


        //shows pending task when entering main
        botNav.setSelectedItemId(R.id.bot_nav_pending);
    }

    public static java.util.Comparator<TaskDataProvider> getTaskComparator() {
        int sortMode = appInfo.getInt(PREF_SORTING, SORTING_DATE_ASC); //get sort mode
        switch (sortMode) {
            case SORTING_DATE_ASC:
                return new Task.DateComparator(true);
            case SORTING_DATE_DESC:
                return new Task.DateComparator(false);
            case SORTING_TITLE_ASC:
                return new Task.TitleComparator(true);
            case SORTING_TITLE_DESC:
                return new Task.TitleComparator(false);
            default:
                throw (new RuntimeException("Unidentified sorting mode"));
        }
    }

    public void onAddButtonClick(View view) {
        TaskAddActivity.launchActivity(this);
    }

    public void onSortByMenuClick(MenuItem item) {
        new SortingPickerFragment().show(getSupportFragmentManager(), "tag");
    }

    public void onDeleteCompletedTasksMenuClick(MenuItem item) {
        DatabaseHolder.getDatabase(getApplicationContext()).getTaskDao().deleteCompletedTasks();
        Toast.makeText(this, R.string.toast_tasks_deleted, Toast.LENGTH_SHORT).show();
        if (botNav.getSelectedItemId() == R.id.bot_nav_completed) {
            currentFragment.updateTaskList();
        }
    }

    public void onExportImportMenuClick(MenuItem item) {
        new ExportImportSelectorFragment().show(getSupportFragmentManager(), "tag");
    }

    private void importMemosFrom(Uri uri) {
        InputStream inputStream;
        try {
            //get inputStream from uri
            inputStream = getContentResolver().openInputStream(uri);
            assert inputStream != null;

            //read all bytes from the stream
            ArrayList<Byte> byteList = new ArrayList<>();
            while (true) {
                int result = inputStream.read();
                if (result == -1) break; //reached end of stream
                byteList.add((byte) result);
            }


            //copy bytes to byte[] type array
            byte[] byteArray = new byte[byteList.size()];
            for (int i = 0; i < byteList.size(); i++) {
                byteArray[i] = byteList.get(i);
            }
            String JSONString = new String(byteArray, StandardCharsets.UTF_8);    //get string from bytes
            JSONObject root = new JSONObject(JSONString);    //get JSON object from string

            //get handles to database
            AppDataBase database = DatabaseHolder.getDatabase(getApplicationContext());
            TaskDao taskDao = database.getTaskDao();
            RecurringTaskDao recurringTaskDao = database.getRecurringTaskDao();

            //save tasks
            JSONArray taskArray = root.getJSONArray("tasks");
            for (int i = 0; i < taskArray.length(); i++) {
                Task task = Task.loadFromJSON(taskArray.getJSONObject(i));
                taskDao.insertTask(task);
            }

            //save recurring tasks
            JSONArray recurringTaskArray = root.getJSONArray("recurringTasks");
            for (int i = 0; i < recurringTaskArray.length(); i++) {
                RecurringTask task = RecurringTask.loadFromJSON(recurringTaskArray.getJSONObject(i));
                recurringTaskDao.insertRecurringTask(task);
            }

            inputStream.close();

        } catch (IOException | JSONException e) {
            Toast.makeText(this, R.string.toast_import_fail, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        Toast.makeText(this, R.string.toast_import_success, Toast.LENGTH_LONG).show();
    }

    //exported JSON format :
    /*
    root
        //each root element is an array of certain task type
        tasks : array of array of Task JSON object

        recurringTasks : array of RecurringTask JSON object

     */
    private void exportMemosTo(Uri uri) {
        try {
            //open fileOutputStream
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
            assert pfd != null;
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());

            JSONObject root = new JSONObject();

            AppDataBase database = DatabaseHolder.getDatabase(getApplicationContext());

            //put all tasks into JSON array
            final JSONArray taskArray = new JSONArray();
            List<Task> tasks = database.getTaskDao().selectAll();
            for (Task task : tasks) {
                taskArray.put(task.toJSON());
            }

            //put all recurring tasks into JSON array
            final JSONArray recurringTaskArray = new JSONArray();
            List<RecurringTask> recurringTasks = database.getRecurringTaskDao().selectAll();
            for (RecurringTask task : recurringTasks) {
                recurringTaskArray.put(task.toJSON());
            }

            //put task arrays to root
            root.put("tasks", taskArray);
            root.put("recurringTasks", recurringTaskArray);

            //write JSON content (with utf-8)
            fileOutputStream.write(root.toString(4).getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
        } catch (IOException | JSONException e) {
            Toast.makeText(this, R.string.toast_export_fail, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        Toast.makeText(this, R.string.toast_export_success, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_EXPORT_TASKS:
                if (resultCode==RESULT_OK) {
                    assert data != null;
                    Uri uri = data.getData();
                    exportMemosTo(uri);
                }
                break;
            case REQUEST_CODE_IMPORT_TASKS:
                if (resultCode==RESULT_OK) {
                    assert data != null;
                    Uri uri = data.getData();
                    importMemosFrom(uri);
                }
                break;
        }

    }

}