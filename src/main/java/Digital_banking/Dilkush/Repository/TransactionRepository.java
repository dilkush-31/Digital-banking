package Digital_banking.Dilkush.Repository;


import Digital_banking.Dilkush.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionReference(
            String transactionReference
    );

    List<Transaction> findBySenderAccount_AccountNumber(
            String accountNumber
    );

    List<Transaction> findByReceiverAccount_AccountNumber(
            String accountNumber
    );
}