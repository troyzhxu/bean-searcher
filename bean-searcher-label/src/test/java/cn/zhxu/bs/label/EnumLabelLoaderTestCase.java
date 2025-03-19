package cn.zhxu.bs.label;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class EnumLabelLoaderTestCase {

    public enum PayStatus {

        UNPAID("未支付"),
        PAID("已支付"),
        CANCELED("已取消");

        private final String label;

        PayStatus(String label) {
            this.label = label;
        }
    }

    public enum DeliveryStatus {
        UNDELIVERED("未发货"),
        DELIVERED("已发货"),
        RECEIVED("已收货");

        private final String label;

        DeliveryStatus(String label) {
            this.label = label;
        }
    }

    public static class Order {

        private PayStatus payStatus;
        private DeliveryStatus deliveryStatus;

        @LabelFor("payStatus")
        private String payStatusName;

        @LabelFor("deliveryStatus")
        private String deliveryStatusName;

        public Order(PayStatus payStatus, DeliveryStatus deliveryStatus) {
            this.payStatus = payStatus;
            this.deliveryStatus = deliveryStatus;
        }
    }

    @Test
    public void test_01() {
        EnumLabelLoader loader = new EnumLabelLoader()
                .with(PayStatus.class, s -> s.label)
                .with(DeliveryStatus.class, s -> s.label);

        LabelResultFilter resultFilter = new LabelResultFilter();
        resultFilter.addLabelLoader(loader);

        List<Order> dataList = Arrays.asList(
                new Order(PayStatus.CANCELED, DeliveryStatus.UNDELIVERED),
                new Order(PayStatus.PAID, DeliveryStatus.DELIVERED),
                new Order(PayStatus.PAID, DeliveryStatus.RECEIVED),
                new Order(PayStatus.UNPAID, DeliveryStatus.UNDELIVERED)
        );

        resultFilter.processDataList(Order.class, dataList);

        Assertions.assertEquals(PayStatus.CANCELED.label, dataList.get(0).payStatusName);
        Assertions.assertEquals(PayStatus.PAID.label, dataList.get(1).payStatusName);
        Assertions.assertEquals(PayStatus.PAID.label, dataList.get(2).payStatusName);
        Assertions.assertEquals(PayStatus.UNPAID.label, dataList.get(3).payStatusName);

        Assertions.assertEquals(DeliveryStatus.UNDELIVERED.label, dataList.get(0).deliveryStatusName);
        Assertions.assertEquals(DeliveryStatus.DELIVERED.label, dataList.get(1).deliveryStatusName);
        Assertions.assertEquals(DeliveryStatus.RECEIVED.label, dataList.get(2).deliveryStatusName);
        Assertions.assertEquals(DeliveryStatus.UNDELIVERED.label, dataList.get(3).deliveryStatusName);
    }

}
