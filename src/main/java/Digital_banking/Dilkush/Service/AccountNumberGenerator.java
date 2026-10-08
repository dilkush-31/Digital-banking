package Digital_banking.Dilkush.Service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class AccountNumberGenerator {

    public String generate() {

        long number = ThreadLocalRandom.current()
                .nextLong(100000000000L, 1000000000000L);

        return String.valueOf(number);
    }
}