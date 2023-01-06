package vttp2022.paf.assessment.eshop.controllers;

/*
--- Railway Deployment ---
NOTE: pom.xml java version must be 11

Railway login
Railway link
Railway up -d
 */

import java.util.Optional;
import java.util.UUID;

import javax.print.attribute.standard.Media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp2022.paf.assessment.queries;
import vttp2022.paf.assessment.eshop.models.Customer;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.respositories.CustomerRepository;
import vttp2022.paf.assessment.eshop.respositories.OrderRepository;
import vttp2022.paf.assessment.eshop.services.WarehouseService;
import vttp2022.paf.assessment.eshop.models.OrderStatus;

@RestController
@RequestMapping(path="/api")
public class OrderController {

	@Autowired
	CustomerRepository custRepo;
	
	@Autowired
	OrderRepository orderRepo;

	@Autowired
	WarehouseService whService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	//TODO: Task 3
	@PostMapping(path="/order", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getHome(@RequestBody Order order) {
				
		//Checking if the customer is valid
		//Checking whether username exists...
		String name = order.getCustomer().getName();
		Optional<Customer> customer = custRepo.findCustomerByName(name);

		if(customer.get().getAddress().equals("1")){
			JsonObject j = Json.createObjectBuilder().add("error", "Customer " + customer.get().getName() + " not found").build();
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(j.toString());
		}

		//Task 3 part b. Populating the model
		String orderId = UUID.randomUUID().toString().substring(0, 8);
		order.setOrderId(orderId);
		order.setCustomer(customer.get());

		/*
		db273525
		null
		fred
		201 Cobblestone Lane
		fredflintstone@bedrock.com
		null
		Fri Jan 06 11:30:13 SGT 2023
		[vttp2022.paf.assessment.eshop.models.LineItem@46682173]
		 */
		// Task 3 part d. Saving the order to database
		try {
			orderRepo.saveOrder(order);

			OrderStatus status = whService.dispatch(order);
			if(status.getStatus().equals("dispatched")){
				JsonObject j = Json.createObjectBuilder()
					.add("orderId", status.getOrderId())
					.add("deliveryId", status.getDeliveryId())
					.add("status", status.getStatus()).build();
				return ResponseEntity.status(HttpStatus.OK).body(j.toString());
			} else {
				JsonObject j = Json.createObjectBuilder()
					.add("orderId", status.getOrderId())
					.add("status", status.getStatus()).build();
				return ResponseEntity.status(HttpStatus.OK).body(j.toString());
			}
		} catch (Exception e) {
			JsonObject j = Json.createObjectBuilder().add("error", e.getMessage()).build();
			return ResponseEntity.ok().body(j.toString());
		}



		// JsonObject j = Json.createObjectBuilder().add("Success", "testing").build();

		// return ResponseEntity.ok().body(j.toString());
	}

	@GetMapping(path="/order/{name}/status", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getInformation(@PathVariable("name") String name) {

		int dispatchedCount = 0;
		int pendingCount = 0;
		final SqlRowSet dispatchquery = jdbcTemplate.queryForRowSet(queries.GET_NUMBER_OF_ORDERS, "dispatched", name);
		while(dispatchquery.next()) {
			dispatchedCount++;
		}

		final SqlRowSet pendingquery = jdbcTemplate.queryForRowSet(queries.GET_NUMBER_OF_ORDERS, "pending", name);
		while(pendingquery.next()) {
			pendingCount++;
		}

		StringBuilder dc = new StringBuilder();
		dc.append(dispatchedCount);

		StringBuilder pc = new StringBuilder();
		pc.append(pendingCount);

		JsonObject j = Json.createObjectBuilder()
			.add("name", name)
			.add("dispatched", dc.toString())
			.add("pending", pc.toString()).build();
		return ResponseEntity.status(HttpStatus.OK).body(j.toString());
	}

}