package vttp2022.paf.assessment.eshop.services;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp2022.paf.assessment.queries;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;

@Service
public class WarehouseService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	// You cannot change the method's signature
	// You may add one or more checked exceptions
	public OrderStatus dispatch(Order order) {

		// TODO: Task 4
		JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
		JsonObjectBuilder objBuilder = Json.createObjectBuilder();
		for (int i = 0; i < order.getLineItems().size(); i++) {
			objBuilder.add("item", order.getLineItems().get(i).getItem())
			.add("quantity", order.getLineItems().get(i).getQuantity());
			JsonObject built = objBuilder.build();
			arrBuilder.add(built);
		}
		JsonArray arrBuilt = arrBuilder.build();

		JsonObject j = Json.createObjectBuilder()
		.add("orderId", order.getOrderId())
		.add("name", order.getCustomer().getName())
		.add("address", order.getCustomer().getAddress())
		.add("email", order.getCustomer().getEmail())
		.add("lineItems", arrBuilt)
		.add("createdBy", "Muhammad Ridhwan Bin Zainal Abidin")
		.build();
		RequestEntity<String> request = RequestEntity.post("http://paf.chuklee.com/dispatch/" + order.getOrderId())
		.accept(MediaType.APPLICATION_JSON)
		.contentType(MediaType.APPLICATION_JSON)
		.body(j.toString());
		
		System.out.println(j.toString());


		RestTemplate template = new RestTemplate();
		ResponseEntity<String> response = null;

		String payload = null;
		// Making the request
	
		try {
			response = template.exchange(request, String.class);
			payload = response.getBody();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		OrderStatus status = new OrderStatus();
		// No error, continue to return OrderStatus Object
		if(response != null){
			System.out.println(response.getStatusCode());
			System.out.printf(">>> Server response:\n%s\n", payload);
			JsonReader reader = Json.createReader(new StringReader(payload));
			JsonObject out = reader.readObject();
			status.setDeliveryId(out.getString("deliveryId"));
			status.setOrderId(out.getString("orderId"));
			status.setStatus("dispatched");
			System.out.println(status.getStatus());
			int check = jdbcTemplate.update(queries.DISPATCH_ORDER,
			 status.getOrderId(), 
			 status.getDeliveryId(), 
			 status.getStatus());

			 return status;
		} 
		// If error encountered
		else {
			status.setOrderId(order.getOrderId());
			status.setStatus("pending");
			System.out.println(status.getStatus());
			int check = jdbcTemplate.update(queries.DISPATCH_ORDER,
			 status.getOrderId(), 
			 "Error encountered. Item not delivered.", 
			 status.getStatus());
			 return status;
		}
		
	}
}