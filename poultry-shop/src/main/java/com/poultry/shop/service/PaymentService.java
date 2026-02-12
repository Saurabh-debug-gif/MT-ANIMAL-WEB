package com.poultry.shop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${cashfree.appId}")
    private String appId;

    @Value("${cashfree.secretKey}")
    private String secretKey;

    @Value("${cashfree.env}")
    private String env;   // sandbox / prod

    private String getBaseUrl() {
        if ("prod".equalsIgnoreCase(env)) {
            return "https://api.cashfree.com/pg";
        } else {
            return "https://sandbox.cashfree.com/pg";
        }
    }

    public String createPaymentSession(String orderId,
                                       double amount,
                                       String customerName,
                                       String customerPhone,
                                       String customerEmail) {

        String url = getBaseUrl() + "/orders";

        Map<String, Object> payload = new HashMap<>();
        payload.put("order_id", orderId);
        payload.put("order_amount", amount);
        payload.put("order_currency", "INR");

        payload.put("order_meta", Map.of(
                "return_url", "https://www.mt-animal-care.shop/payment/success?order_id={order_id}",
                "notify_url", "https://www.mt-animal-care.shop/payment/webhook"
        ));



        Map<String, String> customer = new HashMap<>();
        customer.put("customer_id", "CUST_" + System.currentTimeMillis());
        customer.put("customer_name", customerName);
        customer.put("customer_phone", customerPhone);
        customer.put("customer_email", customerEmail);

        payload.put("customer_details", customer);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", appId);
        headers.set("x-client-secret", secretKey);
        headers.set("x-api-version", "2023-08-01");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map body = response.getBody();

        if (body != null && body.get("payment_session_id") != null) {
            return body.get("payment_session_id").toString();   // ✅ THIS IS WHAT CASHFREE RETURNS
        }

        throw new RuntimeException("Cashfree invalid response: " + body);
    }
}
