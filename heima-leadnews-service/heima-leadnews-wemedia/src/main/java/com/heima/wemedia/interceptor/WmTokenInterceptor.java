package com.heima.wemedia.interceptor;

import com.heima.model.pojo.wemedia.WmUser;
import com.heima.utils.thread.WmThreadLocalUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WmTokenInterceptor implements HandlerInterceptor {


    /**
     * 前置拦截器，获取用户信息，存储到当前线程中
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("userId");
        if (userId != null) {
            //存入当前线程
            WmUser wmUser = new WmUser();
            wmUser.setId(Integer.valueOf(userId));
            WmThreadLocalUtil.setUser(wmUser);
        }

        return true;

    }

    /**
     * 后置拦截器，清理当前线程
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WmThreadLocalUtil.clear();
    }
}
