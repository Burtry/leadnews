package com.heima.utils.thread;

import com.heima.model.pojo.user.ApUser;

public class AppThreadLocalUtil {

    private final static ThreadLocal<ApUser> AP_USER_THREAD_LOCAL = new ThreadLocal<>();

    //存入线程
    public static void setUser(ApUser apUser) {
        AP_USER_THREAD_LOCAL.set(apUser);
    }

    //获取
    public static ApUser getUser() {
        return AP_USER_THREAD_LOCAL.get();
    }

    //清理
    public static void clear() {
        AP_USER_THREAD_LOCAL.remove();
    }
}
