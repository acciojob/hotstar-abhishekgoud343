package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto) {
        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setUser(user);

        int totalAmount = 0;
        int noOfScreens = subscription.getNoOfScreensSubscribed();

        switch (subscription.getSubscriptionType()) {
            case BASIC:
                totalAmount = 500 + 200 * noOfScreens;
                break;
            case PRO:
                totalAmount = 800 + 250 * noOfScreens;
                break;
            case ELITE:
                totalAmount = 1000 + 350 * noOfScreens;
        }

        subscription.setTotalAmountPaid(totalAmount);

        user.setSubscription(subscription);

        userRepository.save(user);

        return totalAmount;
    }

    public Integer upgradeSubscription(Integer userId) throws Exception {
        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();

        Subscription subscription = user.getSubscription();
        SubscriptionType subscriptionType = subscription.getSubscriptionType();

        if (subscriptionType.equals(SubscriptionType.ELITE))
            throw new Exception("Already the best Subscription");

        int extraAmount;
        int noOfScreens = subscription.getNoOfScreensSubscribed();

        if (subscriptionType.equals(SubscriptionType.PRO)) {
            extraAmount = 200 + 100 * noOfScreens;
            subscription.setSubscriptionType(SubscriptionType.ELITE);
        }
        else {
            extraAmount = 300 + 50 * noOfScreens;
            subscription.setSubscriptionType(SubscriptionType.PRO);
        }

        subscription.setTotalAmountPaid(subscription.getTotalAmountPaid() + extraAmount);

        subscriptionRepository.save(subscription);

        return extraAmount;
    }

    public Integer calculateTotalRevenueOfHotstar() {
        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        int res = 0;

        for (Subscription subscription : subscriptionRepository.findAll())
            res += subscription.getTotalAmountPaid();

        return res;
    }
}