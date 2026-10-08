package Digital_banking.Dilkush.DTO;

import Digital_banking.Dilkush.Entity.AccountType;
import Digital_banking.Dilkush.Entity.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AccountResponse {

    private String accountNumber;
    private BigDecimal balance;
    private AccountType accountType;
    private AccountStatus status;
    private String ownerName;
}