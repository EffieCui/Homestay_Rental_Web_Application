package com.project.staybooking.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


// 用来处理cross origin的请求，决定是否支持前端跨域访问（前后端跨域。前后端deploy在不同端口/机器）
// filter链条的第一层

@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // filter的第一层。如果不通过就不用看jwt的 token了
                                    // 必须写，不然这个filter不会被检测到
public class CorsFilter extends OncePerRequestFilter { // 每个请求都要经过这个filter的筛选

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        // parameter：response - 返回是否支持，为什么不支持
        // filter chain： 可能需要很多层filter过滤。需要filter责任链的逻辑

        // 后端给前端返回三个header，来表示后端是否支持跨域访问 （填空）
        // 看支持的是否包括发送请求的人。如果谁发请求都回绝，s1写""空；如果只支持特定网址，就写"www.xxx,com"; 如果谁都通过，就写"*"
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        // 如果出现跨域访问，能提供哪些http method
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        // 如果出现跨域访问，它的header只能包括以下key value pairs
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
                                                    // Authorization：允许前端在请求里加入authorization
                                                    // Content-Type：app json/ app form

        // if else： 看发过来的http请求的method是不是options
        if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
            // options： 前端http特殊的请求。（post or get or delete）
            // 后端先接到options请求，返回固定的是否支持跨域访问 cross origins
            // 如果支持，后续就跟上get post etc
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            //返回ok，不往下走。不进入下个filter，也不进入controller，直接返回前端
            // 对于options，只要三个header支持就够了
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            // 往下走，进入controller，处理这些method
        }
    }
}
