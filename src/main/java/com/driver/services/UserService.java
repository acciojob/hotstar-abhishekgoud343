package com.driver.services;


import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;

    public Integer addUser(User user) {
        //Jut simply add the user to the Db and return the userId returned by the repository
        return userRepository.save(user).getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId) {
        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user = userRepository.findById(userId).get();

        int res = 0;
        SubscriptionType subscriptionType = user.getSubscription().getSubscriptionType();

        for (WebSeries webSeries : webSeriesRepository.findAll()) {
            if (user.getAge() < webSeries.getAgeLimit()) continue;

            if (subscriptionType.equals(SubscriptionType.ELITE)) //ELITE can watch anything
                ++res;
            else if (webSeries.getSubscriptionType().equals(SubscriptionType.BASIC)) //anyone can watch basic
                ++res;
            else if (subscriptionType.equals(webSeries.getSubscriptionType())) // PRO & PRO
                ++res;
        }

        return res;
    }
}