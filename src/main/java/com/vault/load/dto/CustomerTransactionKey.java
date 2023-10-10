package com.vault.load.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerTransactionKey {
    private String id;
    private String customerId;
}