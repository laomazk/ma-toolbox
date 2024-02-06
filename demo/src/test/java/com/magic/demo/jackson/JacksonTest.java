package com.magic.demo.jackson;

import com.magic.core.JacksonUtils;
import com.magic.demo.demos.web.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class JacksonTest {

    @Test
    void writeValueAsStringTest() {
        User user = new User();
        user.setAge(1);
        user.setName("张三");
        user.setGg(true);
        user.setBirth(LocalDateTime.now());
        String s = JacksonUtils.writeValueAsString(user);
        System.out.println("s = " + s);
    }

    @Test
    void readValueTest() {
        String s = "{\"name\":\"张三\",\"age\":1,\"gg\":1,\"birth\":\"1707212467184\"}";
        User user = JacksonUtils.readValue(s, User.class);
        System.out.println("user = " + user);

    }
}
