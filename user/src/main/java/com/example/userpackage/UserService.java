package com.example.userpackage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.time.Duration;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    RedisTemplate<String,User> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    public String addUser(UserRequestDto userRequestDto){
     User user = User.builder().userName(userRequestDto.getUserName()).age(userRequestDto.getAge())
             .mobNo(userRequestDto.getMobNo()).build();

     //save it in the db
     userRepository.save(user);

     //save it in the cache
        saveInCache(user);

     //send and update to the wallet module/wallet service ----> that create a new wallet from username as a String
        kafkaTemplate.send("create_wallet",user.getUserName());

        return "user added successfully";
    }

    public void saveInCache(User user){
        Map map = objectMapper.convertValue(user, Map.class);
        String key = "user_Key"+user.getUserName();
        System.out.println("The user key is"+key);
        redisTemplate.opsForHash().putAll(key,map);
        redisTemplate.expire(user.getUserName(), Duration.ofHours(12));

    }

    public User getUserByUsername(String username){
        User user;
        //logic

        //1.Find in Redis Cache
        Map map = redisTemplate.opsForHash().entries(username);

        //if user is not in cache

        if(map == null){
            //get it from user repository
           user = userRepository.findByUserName(username);

            //save that user in cache
            saveInCache(user);
            return user;
        }else{
            //we found out in redis template

           user = objectMapper.convertValue(map,User.class);
            return user;
        }

    }
}
