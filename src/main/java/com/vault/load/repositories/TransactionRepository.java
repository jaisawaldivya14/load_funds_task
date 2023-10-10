package com.vault.load.repositories;

import com.vault.load.entities.Transaction;
import com.vault.load.entities.TransactionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, TransactionKey>,
        JpaSpecificationExecutor<Transaction> {

    @Query(value = "SELECT * FROM Transaction WHERE transaction_id = ?1 and customer_id = ?2", nativeQuery = true)
    Optional<Transaction> getByCustomerIdAndTransactionId(String transactionId, String customerId);

    @Query(value = "SELECT sum(amount) FROM Transaction WHERE customer_id = ?1 and transaction_time between DATEADD('DAY',-7, CURRENT_DATE) and CURRENT_DATE", nativeQuery = true)
    Optional<BigDecimal> getTotalTransactionAmount(String customerId, int days);

    @Query(value = "SELECT * FROM Transaction WHERE customer_id = ?1 and FORMATDATETIME(transaction_time, 'yyyy-MM-dd') = FORMATDATETIME(?2,'yyyy-MM-dd')", nativeQuery = true)
    List<Transaction> getTransactions(String customerId, OffsetDateTime time);
}