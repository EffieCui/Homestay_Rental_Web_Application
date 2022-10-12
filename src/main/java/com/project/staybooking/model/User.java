package com.project.staybooking.model;

// 这些功能是hibernate实现的。基于java的interface
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@Table(name = "user")
@JsonDeserialize(builder = User.Builder.class) //从前端request data： deserializable: json --> obj
public class User implements Serializable { //网络传输使用serializable，优化为网络可以传输的格式
    private static final long serialVersionUID = 1L; //保证从数据库读的和写入数据库的版本是相同的（增删field之后会添加新的版本）
    @Id
    private String username;
    @JsonIgnore // in some services, like stay list and reservation list, we want to show the host information or guest information, but we only want to show the username, not password or enabled.
    private String password;
    @JsonIgnore
    private boolean enabled; //use security framework to check if the user can log in or not (true or false). if true, check user name and password
    // if the user log out， enabled = false.

    public User() {} //orm, hibernate need this+set to construct an object to map data to obj
    //after query from database, hibernate call this

    private User(Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
        this.enabled = builder.enabled;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public User setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public static class Builder {
        @JsonProperty("username")
        private String username;

        @JsonProperty("password")
        private String password;

        @JsonProperty("enabled")
        private boolean enabled;

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
