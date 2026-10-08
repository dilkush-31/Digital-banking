package Digital_banking.Dilkush.Service;
import Digital_banking.Dilkush.DTO.DepositRequest;
import Digital_banking.Dilkush.DTO.TransactionResponse;
import Digital_banking.Dilkush.Entity.AccountStatus;
import Digital_banking.Dilkush.Entity.Transaction;
import Digital_banking.Dilkush.Entity.TransactionStatus;
import Digital_banking.Dilkush.Entity.TransactionType;
import Digital_banking.Dilkush.DTO.WithdrawRequest;
import Digital_banking.Dilkush.Exception.*;
import Digital_banking.Dilkush.DTO.TransferRequest;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import Digital_banking.Dilkush.Repository.TransactionRepository;

import Digital_banking.Dilkush.DTO.AccountRequest;
import Digital_banking.Dilkush.DTO.AccountResponse;
import Digital_banking.Dilkush.Entity.*;
import Digital_banking.Dilkush.Repository.AccountRepository;
import Digital_banking.Dilkush.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountResponse createAccount(
            AccountRequest request,
            String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        String accountNumber;

        do {
            accountNumber =
                    accountNumberGenerator.generate();

        } while (
                accountRepository
                        .existsByAccountNumber(accountNumber)
        );

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .balance(BigDecimal.ZERO)
                .accountType(request.getAccountType())
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build();

        Account savedAccount =
                accountRepository.save(account);

        return new AccountResponse(
                savedAccount.getAccountNumber(),
                savedAccount.getBalance(),
                savedAccount.getAccountType(),
                savedAccount.getStatus(),
                savedAccount.getUser().getName()
        );
    }

    public AccountResponse getAccount(
            String accountNumber,
            String email) {

        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));

        if (!account.getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccountAccessException(
                    "You are not authorized to access this account");
        }

        return new AccountResponse(
                account.getAccountNumber(),
                account.getBalance(),
                account.getAccountType(),
                account.getStatus(),
                account.getUser().getName()
        );
    }

    public BigDecimal getBalance(
            String accountNumber,
            String email) {

        Account account = accountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Account not found"));

        if (!account.getUser().getEmail().equals(email)) {
            throw new RuntimeException(
                    "You are not authorized to access this account");
        }

        return account.getBalance();
    }

    @Transactional
    public TransactionResponse deposit(DepositRequest request, String email) {

        Account account = accountRepository
                .findByAccountNumberForUpdate(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You are not authorized to use this account");
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        BigDecimal newBalance =
                account.getBalance().add(request.getAmount());

        account.setBalance(newBalance);

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setTransactionReference(UUID.randomUUID().toString());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(request.getAmount());
        transaction.setReceiverAccount(account);
        transaction.setStatus(TransactionStatus.SUCCESS);

        transactionRepository.save(transaction);

        return new TransactionResponse(
                transaction.getTransactionReference(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getStatus(),
                account.getBalance()
        );
    }

    @Transactional
    public TransactionResponse withdraw(
            WithdrawRequest request,
            String email) {

        Account account = accountRepository
                .findByAccountNumberForUpdate(request.getAccountNumber())
                .orElseThrow(() ->
                        new RuntimeException("Account not found"));

        // Check account ownership
        if (!account.getUser().getEmail().equals(email)) {
            throw new RuntimeException(
                    "You are not authorized to use this account");
        }

        // Check account status
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new InactiveAccountException(
                    "Account is " + account.getStatus()
            );
        }

        // Check amount
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Amount must be greater than zero");
        }

        // Check sufficient balance
        if (account.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance");
        }

        // Deduct money
        BigDecimal newBalance =
                account.getBalance()
                        .subtract(request.getAmount());

        account.setBalance(newBalance);

        accountRepository.save(account);

        // Create transaction record
        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                UUID.randomUUID().toString());

        transaction.setType(
                TransactionType.WITHDRAW);

        transaction.setAmount(request.getAmount());

        transaction.setSenderAccount(account);

        transaction.setStatus(
                TransactionStatus.SUCCESS);

        transactionRepository.save(transaction);

        return new TransactionResponse(
                transaction.getTransactionReference(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getStatus(),
                account.getBalance()
        );
    }

    @Transactional
    public TransactionResponse transfer(
            TransferRequest request,
            String email) {

        Optional<Transaction> existingTransaction =
                transactionRepository.findByIdempotencyKey(
                        request.getIdempotencyKey()
                );

        if (existingTransaction.isPresent()) {

            Transaction transaction = existingTransaction.get();

            return new TransactionResponse(
                    transaction.getTransactionReference(),
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getStatus(),
                    transaction.getSenderAccount().getBalance()
            );
        }


        // Same account check
        if (request.getSenderAccountNumber()
                .equals(request.getReceiverAccountNumber())) {

            throw new RuntimeException(
                    "Sender and receiver accounts cannot be same");
        }

        // Amount check
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Amount must be greater than zero");
        }

        String senderNumber = request.getSenderAccountNumber();
        String receiverNumber = request.getReceiverAccountNumber();

        Account sender;
        Account receiver;

        // Lock accounts in a fixed order
        if (senderNumber.compareTo(receiverNumber) < 0) {

            sender = accountRepository
                    .findByAccountNumberForUpdate(senderNumber)
                    .orElseThrow(() ->
                            new RuntimeException("Sender account not found"));

            receiver = accountRepository
                    .findByAccountNumberForUpdate(receiverNumber)
                    .orElseThrow(() ->
                            new RuntimeException("Receiver account not found"));

        } else {

            receiver = accountRepository
                    .findByAccountNumberForUpdate(receiverNumber)
                    .orElseThrow(() ->
                            new RuntimeException("Receiver account not found"));

            sender = accountRepository
                    .findByAccountNumberForUpdate(senderNumber)
                    .orElseThrow(() ->
                            new RuntimeException("Sender account not found"));
        }

        // Ownership check
        if (!sender.getUser().getEmail().equals(email)) {

            throw new RuntimeException(
                    "You are not authorized to use this account");
        }

        // Account status
        if (sender.getStatus() != AccountStatus.ACTIVE) {

            throw new RuntimeException(
                    "Sender account is not active");
        }

        if (receiver.getStatus() != AccountStatus.ACTIVE) {

            throw new RuntimeException(
                    "Receiver account is not active");
        }

        // Balance check
        if (sender.getBalance()
                .compareTo(request.getAmount()) < 0) {

            throw new InsufficientBalanceException(
                    "Insufficient balance");
        }

        // Debit sender
        sender.setBalance(
                sender.getBalance()
                        .subtract(request.getAmount())
        );

        // Credit receiver
        receiver.setBalance(
                receiver.getBalance()
                        .add(request.getAmount())
        );

        accountRepository.save(sender);
        accountRepository.save(receiver);

        // Create transaction
        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                UUID.randomUUID().toString());

        transaction.setIdempotencyKey(
                request.getIdempotencyKey());

        transaction.setType(
                TransactionType.TRANSFER);

        transaction.setAmount(
                request.getAmount());

        transaction.setSenderAccount(sender);
        transaction.setReceiverAccount(receiver);

        transaction.setStatus(
                TransactionStatus.SUCCESS);

        transactionRepository.save(transaction);

        return new TransactionResponse(
                transaction.getTransactionReference(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getStatus(),
                sender.getBalance()
        );
    }
}