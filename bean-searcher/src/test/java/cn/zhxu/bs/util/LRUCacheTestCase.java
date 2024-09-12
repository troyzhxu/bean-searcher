package cn.zhxu.bs.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class LRUCacheTestCase {

    @Test
    public void test_01() throws InterruptedException {
        Random random = new Random();
        Cache<String> lruCache = new LRUCache<>(50);
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
                        Assertions.assertEquals(key, value);
                    }
                }
                latch.countDown();
            }).start();
        }
        latch.await();
        long t = System.currentTimeMillis() - t0;
        System.out.println("次数：" + putCount);
        System.out.println("耗时：" + t);
        System.out.println("\ttest_01 ok!");
    }

    @Test
    public void test_02() {
        Cache<String> cache = new LRUCache<>(3);
        cache.cache("a", "a");
        cache.cache("b", "b");
        cache.cache("c", "c");
        Assertions.assertEquals("c", cache.get("c"));
        Assertions.assertEquals("a", cache.get("a"));
        Assertions.assertEquals("b", cache.get("b"));
        cache.cache("d", "d");
        Assertions.assertNull(cache.get("c"));
        Assertions.assertEquals("b", cache.get("b"));
        Assertions.assertEquals("d", cache.get("d"));
        Assertions.assertEquals("a", cache.get("a"));
        cache.cache("e", "e");
        Assertions.assertNull(cache.get("c"));
        Assertions.assertNull(cache.get("b"));
        Assertions.assertEquals("a", cache.get("a"));
        Assertions.assertEquals("d", cache.get("d"));
        Assertions.assertEquals("e", cache.get("e"));
    }

}
