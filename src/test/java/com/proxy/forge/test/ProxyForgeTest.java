package com.proxy.forge.test;

import com.proxy.forge.dto.User;
import com.proxy.forge.service.UserSerivce;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * <p>ProjectName: proxy-forge</p>
 * <p>PackageName: com.proxy.forge.test</p>
 * <p>Description: test class</p>
 * <p>Copyright: Copyright (c) 2023 by Ts</p>
 * <p>Contacts: Ts vx: Q_Q-1992</p>
 *
 * @Author: Ts
 * @Version: 1.0
 * @Date: 2025-11-19 16:35
 **/

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProxyForgeTest {

    @Autowired
    UserSerivce userSerivce;


    @Test
    public void test() {
        System.out.println("test");
    }


    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setId(1);
        user.setUserName("root");
        user.setPassWord("456");
        System.out.println(userSerivce.updateUserById(user));
    }
}
