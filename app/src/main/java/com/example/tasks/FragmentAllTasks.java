package com.example.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class FragmentAllTasks extends TaskListFragment {

    private TaskListAdapter adapter;

    public FragmentAllTasks() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_task_list, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();

        //set appbar title
        Toolbar appbar = requireActivity().findViewById(R.id.appbar);
        appbar.setTitle(R.string.main_bot_nav_all);


        //set up recyclerView
        RecyclerView taskViewAll = requireActivity().findViewById(R.id.task_view);
        taskViewAll.setLayoutManager(new LinearLayoutManager(getContext()));
        //get viewHolder factory
        TaskCardViewHolderFactory factory =
                new TaskCardViewHolderAutoDate.TaskCardViewHolderAutoDateFactory(null, null,
                        getResources().getColor(R.color.colorPrimaryDark, requireContext().getTheme()),
                        getResources().getColor(R.color.colorAccent, requireContext().getTheme()),
                        getResources().getColor(R.color.colorDateRecurring, requireContext().getTheme()));
        //get adapter with the factory
        adapter = TaskListAdapter.getInstance(factory);
        //set adapter
        taskViewAll.setAdapter(adapter);
    }

    @Override
    void updateTaskList() {
        //get tasks
        List<Task> tasks = DatabaseHolder.getDatabase(requireActivity().getApplicationContext()).getTaskDao().selectAll();
        tasks.removeIf(new Predicate<Task>() {
            @Override
            public boolean test(Task task) {
                return task.completed;
            }
        });

        //get recurring tasks
        List<RecurringTask> recurringTasks = DatabaseHolder.getDatabase(requireActivity().getApplicationContext()).getRecurringTaskDao().selectAll();

        List<TaskDataProvider> taskDataList = new ArrayList<>();
        taskDataList.addAll(tasks);
        taskDataList.addAll(recurringTasks);

        //update tasks
        adapter.submitList(taskDataList);

    }
}
