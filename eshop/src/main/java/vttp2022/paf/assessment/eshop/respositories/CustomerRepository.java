package vttp2022.paf.assessment.eshop.respositories;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp2022.paf.assessment.queries;
import vttp2022.paf.assessment.eshop.models.Customer;

@Repository
public class CustomerRepository {

	@Autowired
	JdbcTemplate template;

	// You cannot change the method's signature
	public Optional<Customer> findCustomerByName(String name) {
		
		// TODO: Task 3 
		String nameToPrint = "1";

		Customer customer = new Customer();
		
		//Checking if customer exists
		final SqlRowSet query = template.queryForRowSet(queries.FIND_CUSTOMER_BY_NAME, name);
		while(query.next()) {
			nameToPrint = query.getString("name");
			customer.setName(nameToPrint);
			customer.setAddress(query.getString("address"));
			customer.setEmail(query.getString("email"));
		}

		if(nameToPrint.equals("1")){
			Customer response = new Customer();
			response.setName(name);
			response.setAddress("1");
			return Optional.of(response);
		} else {
			return Optional.of(customer);
		}
	}	
}