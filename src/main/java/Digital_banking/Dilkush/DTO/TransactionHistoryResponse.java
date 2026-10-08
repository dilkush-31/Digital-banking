package Digital_banking.Dilkush.DTO;

import Digital_banking.Dilkush.Entity.TransactionStatus;
import Digital_banking.Dilkush.Entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TransactionHistoryResponse {

    private String transactionReference;

    private TransactionType type;

    private BigDecimal amount;

    private String senderAccountNumber;

    private String receiverAccountNumber;

    private TransactionStatus status;

    private LocalDateTime createdAt;
}