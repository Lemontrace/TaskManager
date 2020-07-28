package com.example.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class FragmentCompletedTasks extends Fragment {

    public FragmentCompletedTasks() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_tasks_list, container, false);
    }

    TaskListAdapter adapter;
    @Override
    public void onStart() {
        super.onStart();

        //set appbar title
        Toolbar appbar = requireActivity().findViewById(R.id.appbar_main);
        appbar.setTitle(R.string.main_bot_nav_completed);

        //set up recyclerView
        RecyclerView taskViewCompleted = requireActivity().findViewById(R.id.taskview);
        taskViewCompleted.setLayoutManager(new LinearLayoutManager(getContext()));
        Integer textColor=getResources().getColor(R.color.colorPastMemo,null);
        //get viewHolder factory
        TaskCardViewHolderFactory factory =
                new TaskCardViewHolderExactDate.TaskCardViewHolderExactDateFactory
                        (textColor,textColor);
        //get adapter with the factory
         adapter=TaskListAdapter.getInstance(factory);
        //set adapter
        taskViewCompleted.setAdapter(adapter);
        //update task list
        updateTaskList();
    }

    public void updateTaskList() {
        //get completed tasks and sort them
        List<Task> completedTasks = DatabaseSingleton.getInstance(getContext()).dataBase.getTaskDao().selectByCompletedState(true);
        completedTasks.sort(MainActivity.getTaskComparator());
        //update tasks
        adapter.submitList(new ArrayList<TaskDataProvider>(completedTasks));
    }
}
