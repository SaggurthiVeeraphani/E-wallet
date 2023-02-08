package com.project.majorproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class walletService {
    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    walletRepository walletrepository;


    @KafkaListener(topics = "create_wallet",groupId = "test1234")
    public void createWallet(String message){
        wallet Wallet = wallet.builder().userName(message).balance(100).build();

        walletrepository.save(Wallet);

    }
}