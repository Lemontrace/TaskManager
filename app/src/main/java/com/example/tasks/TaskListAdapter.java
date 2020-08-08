package com.example.tasks;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

class TaskListAdapter extends ListAdapter<TaskDataProvider, TaskCardViewHolder> {
    TaskListAdapter(@NonNull DiffUtil.ItemCallback<TaskDataProvider> diffCallback) {
        super(diffCallback);
    }

    private TaskCardViewHolderFactory viewHolderFactory;

    static TaskListAdapter getInstance(TaskCardViewHolderFactory factory) {
        TaskListAdapter adapter = new TaskListAdapter(new DiffUtil.ItemCallback<TaskDataProvider>() {
            @Override
            public boolean areItemsTheSame(@NonNull TaskDataProvider oldItem, @NonNull TaskDataProvider newItem) {
                return oldItem.getId().equals(newItem.getId()) && oldItem.getTaskType().equals(newItem.getTaskType());
            }

            @Override
            public boolean areContentsTheSame(@NonNull TaskDataProvider oldItem, @NonNull TaskDataProvider newItem) {
                //compares title and date of the tasks for equality
                return oldItem.getTitle().equals(newItem.getTitle()) && Date.isEqual(oldItem.getDate(), newItem.getDate());
            }
        });

        adapter.viewHolderFactory=factory;
        return adapter;
    }

    @NonNull
    @Override
    public TaskCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return viewHolderFactory.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskCardViewHolder holder, int position) {
        TaskDataProvider taskData = getCurrentList().get(position);
        holder.bindTo(taskData);
    }


}
