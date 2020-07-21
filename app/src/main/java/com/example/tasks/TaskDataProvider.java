package com.example.tasks;


public interface TaskDataProvider {


    Integer TASK_TYPE_TASK = 0;
    Integer TASK_TYPE_RECURRING_TASK = 1;

    Integer getId();

    String getTitle();

    Date getDate();

    Integer getTaskType();
}
