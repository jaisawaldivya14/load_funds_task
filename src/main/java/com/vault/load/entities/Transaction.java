package com.vault.load.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Builder
@Table
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transaction {
    @EmbeddedId
    private TransactionKey transactionKey;

    @Column
    private BigDecimal amount;

    @Column
    private OffsetDateTime transaction_time;
}

