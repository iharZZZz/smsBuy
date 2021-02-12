package smolka.smsapi.service.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smolka.smsapi.model.User;
import smolka.smsapi.service.api_key.UserService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class BalanceSyncServiceImpl implements BalanceSyncService {

    @Autowired
    private UserService userService;

    private interface BalanceOperationLambda<T> {
        T getResultOfOperation(User user, BigDecimal sum);
    }

    @AllArgsConstructor
    private static class Worker<T> implements Callable<T> {

        private final BalanceSyncServiceImpl self;
        private final BalanceOperationLambda<T> operationLambda;
        private final BigDecimal cost;
        private final User user;

        @Override
        public T call() throws Exception {
            try {
                self.entryInSyncBlock(user);
                return operationLambda.getResultOfOperation(user, cost);
            } finally {
                self.release(user);
            }
        }
    }

    private final Map<Long, Lock> workersMap = new ConcurrentHashMap<>();

    @Override
    public Boolean orderIsPossible(User user, BigDecimal orderCost) {
        BalanceOperationLambda<Boolean> orderIsPossible = (userArg, sum) -> userService.orderIsPossible(userArg, sum);
        return getResult(orderIsPossible, user, orderCost);
    }

    @Override
    public User subFromRealBalanceAndAddToFreeze(User user, BigDecimal sum) {
        BalanceOperationLambda<User> subFromRealBalanceAndAddToFreeze = (userArg, sumArg) -> userService.subFromRealBalanceAndAddToFreeze(userArg, sumArg);
        return getResult(subFromRealBalanceAndAddToFreeze, user, sum);
    }

    @Override
    public User subFromFreezeAndAddToRealBalance(User user, BigDecimal sum) {
        BalanceOperationLambda<User> subFromFreezeAndAddToRealBalance = (userArg, sumArg) -> userService.subFromFreezeAndAddToRealBalance(userArg, sumArg);
        return getResult(subFromFreezeAndAddToRealBalance, user, sum);
    }

    @Override
    public User subFromFreeze(User user, BigDecimal sum) {
        BalanceOperationLambda<User> subFromFreeze = (userArg, sumArg) -> userService.subFromFreeze(userArg, sumArg);
        return getResult(subFromFreeze, user, sum);
    }

    private <T> T getResult(BalanceOperationLambda<T> operation, User user, BigDecimal cost) {
        try {
            workersMap.putIfAbsent(user.getUserId(), new ReentrantLock());
            Worker<T> worker = new Worker<>(this, operation, cost, user);
            FutureTask<T> futureResult = new FutureTask<T>(worker);
            new Thread(futureResult).start();
            return futureResult.get();
        } catch (Exception ignore) {
            return null;
        }
    }
    
    private void entryInSyncBlock(User user) {
        workersMap.get(user.getUserId()).lock();
    }

    private void release(User user) {
        workersMap.get(user.getUserId()).unlock();
    }
}
