package com.example.transactionpackage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import netscape.javascript.JSObject;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionrepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    public void createTransaction(TransactionRequest transactionRequest) throws JsonProcessingException {

        //Firstly we will create the transaction Entity and put status in pending
        Transaction transaction = Transaction.builder().fromUser(transactionRequest.getFromUser()).toUser(transactionRequest.getToUser())
                .amount(transactionRequest.getAmount()).purpose(transactionRequest.getPurpose()).transactionId(UUID.randomUUID().toString())
                .transactionDate(new Date()).transactionStatus(TransactionStatus.PENDING).build();

        transactionrepository.save(transaction);

        //create JSON object


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fromUser",transactionRequest.getFromUser());
        jsonObject.put("toUser",transactionRequest.getToUser());
        jsonObject.put("amount",transactionRequest.getAmount());
        jsonObject.put("transactionId",transaction.getTransactionId());

        //converting to string and send it via kafka to the wallet microservice

        String kafkamessage = objectMapper.writeValueAsString(jsonObject);
        kafkaTemplate.send("update_wallet",kafkamessage);

    }
}
