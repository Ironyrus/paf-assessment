package vttp2022.paf.assessment.eshop.respositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vttp2022.paf.assessment.orderException;
import vttp2022.paf.assessment.queries;
import vttp2022.paf.assessment.eshop.models.Order;

@Repository
public class OrderRepository {
	// TODO: Task 3
	@Autowired
	JdbcTemplate template;

	@Transactional(rollbackFor = orderException.class)
	public void saveOrder(Order order){
		int check = 1000;
		for (int i = 0; i < order.getLineItems().size(); i++) {
			check = template.update(queries.SAVE_ORDER, 
			order.getOrderId(), 
			order.getName(), 
			order.getLineItems().get(i).getItem(), 
			order.getLineItems().get(i).getQuantity());
			
		}
		System.out.println(check);

	}
}