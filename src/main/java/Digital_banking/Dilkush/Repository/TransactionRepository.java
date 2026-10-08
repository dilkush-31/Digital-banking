package Digital_banking.Dilkush.Repository;


import Digital_banking.Dilkush.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {



    Optional<Transaction> findByTransactionReference(
            String transactionReference
    );

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    List<Transaction> findBySenderAccount_AccountNumber(
            String accountNumber
    );

    List<Transaction> findByReceiverAccount_AccountNumber(
            String accountNumber
    );
    @Query("""
       SELECT t FROM Transaction t
       WHERE t.senderAccount.user.email = :email
          OR t.receiverAccount.user.email = :email
       ORDER BY t.createdAt DESC
       """)
    List<Transaction> findTransactionsByUserEmail(@Param("email") String email);
}