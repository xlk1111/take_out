package com.xlk.takeout.filter;

import com.alibaba.fastjson.JSON;
import com.xlk.takeout.common.BaseContext;
import com.xlk.takeout.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //    路径匹配器支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        HttpServletResponse response = (HttpServletResponse) servletResponse;


//        A. 获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求: {}", requestURI);
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/user/sendMsg"
        };
//        B. 判断本次请求, 是否需要登录, 才可以访问
        boolean check = check(urls, requestURI);

//        C. 如果不需要，则直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

//        D. 判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，用户id为: {}", request.getSession().getAttribute("employee"));

            long id = Thread.currentThread().getId();
            log.info("线程id为；{}", id);
            //将session中的id set进当前线程中
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户id为: {}", request.getSession().getAttribute("user"));

            long id = Thread.currentThread().getId();
            log.info("线程id为；{}", id);
            //将session中的id set进当前线程中
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }


//        E. 如果未登录, 则返回未登录结果
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }


    /**
     * 路径匹配，检查本次请求是否需要放行
     *
     * @param urls
     * @param requestURL
     * @return
     */
    public boolean check(String[] urls, String requestURL) {

        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURL);
            if (match) {
                return true;
            }
        }

        return false;
    }
}