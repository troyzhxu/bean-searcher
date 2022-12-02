package cn.zhxu.bs.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class LRUCacheTestCase {

    Cache<String> lruCache = new LRUCache<>(50);

    Random random = new Random();

    @Test
    public void test() throws InterruptedException {
        AtomicInteger putCount = new AtomicInteger(0);
        int threadCount = 50;
        CountDownLatch latch = new CountDownLatch(threadCount);
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    String key = String.valueOf(random.nextInt(50));
                    String value = lruCache.get(key);
                    if (value == null) {
                        lruCache.cache(key, key);
                        putCount.incrementAndGet();
                    } else {
                        Assert.assertEquals(key, value);
                    }
                }
                latch.countDown();
            }).start();
        }
        latch.await();
        long t = System.currentTimeMillis() - t0;
        System.out.println("次数：" + putCount);
        System.out.println("耗时：" + t);
    }

}
