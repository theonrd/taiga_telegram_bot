package com.theonrd.tgbot.BotCommands;

import com.theonrd.tgbot.ConfigHelper;

public class TasksStruct {

    private final long lModTime;
    private final String sTaskName, sCatName;

    public TasksStruct(long modTime, String taskName, String catName) {

        this.lModTime = modTime;
        this.sCatName = catName;
        this.sTaskName = taskName;
    }

    public long getModTime() {

        return this.lModTime;
    }

    public String getTaskName() {

        return this.sTaskName;
    }

    public String getCategoryName() {

        return this.sCatName;
    }

    public boolean isActualToQuery() {

        System.out.println(System.currentTimeMillis() - ConfigHelper.update_timer_time + " <= " + this.getModTime());

        return System.currentTimeMillis() - ConfigHelper.update_timer_time <= this.getModTime() + ConfigHelper.timezone_different;
    }
}
