package cent.wong.compedia.util;

import cent.wong.compedia.entity.CreditCardDetails;
import cent.wong.compedia.entity.CustomerDetails;
import cent.wong.compedia.entity.MidtransSnap;
import cent.wong.compedia.entity.TransactionDetails;

public class MidtransUtil {

    public static MidtransSnap generateSnapData(
            TransactionDetails transactionDetails,
            CustomerDetails customerDetails
    ){
        MidtransSnap midtransSnap = new MidtransSnap();
        midtransSnap.setTransactionDetails(transactionDetails);
        midtransSnap.setCustomerDetails(customerDetails);
        midtransSnap.setCreditCardDetails(new CreditCardDetails());

        return midtransSnap;
    }
}
