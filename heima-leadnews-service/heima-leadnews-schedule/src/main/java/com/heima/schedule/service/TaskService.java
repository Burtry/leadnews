package com.heima.schedule.service;

import com.heima.model.dto.schedule.Task;

public interface TaskService {

    /**
     * 添加延时任务
     * @param task
     * @return
     */
    long addTask(Task task);
}
