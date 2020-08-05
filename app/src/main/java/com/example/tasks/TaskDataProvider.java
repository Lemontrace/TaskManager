package com.example.tasks;


public interface TaskDataProvider {


    Integer TASK_TYPE_TASK = 0;
    Integer TASK_TYPE_RECURRING_TASK = 1;
    Integer TASK_TYPE_RECURRING_TASK_INSTANCE = 2;

    Integer getId();

    String getTitle();

    Date getDate();

    Integer getTaskType();

    class DateComparator implements java.util.Comparator<TaskDataProvider> {

        int factor;

        DateComparator(boolean ascending) {
            if (ascending) {
                factor = +1;
            } else {
                factor = -1;
            }
        }

        // null date(date not set) gets the smallest value, meaning it will always appear first
        @Override
        public int compare(TaskDataProvider m1, TaskDataProvider m2) {
            if (m1.getDate() == null && m2.getDate() == null) {
                return 0;
            } else if (m1.getDate() == null) {
                return -1;
            } else if (m2.getDate() == null) {
                return +1;
            } else {
                return m1.getDate().compareTo(m2.getDate()) * factor;
            }
        }

    }

    class TitleComparator implements java.util.Comparator<TaskDataProvider> {

        int factor;

        TitleComparator(boolean ascending) {
            if (ascending) {
                factor = +1;
            } else {
                factor = -1;
            }
        }

        @Override
        public int compare(TaskDataProvider m1, TaskDataProvider m2) {
            return Integer.signum(m1.getTitle().compareToIgnoreCase(m2.getTitle())) * factor;
        }
    }
}