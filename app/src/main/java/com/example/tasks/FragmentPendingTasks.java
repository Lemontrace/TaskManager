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
import java.util.function.Consumer;
import java.util.function.Predicate;


public class FragmentPendingTasks extends TaskListFragment {

    private View noDateView;
    private View overDueView;
    private View todayView;
    private View restView;

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
        Toolbar appbar = requireActivity().findViewById(R.id.appbar);
        appbar.setTitle(R.string.main_bot_nav_pending);

        noDateView = requireActivity().findViewById(R.id.include_no_date);
        overDueView = requireActivity().findViewById(R.id.include_overdue);
        todayView = requireActivity().findViewById(R.id.include_today);
        restView = requireActivity().findViewById(R.id.include_rest);

        updateTaskList();

    }

    @Override
    void updateTaskList() {
        //get dao
        TaskDao dao = DatabaseHolder.getDatabase(requireActivity().getApplicationContext()).getTaskDao();
        RecurringTaskDao recurringTaskDao = DatabaseHolder.getDatabase(requireActivity().getApplicationContext()).getRecurringTaskDao();

        //get overdue tasks, tasks due today, tasks with no date set (incomplete ones)
        Predicate<Task> isTaskCompleted = new Predicate<Task>() {
            @Override
            public boolean test(Task task) {
                return task.completed;
            }
        };
        final Date today = Date.getToday();

        List<Task> _overDueTasks = dao.selectTaskBeforeDate(today);
        _overDueTasks.removeIf(isTaskCompleted);
        _overDueTasks.sort(new Task.DateComparator(true));
        final List<TaskDataProvider> overDueTasks = new ArrayList<TaskDataProvider>(_overDueTasks);

        List<Task> _todayTasks = dao.selectTaskAtDate(today);
        _todayTasks.removeIf(isTaskCompleted);
        final List<TaskDataProvider> todayTasks = new ArrayList<TaskDataProvider>(_todayTasks);

        List<Task> _rest = dao.selectTaskAfterDate(today);
        _rest.removeIf(isTaskCompleted);
        final List<TaskDataProvider> rest = new ArrayList<TaskDataProvider>(_rest);

        List<Task> _noDateTasks = dao.selectTaskWithoutDate();
        _noDateTasks.removeIf(isTaskCompleted);
        List<TaskDataProvider> noDateTasks = new ArrayList<TaskDataProvider>(_noDateTasks);


        List<RecurringTask> recurringTaskList = recurringTaskDao.selectAll();

        //get recurringTaskInstances for each recurringTask and put each of them in the right category
        recurringTaskList.forEach(new Consumer<RecurringTask>() {
            @Override
            public void accept(RecurringTask recurringTask) {
                recurringTask.getActiveInstances().forEach(new Consumer<RecurringTaskInstance>() {
                    @Override
                    public void accept(RecurringTaskInstance taskInstance) {
                        if (taskInstance.getDate().compareTo(today) < 0) {
                            overDueTasks.add(taskInstance);
                        } else if (taskInstance.getDate().compareTo(today) == 0) {
                            todayTasks.add(taskInstance);
                        } else {
                            rest.add(taskInstance);
                        }
                    }
                });
            }
        });


        //determine which views should be shown
        //also, set up recyclerViews and their adapters
        if (noDateTasks.isEmpty()) {
            //hide overdue task view
            noDateView.setVisibility(View.GONE);
        } else {
            //show overdue task view
            noDateView.setVisibility(View.VISIBLE);
            //set up recyclerView
            RecyclerView taskViewNoDate = requireActivity().findViewById(R.id.task_view_no_date);
            taskViewNoDate.setLayoutManager(new LinearLayoutManager(getContext()));
            //get viewHolder factory
            //default colors
            TaskCardViewHolderFactory factory =
                    new TaskCardViewHolderExactDate.TaskCardViewHolderExactDateFactory
                            (null, getResources().getColor(R.color.colorTaskNoDate, null));
            //get adapter that uses the factory
            TaskListAdapter adapterNoDate = TaskListAdapter.getInstance(factory);
            //set adapter
            taskViewNoDate.setAdapter(adapterNoDate);
            //update tasks
            adapterNoDate.submitList(noDateTasks);
        }

        if (overDueTasks.isEmpty()) {
            //hide overdue task view
            overDueView.setVisibility(View.GONE);
        } else {
            //show overdue task view
            overDueView.setVisibility(View.VISIBLE);
            //set up recyclerView
            RecyclerView taskViewOverDue = requireActivity().findViewById(R.id.task_view_overdue);
            taskViewOverDue.setLayoutManager(new LinearLayoutManager(getContext()));
            //get viewHolder factory
            //dark color for date
            TaskCardViewHolderFactory factory =
                    new TaskCardViewHolderExactDate.TaskCardViewHolderExactDateFactory
                            (null, getResources().getColor(R.color.colorPrimaryDark, null));
            //get adapter that uses the factory
            TaskListAdapter adapterOverDue = TaskListAdapter.getInstance(factory);
            //set adapter
            taskViewOverDue.setAdapter(adapterOverDue);
            //update tasks
            overDueTasks.sort(new TaskDataProvider.DateComparator(true));
            adapterOverDue.submitList(overDueTasks);
        }

        if (todayTasks.isEmpty()) {
            //hide today task view
            todayView.setVisibility(View.GONE);
        } else {
            //show today task view
            todayView.setVisibility(View.VISIBLE);
            //set up recyclerView
            RecyclerView taskViewToday = requireActivity().findViewById(R.id.task_view_today);
            taskViewToday.setLayoutManager(new LinearLayoutManager(getContext()));
            //get viewHolder factory
            //accent color for date

            TaskCardViewHolderFactory factory =
                    new TaskCardViewHolderDateToday.TaskCardViewHolderDateTodayFactory
                            (null, getResources().getColor(R.color.colorAccent, null));
            //get adapter that uses the factory
            TaskListAdapter adapterToday = TaskListAdapter.getInstance(factory);
            //set adapter
            taskViewToday.setAdapter(adapterToday);
            //update tasks
            adapterToday.submitList(todayTasks);
        }

        if (rest.isEmpty()) {
            //hide overdue task view
            restView.setVisibility(View.GONE);
        } else {
            //show overdue task view
            restView.setVisibility(View.VISIBLE);
            //set up recyclerView
            RecyclerView taskViewRest = requireActivity().findViewById(R.id.task_view_rest);
            taskViewRest.setLayoutManager(new LinearLayoutManager(getContext()));
            //get viewHolder factory
            //dark color for date
            TaskCardViewHolderFactory factory =
                    new TaskCardViewHolderExactDate.TaskCardViewHolderExactDateFactory
                            (null, null);
            //get adapter that uses the factory
            TaskListAdapter adapterRest = TaskListAdapter.getInstance(factory);
            //set adapter
            taskViewRest.setAdapter(adapterRest);
            //update tasks
            rest.sort(new TaskDataProvider.DateComparator(true));
            adapterRest.submitList(rest);
        }
    }
}

