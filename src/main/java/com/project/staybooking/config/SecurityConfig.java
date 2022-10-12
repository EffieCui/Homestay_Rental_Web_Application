package com.project.staybooking.config;

import com.project.staybooking.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

// 初始化配置，告诉（spring）framework 怎么处理具体内容
//For security reasons, storing unencrypted passwords directly in the database is not recommended.
//We should do the encryption before saving the data.

@EnableWebSecurity //除了内部的config，spring需要创建和security相关的class
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource; //spring boot 启动时自动生成

    @Autowired
    private JwtFilter jwtFilter;

    @Bean //把下面作为 spring container 里的obj
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //比较两个加密过后的字符串是否一致
    }

    //authorization：检查是否有权限访问，看url是否匹配（eg 只有host可以upload）
    // 经过过滤才能达到controller被调用
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests() // 有以下的权限限制
                .antMatchers(HttpMethod.POST, "/register/*").permitAll() //任何人都可以访问
                .antMatchers(HttpMethod.POST, "/authenticate/*").permitAll()
                .antMatchers("/stays").hasAuthority("ROLE_HOST")
                .antMatchers("/stays/*").hasAuthority("ROLE_HOST")
                .antMatchers("/search").hasAuthority("ROLE_GUEST")
                .antMatchers("/reservations").hasAuthority("ROLE_GUEST")
                .antMatchers("/reservations/*").hasAuthority("ROLE_GUEST")
                .anyRequest().authenticated()
                .and()
                .csrf()
                .disable();

        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 框架never creat http session again
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); //filter先于username和pw运行。通过filter之后就知道了用户名和密码（通过了就不会被第二层拦住）
                                            // spring frame work自带的，对用户名和密码校验
    }

    // 配置： 在启动时 让security frame 连接到db
    // 有登录的request之后，通过这个配置看name和password 是否 match db里注册的user name 和pw
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("SELECT username, password, enabled FROM user WHERE username = ?")
                //"？"： placeholder，真正的值在请求发过来之后被替换。另防止sql injection
                .authoritiesByUsernameQuery("SELECT username, authority FROM authority WHERE username = ?");
    }

    @Override
    @Bean
    // 帮你用username和pw登陆时验证，判断你是谁，告诉你 user exist or not
    // 被authentication service 调用
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();

    }
}

// authentication vs authorization
// authentication： 验证你是谁，token or pw （tott，oauth）
// authorization：知道了你是谁之后，看你有什么权限，功能是否给你使用
// 先 authentication 再 authorization。