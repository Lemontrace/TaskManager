package com.example.tasks;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.function.Predicate;


public class FragmentPendingTasks extends Fragment{

    public FragmentPendingTasks() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending_task, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();

        //set appbar title
        Toolbar appbar=getActivity().findViewById(R.id.appbar_main);
        appbar.setTitle(R.string.main_bot_nav_pending);

        //get dao
        TaskDao dao = DatabaseSingleton.getInstance(getContext()).dataBase.getTaskDao();


        Predicate<Task> isTaskCompleted=new Predicate<Task>() {
            @Override
            public boolean test(Task task) {
                return task.completed;
            }
        };


        //get overdue tasks, tasks due today, tasks with no date set (incomplete ones)
        Date today=Date.getToday();

        List<Task> overDueTasks = dao.selectTaskBeforeDate(today);
        overDueTasks.removeIf(isTaskCompleted);
        overDueTasks.sort(new Task.DateComparator(true));

        List<Task> todayTasks = dao.selectTaskAtDate(today);
        todayTasks.removeIf(isTaskCompleted);

        List<Task> noDateTasks = dao.selectTaskWithoutDate();
        noDateTasks.removeIf(isTaskCompleted);


        //set up recyclerviews and their adapters
        View noDateView=getActivity().findViewById(R.id.include_no_date);
        if (noDateTasks.isEmpty()) {
            //hide overdue task view
            noDateView.setVisibility(View.GONE);
        } else {
            //show overdue task view
            noDateView.setVisibility(View.VISIBLE);
            //set up recyclerView
            RecyclerView taskViewNoDate=getActivity().findViewById(R.id.taskview_no_date);
            taskViewNoDate.setLayoutManager(new LinearLayoutManager(getContext()));
            //get viewHolder factory
            //default colors
            TaskCardViewHolderFactory factory=
                    new TaskCardViewHolderExactDate.TaskCardViewHolderExactDateFactory
                            (null,null);
            //get adapter that uses the factory
            TaskListAdapter noDateAdapter= TaskListAdapter.getInstance(factory);
            //set adapter
            taskViewNoDate.setAdapter(noDateAdapter);
            //update tasks
            noDateAdapter.submitList(noDateTasks);
        }

        View overDueView=getActivity().findViewById(R.id.include_overdue);
        if (overDueTasks.isEmpty()) {
            //hide overdue task view
            overDueView.setVisibility(View.GONE);
        } else {
            //show overdue task view
            overDueView.setVisibility(View.VISIBLE);
            //set up recyclerView
            RecyclerView taskViewOverDue=getActivity().findViewById(R.id.taskview_overdue);
            taskViewOverDue.setLayoutManager(new LinearLayoutManager(getContext()));
            //get viewHolder factory
            //dark color for date
            TaskCardViewHolderFactory factory=
                    new TaskCardViewHolderExactDate.TaskCardViewHolderExactDateFactory
                            (null,getResources().getColor(R.color.colorPrimaryDark,null));
            //get adapter that uses the factory
            TaskListAdapter overDueAdapter= TaskListAdapter.getInstance(factory);
            //set adapter
            taskViewOverDue.setAdapter(overDueAdapter);
            //update tasks
            overDueTasks.sort(new Task.DateComparator(true));
            overDueAdapter.submitList(overDueTasks);
        }

        View todayView=getActivity().findViewById(R.id.include_today);
        if (todayTasks.isEmpty()) {
            //hide today task view
            todayView.setVisibility(View.GONE);
        } else {
            //show today task view
            todayView.setVisibility(View.VISIBLE);
            //set up recyclerView
            RecyclerView taskViewToday=getActivity().findViewById(R.id.taskview_today);
            taskViewToday.setLayoutManager(new LinearLayoutManager(getContext()));
            //get viewHolder factory
            //accent color for date
            TaskCardViewHolderFactory factory=
                    new TaskCardViewHolderDateToday.TaskCardViewHolderDateTodayFactory
                            (null,getResources().getColor(R.color.colorAccent,null));
            //get adapter that uses the factory
            TaskListAdapter todayAdapter= TaskListAdapter.getInstance(factory);
            //set adapter
            taskViewToday.setAdapter(todayAdapter);
            //update tasks
            todayAdapter.submitList(todayTasks);
        }


        //display message if there is no pending task
        View noPendingTaskInclude=getActivity().findViewById(R.id.include_no_pending_task);
        if (overDueTasks.isEmpty()&&todayTasks.isEmpty()&&noDateTasks.isEmpty()) {
            noPendingTaskInclude.setVisibility(View.VISIBLE);
        } else {
            noPendingTaskInclude.setVisibility(View.GONE);
        }

    }

}

