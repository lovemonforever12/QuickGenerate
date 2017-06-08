package com.ucg.util.timer;

public class TaskWrapper
{

    public TaskWrapper(int priority, Runnable task)
    {
        this.priority = priority;
        this.task = task;
    }

    public Runnable getTask()
    {
        return task;
    }

    public void setTask(Runnable task)
    {
        this.task = task;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    private Runnable task;
    private int priority;
}
