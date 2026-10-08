package Digital_banking.Dilkush.Controller;

import Digital_banking.Dilkush.DTO.TransactionHistoryResponse;
import Digital_banking.Dilkush.Entity.Transaction;
import Digital_banking.Dilkush.Service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping

    public List<TransactionHistoryResponse> getMyTransactions(
            Authentication authentication) {

        String email = authentication.getName();

        return transactionService.getMyTransactions(email);
    }
}