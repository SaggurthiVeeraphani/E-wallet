package com.project.majorproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class walletService {
    @Autowired
    static
    KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    static
    ObjectMapper objectMapper;

    @Autowired
    static
    walletRepository walletrepository1;


    @KafkaListener(topics = "create_wallet",groupId = "test1234")
    public void createWallet(String message){
        wallet wallet1 = wallet.builder().userName(message).balance(100).build();

        walletrepository1.save(wallet1);
    }
    @KafkaListener(topics = "update_wallet",groupId = "test1234")
    public static void updateWallet(String message){

        //decoded back to JSON object and extract information
        JSONObject jsonObject = ObjectMapper.convertValue(message,JSONObject.class);
        String fromUser = (String)jsonObject.get("fromUser");
        String toUser = (String)jsonObject.get("toUser");
        int transactionAmount = (Integer)jsonObject.get("amount");
        String transactionID = (String)jsonObject.get("transactionId");

        JSONObject returnObject = new JSONObject();

        returnObject.put("transactionId",transactionID);

        wallet fromUserWallet = walletrepository1.getWalletByUserName(fromUser);

        wallet toUserWallet = walletrepository1.getWalletByUserName(toUser);

        if(fromUserWallet.getBalance() >= transactionAmount){
            //That is successful transaction
            returnObject.put("status","Success");

            kafkaTemplate.send("update Transaction",objectMapper.writeValueAsString(returnObject));

            //updata the sender and receiver wallet


        }else{
            returnObject.put("status","Failed");
            kafkaTemplate.send("update Transaction",objectMapper.writeValueAsString(returnObject));

            //In this case no need to update the wallet
        }


    }
}
