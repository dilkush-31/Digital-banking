package Digital_banking.Dilkush.Controller;

import Digital_banking.Dilkush.DTO.*;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import Digital_banking.Dilkush.Service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        AccountResponse response =
                accountService.createAccount(
                        request,
                        email
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable String accountNumber,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                accountService.getAccount(
                        accountNumber,
                        email
                )
        );
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable String accountNumber,
            Authentication authentication) {

        String email = authentication.getName();

        return ResponseEntity.ok(
                accountService.getBalance(
                        accountNumber,
                        email
                )
        );
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @Valid @RequestBody DepositRequest request,
            Authentication authentication) {

        TransactionResponse response =
                accountService.deposit(
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(
            @Valid @RequestBody WithdrawRequest request,
            Authentication authentication) {

        TransactionResponse response =
                accountService.withdraw(
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest request,
            Authentication authentication) {

        TransactionResponse response =
                accountService.transfer(
                        request,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}