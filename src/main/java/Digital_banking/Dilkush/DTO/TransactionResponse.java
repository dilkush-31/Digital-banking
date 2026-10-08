package Digital_banking.Dilkush.DTO;

import Digital_banking.Dilkush.Entity.TransactionStatus;
import Digital_banking.Dilkush.Entity.TransactionType;

import java.math.BigDecimal;

public class TransactionResponse {

    private String transactionReference;
    private TransactionType transactionType;
    private BigDecimal amount;
    private TransactionStatus status;
    private BigDecimal balance;

    public TransactionResponse(
            String transactionReference,
            TransactionType transactionType,
            BigDecimal amount,
            TransactionStatus status,
            BigDecimal balance) {

        this.transactionReference = transactionReference;
        this.transactionType = transactionType;
        this.amount = amount;
        this.status = status;
        this.balance = balance;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}