package com.orient.firecontrol_web_demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FirecontrolWebDemoApplicationTests {

    @Test
    public void contextLoads() {
        Set<String> strings = new HashSet<>();
        strings.add("1");
        strings.add("1");
        strings.add("1");
        System.out.println(strings);
    }

}
