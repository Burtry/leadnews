package com.heima.schedule.service.impl;

import com.heima.common.redis.CacheService;
import com.heima.model.dto.schedule.Task;
import com.heima.schedule.ScheduleApplication;
import com.heima.schedule.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Set;

@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class TaskServiceImplTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private CacheService cacheService;

    @Test
    public void addTask() {
        for (int i = 0; i < 5; i++) {
            Task task = new Task();
            task.setTaskType(100 + i);
            task.setParameters("task test".getBytes());
            task.setPriority(50);
            task.setExecuteTime(new Date().getTime() + 500 * i);

            long taskId = taskService.addTask(task);
        }

    }

    @Test
    public void pool() {
        Task task = taskService.pull(100, 50);
        System.out.println(task);

    }

    @Test
    public void testKeys() {
        Set<String> keys = cacheService.scan("future_*");
        System.out.println(keys);
    }
}