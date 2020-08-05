package com.example.tasks;

public class RecurringTaskInstance implements TaskDataProvider {

    static private int lastId = 0;
    private RecurringTask recurringTaskClass;
    private Date date;
    private int id;

    RecurringTaskInstance(RecurringTask recurringTaskClass, Date date) {
        this.recurringTaskClass = recurringTaskClass;
        this.date = date;
        this.id = ++lastId;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return recurringTaskClass.title;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public Integer getTaskType() {
        return TaskDataProvider.TASK_TYPE_RECURRING_TASK_INSTANCE;
    }
}
