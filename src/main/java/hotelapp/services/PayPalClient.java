package hotelapp.services;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PayPalClient {
    String clientId = "ARgYDj9RIViPklbz5XbLe7sSZpaBboL5LufzxGekuEhs5nHJXe6bpQtT8fVFgGM6HeIrpZu966g2tWDZ";
    String clientSecret = "EKmgk0VQtAjh4CeVCAThT-M8tu9HypB3-sgJM05CS2yqyRCdimOv1OdF7Q4r8M9oxdqEqnl1c3puO_HO";

    public Map<String, Object> createPayment(String sum, HttpServletRequest request){
        Map<String, Object> response = new HashMap<String, Object>();
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(sum);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        String appUrl = request.getScheme() + "://" + request.getServerName();

        if(appUrl.contains("localhost")) {
            redirectUrls.setCancelUrl("http://localhost:8080/register");
            redirectUrls.setReturnUrl("http://localhost:8080/register");
        } else {
            redirectUrls.setCancelUrl("http://hacktues.com/register");
            redirectUrls.setReturnUrl("http://hacktues.com/register");
        }

        payment.setRedirectUrls(redirectUrls);

        Payment createdPayment;

        try {
            String redirectUrl = "";
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            createdPayment = payment.create(context);
            if(createdPayment!=null){
                List<Links> links = createdPayment.getLinks();
                for (Links link:links) {
                    if(link.getRel().equals("approval_url")){
                        redirectUrl = link.getHref();
                        break;
                    }
                }
                response.put("status", "success");
                response.put("redirect_url", redirectUrl);
            }
        } catch (PayPalRESTException e) {
            System.out.println("Error happened during payment creation!");
        }
        return response;
    }

    public Map<String, Object> completePayment(HttpServletRequest req){
        Map<String, Object> response = new HashMap();
        Payment payment = new Payment();
        payment.setId(req.getParameter("paymentId"));

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(req.getParameter("PayerID"));
        try {
            APIContext context = new APIContext(clientId, clientSecret, "sandbox");
            Payment createdPayment = payment.execute(context, paymentExecution);
            if(createdPayment!=null){
                response.put("status", "success");
                response.put("payment", createdPayment);
            }
        } catch (PayPalRESTException e) {
            System.err.println(e.getDetails());
        }
        return response;
    }
}
