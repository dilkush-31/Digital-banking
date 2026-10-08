package Digital_banking.Dilkush.DTO;

import Digital_banking.Dilkush.Entity.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {

    @NotNull(message = "Account type is required")
    private AccountType accountType;
}