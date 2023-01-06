package vttp2022.paf.assessment;

public class queries {
    public static String FIND_CUSTOMER_BY_NAME = "select * from customers where name = ?";

    public static String SAVE_ORDER = "insert into orders(order_id, name, itemname, quantity) values(?, ?, ?, ?)";

    public static String DISPATCH_ORDER = "insert into order_status(order_id, delivery_id, status) values(?, ?, ?)";

    public static String GET_NUMBER_OF_ORDERS = 
    "select count(status) as count from order_status os join customers c join orders o on c.name = o.name on o.order_id = os.order_id where os.status = ? and c.name = ? group by os.order_id";
}