package com.heima.schedule.service;

import com.heima.model.dto.schedule.Task;

public interface TaskService {

    /**
     * 添加延时任务
     * @param task
     * @return
     */
    long addTask(Task task);


    /**
     * 取消任务
     * @param taskId        任务id
     * @return              取消结果
     */
    boolean cancelTask(long taskId);

    /**
     * 按照类型和优先级来拉取任务
     * @param type
     * @param priority
     * @return
     */
    Task pull(int type,int priority);
}


