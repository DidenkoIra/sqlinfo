package sql.info.models;

import javax.validation.constraints.NotEmpty;

public class Task {
    @NotEmpty(message = "Title should not be empty")
    private String title;
    private Task parentTask;
    private Integer maxXP;

    public Task() {}

    public Task(String title) {
        this.title = title;
    }

    public Task(String title, Task task, int maxXP) {
        this.title = title;
        this.parentTask = task;
        this.maxXP = maxXP;
    }


    public String getTitle() {
        return title;
    }
    public Task getParentTask() {
        return parentTask;
    }

    public Integer getMaxXP() {
        return maxXP;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    public void setMaxXP(Integer maxXP) {
        this.maxXP = maxXP;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title=" + title +
                ", parentTask=" + parentTask +
                ", maxXP=" + maxXP +
                '}';
    }
}
