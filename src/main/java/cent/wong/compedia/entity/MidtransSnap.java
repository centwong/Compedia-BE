package cent.wong.compedia.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class MidtransSnap implements Serializable {

    @JsonAlias("transaction_details")
    private TransactionDetails transactionDetails;

    @JsonAlias("credit_card")
    private CreditCardDetails creditCardDetails;

    @JsonAlias("customer_details")
    private CustomerDetails customerDetails;

    public Map<String, Object> convertIntoMap(){
        Map<String, Object> params = new LinkedHashMap<>();

        Map<String, Object> transactionDetails = new LinkedHashMap<>();
        transactionDetails.put("order_id", this.transactionDetails.getOrderId());
        transactionDetails.put("gross_amount", this.transactionDetails.getGrossAmount());

        Map<String, Boolean> creditCardDetails = new LinkedHashMap<>();
        creditCardDetails.put("secure", true);

        Map<String, String> customerDetails = new LinkedHashMap<>();
        customerDetails.put("first_name", this.customerDetails.getFirstName());
        customerDetails.put("last_name", this.customerDetails.getLastName());
        customerDetails.put("email", this.customerDetails.getEmail());
        customerDetails.put("phone", this.customerDetails.getPhoneNumber());

        params.put("transaction_details", transactionDetails);
        params.put("credit_card", creditCardDetails);
        params.put("customer_details", customerDetails);

        return params;
    }
}
