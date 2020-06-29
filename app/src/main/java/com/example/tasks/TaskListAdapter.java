package com.example.tasks;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

class TaskListAdapter extends ListAdapter<Task,TaskCardViewHolder> {
    TaskListAdapter(@NonNull DiffUtil.ItemCallback<Task> diffCallback) {
        super(diffCallback);
    }

    private TaskCardViewHolderFactory viewHolderFactory;

    static TaskListAdapter getInstance(TaskCardViewHolderFactory factory) {
        TaskListAdapter adapter=new TaskListAdapter(new DiffUtil.ItemCallback<Task>() {
            @Override
            public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return oldItem.id==newItem.id;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                //compares title and date of the tasks for equality
                return oldItem.title.equals(newItem.title) && Date.isEqual(oldItem.date,newItem.date);
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
        Task task=getCurrentList().get(position);
        holder.bindTo(task);
    }


}
