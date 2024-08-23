package com.heima.xxljob.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HelloJob {


    @Value("${server.port}")
    private String port;
    @XxlJob("demoJobHandler")
    public void hello() {

        System.out.println(port);
        System.out.println("hello");
    }

    @XxlJob("shardingJobHandler")
    public void sharding() {
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        List<Integer> getlist = getlist();

        for (Integer i : getlist) {

            if (i % shardTotal == shardIndex) {
                System.out.println("分片" + shardIndex + "执行,任务为" + i);
            }
        }

    }


    public List<Integer> getlist() {
        List<Integer> integerList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            integerList.add(i);
        }

        return integerList;
    }
}
