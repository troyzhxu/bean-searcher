package cn.zhxu.bs.ex;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 延迟策略
 */
public interface DelayPolicy {

    /**
     * 随机放大的延迟策略
     */
    class RandomInflate implements DelayPolicy {

        /**
         * 放大因子
         */
        private final float inflateFactor;

        public RandomInflate() {
            this(1);
        }

        public RandomInflate(float inflateFactor) {
            this.inflateFactor = inflateFactor;
        }

        @Override
        public int batchDelay(int delayMills, int exportingThreads, int maxExportingThreads) {
            int inflateMills = (int) (delayMills * exportingThreads * inflateFactor / exportingThreads);
            return ThreadLocalRandom.current().nextInt(inflateMills) + delayMills;
        }

    }

    /**
     * 获取没批次查询后的延迟时间
     * @param delayMills 初始延迟时间（毫秒）
     * @param exportingThreads 正在导出的线程数
     * @param maxExportingThreads 正在导出的最大线程数
     * @return 处理后的延迟时间（毫秒）
     */
    int batchDelay(int delayMills, int exportingThreads, int maxExportingThreads);

}
