package com.yupi.project.service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author linger
 * @date 2024/2/16 17:18
 */
@SpringBootTest
public class UserInterfaceInfoServiceTest {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

//    @Test
//    public void invokeCount() {
//        boolean b = userInterfaceInfoService.invokeCount(1L, 1L);
//        // 表示断言b的值为true, 即测试用例期望invokeCount方法返回true
//        Assertions.assertTrue(b);
//    }

    @Test
    void test1() {
        String s1 = "hello";
        try {
            System.out.println(URLEncoder.encode(s1, "utf-8"));
            System.out.println(URLEncoder.encode(s1, "utf-8"));
            System.out.println(URLEncoder.encode(s1, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}