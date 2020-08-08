package com.example.tasks;

import java.util.ArrayList;
import java.util.List;

public class RecurringTaskInstance implements TaskDataProvider {

    static final List<RecurringTaskInstance> instanceList = new ArrayList<>();
    static private int nextIndex = 0;

    private final RecurringTask recurringTaskClass;
    private final Date date;
    private final int id;

    RecurringTaskInstance(RecurringTask recurringTaskClass, Date date) {
        this.recurringTaskClass = recurringTaskClass;
        this.date = date;
        this.id = nextIndex;
        nextIndex += 1;
        instanceList.add(this);
    }

    boolean isCompleted() {
        return recurringTaskClass.completedDates.contains(this.date);
    }

    void setCompleted(AppDataBase dataBase, boolean state) {
        if (state/*==true*/) {
            recurringTaskClass.completedDates.add(this.date);
        } else {
            recurringTaskClass.completedDates.remove(this.date);
        }
        dataBase.getRecurringTaskDao().updateRecurringTask(recurringTaskClass);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return recurringTaskClass.title;
    }

    String getBody() {
        return recurringTaskClass.body;
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
