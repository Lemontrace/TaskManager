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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends FragmentActivity {

    static final String PREF_SORTING="sorting";
        //those values are set so that it matches the index of each mode in string-array resource
        static final int SORTING_DATE_ASC=0;
        static final int SORTING_DATE_DESC=1;
        static final int SORTING_TITLE_ASC=2;
        static final int SORTING_TITLE_DESC=3;

    static final int REQUEST_CODE_EXPORT_MEMOS=0;
    static final int REQUEST_CODE_IMPORT_MEMOS=1;

    static SharedPreferences appInfo;

    BottomNavigationView botNav;
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get appInfo
        appInfo=getPreferences(Context.MODE_PRIVATE);
        //set up appbar
        Toolbar appbar=findViewById(R.id.appbar_main);
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
            public void onNavigationItemReselected(@NonNull MenuItem item) {}
        });


        //shows pending task when entering main
        botNav.setSelectedItemId(R.id.bot_nav_pending);
    }

    public void onAddButtonClick(View view){
        TaskAddActivity.launchActivity(this);
    }

    public void onSortByMenuClick(MenuItem item) {
        new SortingPickerFragment().show(getSupportFragmentManager(),"tag");
    }

    public void onExportImportMenuClick(MenuItem item) {
        new ExportImportSelectorFragment().show(getSupportFragmentManager(),"tag");
    }

    public void onDeleteCompletedTasksMenuClick(MenuItem item) {
        DatabaseSingleton.getInstance(getApplicationContext()).dataBase.getTaskDao().deleteCompletedTask();
        Toast.makeText(this, R.string.toast_tasks_deleted, Toast.LENGTH_SHORT).show();
        if (botNav.getSelectedItemId()==R.id.bot_nav_completed) {
            ((FragmentCompletedTasks)currentFragment).updateTaskList();
        }
    }

    public static java.util.Comparator<Task> getTaskComparator(){
        int sortMode=appInfo.getInt(PREF_SORTING,SORTING_DATE_ASC); //get sort mode
        switch(sortMode){
            case SORTING_DATE_ASC:
                return new Task.DateComparator(true);
            case SORTING_DATE_DESC:
                return new Task.DateComparator(false);
            case SORTING_TITLE_ASC:
                return new Task.TitleComparator(true);
            case SORTING_TITLE_DESC:
                return new Task.TitleComparator(false);
            default:
                throw(new RuntimeException("Unidentified sorting mode"));
        }
    }

    private void exportMemosTo(Uri uri) {
        try {
            //open fileOutputStream
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
            assert pfd != null;
            FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
            //write each Memo's saveString
            for (Task task : DatabaseSingleton.getInstance(getApplicationContext()).dataBase.getTaskDao().selectAll()) {
                fileOutputStream.write(task.getSaveString().getBytes());
                //indicates end of memo
                fileOutputStream.write(":\n".getBytes());
            }
            //indicates end of archive
            fileOutputStream.write("END:END\n".getBytes());
            pfd.close();
        } catch (IOException e) {
            Toast.makeText(this, R.string.toast_export_fail, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        Toast.makeText(this, R.string.toast_export_success, Toast.LENGTH_LONG).show();
    }

    private void importMemosFrom(Uri uri) {
        InputStream inputStream;
        try {
            ArrayList<Task> tasksToAdd = new ArrayList<>();
            //get inputStream from uri and wrap it
            inputStream = getContentResolver().openInputStream(uri);
            assert inputStream != null;
            BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
            //attribute set
            HashMap<String,String> attributes=new HashMap<>();

            String line;
            while (true) {
                //read line
                line=reader.readLine();
                //get attribute name and value
                String[] pair = line.split(":",2);
                if (pair.length!=2) throw new IOException(); //not a valid archive file
                //empty attribute name indicates end of a memo
                if (pair[0].equals("")) {
                    //create Memo object
                    Task task = Task.loadFromAttributes(attributes);
                    //add it to temp. list
                    tasksToAdd.add(task);
                    //clear hash
                    attributes.clear();
                } else if (pair[0].equals("END")) {
                    //end of archive
                    inputStream.close();
                    break;
                } else {
                    //add attribute to the hash
                    attributes.put(pair[0], Task.unescaped(pair[1]));
                }
            }

            //save tasks
            for (Task task : tasksToAdd) {
                DatabaseSingleton.getInstance(getApplicationContext()).dataBase.getTaskDao().insertTask(task);
            }

            Toast.makeText(this, R.string.toast_import_success, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Toast.makeText(this, R.string.toast_import_fail, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static class SortingPickerFragment extends DialogFragment implements DialogInterface.OnClickListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
            builder.setItems(R.array.sort_modes,this);
            builder.setTitle(R.string.menu_main_sort_by);
            return builder.create();
        }

        @Override
        public void onClick(DialogInterface dialog, int sortMode) {
            SharedPreferences.Editor editor=appInfo.edit();
            editor.putInt(PREF_SORTING,sortMode);
            editor.apply();
        }
    }

    public static class ExportImportSelectorFragment extends DialogFragment implements DialogInterface.OnClickListener {


        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
            builder.setItems(R.array.export_or_import,this);
            builder.setTitle(R.string.menu_main_export_or_import);
            return builder.create();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which==0) {
                //export
                Intent intent=new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE,"MemoExport.txt");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                requireActivity().startActivityForResult(intent,REQUEST_CODE_EXPORT_MEMOS);
            } else {
                //import
                Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("text/plain");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                requireActivity().startActivityForResult(intent,REQUEST_CODE_IMPORT_MEMOS);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_EXPORT_MEMOS:
                if (resultCode==RESULT_OK) {
                    assert data != null;
                    Uri uri = data.getData();
                    exportMemosTo(uri);
                }
                break;
            case REQUEST_CODE_IMPORT_MEMOS:
                if (resultCode==RESULT_OK) {
                    assert data != null;
                    Uri uri = data.getData();
                    importMemosFrom(uri);
                }
                break;
        }

    }

}