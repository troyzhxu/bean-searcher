package com.example;


import com.ejlchina.searcher.BeanSearcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class AppTest {

    @Autowired
    private BeanSearcher beanSearcher;

    @Test
    public void test() {
        System.out.println("beanSearcher = " + beanSearcher);
    }

}
