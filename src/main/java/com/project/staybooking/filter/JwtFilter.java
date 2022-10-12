package com.project.staybooking.filter;

import com.project.staybooking.repository.AuthorityRepository;
import com.project.staybooking.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.staybooking.model.Authority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// 调用 解析token
// filter：verify token是否有效，如果有效可以通过token知道你是谁
// 先经过filter，再进入controller
// 存在在tom cat之后，controller之前。被SecurityConfig调用
@Component
// 没写filter的order，因为在security config里提到过jwt filter在username和pw之前（相对顺序，而不是绝对顺序）
public class JwtFilter extends OncePerRequestFilter { // 只有第一次运行时执行操作
    //检查request送来的token是否含有需要的两部分，如果没有，filter不起作用
    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer ";
    private AuthorityRepository authorityRepository;
    private JwtUtil jwtUtil;

    // 和AuthorityRepository里定义过的权限链接起来？？？
    @Autowired
    public JwtFilter(AuthorityRepository authorityRepository, JwtUtil jwtUtil) {
        this.authorityRepository = authorityRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    // 解析token， 从token里获得username， pw， authority。
    // 然后这些user的信息保存在security context holder里，和controller联动，通过principal 调用
    // 找http请求是否有header，如果有是否以bear开头，然后找对应的token。
    // 如果存在，并且验证通过，就看是否第一次验证。如果第一次，我做一次验证。如果已经验证过，就不需要再去做
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = httpServletRequest.getHeader(HEADER);

        String jwt = null; //jwt是host或者guest返回的token
        if (authorizationHeader != null && authorizationHeader.startsWith(PREFIX)) {
            jwt = authorizationHeader.substring(PREFIX.length());
        }

        //解析 token
        //如果authentication内容不存在，说明第一次访问，creat信息（user name和权限）保留在security context里。
        //如果已经存在，啥也不干
        if (jwt != null && jwtUtil.validateToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = jwtUtil.extractUsername(jwt);
            Authority authority = authorityRepository.findById(username).orElse(null);
            if (authority != null) { //wrap authority成list，因为构造函数需要一个list
                List<GrantedAuthority> grantedAuthorities = Arrays.asList(new GrantedAuthority[]{new SimpleGrantedAuthority(authority.getAuthority())});

                //generate token （第二种format）
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, grantedAuthorities);

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        // 不是通过了jwt filter之后就万事大吉了。可能需要很多层filter过滤
        // 如果当前filter通过了，就doFilter调用下一层filter。
    }
}

