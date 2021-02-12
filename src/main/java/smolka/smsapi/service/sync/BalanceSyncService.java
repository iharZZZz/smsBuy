package smolka.smsapi.service.sync;

import smolka.smsapi.model.User;

import java.math.BigDecimal;

public interface BalanceSyncService {
    Boolean orderIsPossible(User user, BigDecimal orderCost);
    User subFromRealBalanceAndAddToFreeze(User user, BigDecimal sum);
    User subFromFreezeAndAddToRealBalance(User user, BigDecimal sum);
    User subFromFreeze(User user, BigDecimal sum);
}
