package Digital_banking.Dilkush.Service;

import Digital_banking.Dilkush.DTO.TransactionHistoryResponse;
import Digital_banking.Dilkush.Entity.Transaction;
import Digital_banking.Dilkush.Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionHistoryResponse> getMyTransactions(String email) {

        List<Transaction> transactions =
                transactionRepository.findTransactionsByUserEmail(email);

        return transactions.stream()
                .map(transaction -> TransactionHistoryResponse.builder()
                        .transactionReference(transaction.getTransactionReference())
                        .type(transaction.getType())
                        .amount(transaction.getAmount())
                        .senderAccountNumber(
                                transaction.getSenderAccount() != null
                                        ? transaction.getSenderAccount().getAccountNumber()
                                        : null
                        )
                        .receiverAccountNumber(
                                transaction.getReceiverAccount() != null
                                        ? transaction.getReceiverAccount().getAccountNumber()
                                        : null
                        )
                        .status(transaction.getStatus())
                        .createdAt(transaction.getCreatedAt())
                        .build()
                )
                .toList();
    }
}