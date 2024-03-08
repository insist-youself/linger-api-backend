package com.yupi.lingerinterface;

import com.yupi.lingerapiclientsdk.client.LingerApiClient;
import com.yupi.lingerapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class LingerInterfaceApplicationTests {

    @Resource
    private LingerApiClient lingerApiClient;

    @Test
    void contextLoads() {
//        String result = lingerApiClient.getNameByGet("yupi");
//        User user = new User();
//        user.setUsername("linger");
//        String userNameByPost = lingerApiClient.getUserNameByPost(user);
//        System.out.println(result);
//        System.out.println(userNameByPost);
    }

}
