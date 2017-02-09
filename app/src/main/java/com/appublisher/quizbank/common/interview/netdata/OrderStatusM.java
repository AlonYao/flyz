package com.appublisher.quizbank.common.interview.netdata;

/**
 * Created by jinbao on 2017/2/4.
 */

public class OrderStatusM {

    private int response_code;
    private OrderBean order;

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

    public OrderBean getOrder() {
        return order;
    }

    public void setOrder(OrderBean order) {
        this.order = order;
    }

    public static class OrderBean {
        private boolean success;
        private String order_num;
        private int user_id;
        private int product_id;
        private String price;
        private String product_name;
        private String cover_pic;
        private String create_time;
        private boolean need_address;
        private String invalid_time;
        private int valid_order_period;
        private int valid_pay_period;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getOrder_num() {
            return order_num;
        }

        public void setOrder_num(String order_num) {
            this.order_num = order_num;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public int getProduct_id() {
            return product_id;
        }

        public void setProduct_id(int product_id) {
            this.product_id = product_id;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public String getCover_pic() {
            return cover_pic;
        }

        public void setCover_pic(String cover_pic) {
            this.cover_pic = cover_pic;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public boolean isNeed_address() {
            return need_address;
        }

        public void setNeed_address(boolean need_address) {
            this.need_address = need_address;
        }

        public String getInvalid_time() {
            return invalid_time;
        }

        public void setInvalid_time(String invalid_time) {
            this.invalid_time = invalid_time;
        }

        public int getValid_order_period() {
            return valid_order_period;
        }

        public void setValid_order_period(int valid_order_period) {
            this.valid_order_period = valid_order_period;
        }

        public int getValid_pay_period() {
            return valid_pay_period;
        }

        public void setValid_pay_period(int valid_pay_period) {
            this.valid_pay_period = valid_pay_period;
        }
    }
}
