package com.vault.load.services;

import com.vault.load.config.ServiceTestConfiguration;
import com.vault.load.dto.TransactionRequest;
import com.vault.load.dto.TransactionResponse;
import com.vault.load.entities.Transaction;
import com.vault.load.repositories.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = ServiceTestConfiguration.class)
public class LoadFundServiceTest {

    @Autowired
    private LoadFundService loadFundService;

    @MockBean
    private FileReaderService fileReaderService;

    @MockBean
    private TransactionRepository repository;

    @MockBean
    private FileWriterService fileWriterService;

    @Value("${customer.load.funds.daily.limit.in.usd}")
    private String dailyLimit;

    @Value("${customer.load.funds.weekly.limit.in.usd}")
    private String weeklyLimit;

    @Test
    public void testProcessLoadFundReq_SuccessfulTransaction() {
        TransactionRequest request = getTransactionRequest("$100.00");

        when(repository.getTransactions(anyString(), any(OffsetDateTime.class))).thenReturn(Collections.emptyList());
        when(repository.getTotalTransactionAmount(anyString(), anyInt())).thenReturn(Optional.of(BigDecimal.ZERO));
        when(repository.save(any(Transaction.class))).thenReturn(mock(Transaction.class));

        TransactionResponse response = loadFundService.processLoadFundReq(request);

        assertTrue(response.isAccepted());

        verify(repository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void testProcessLoadFundReq_FailedTransactionWithHigherDailyCount() {
        TransactionRequest request = getTransactionRequest("$1000.00");

        // Add 3 txns and check for fourth one
        final List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(new Transaction());
        transactionList.add(new Transaction());
        transactionList.add(new Transaction());

        when(repository.getTransactions("123", OffsetDateTime.parse("2023-01-01T12:00:00Z"))).thenReturn(transactionList);

        TransactionResponse response = loadFundService.processLoadFundReq(request);

        assertFalse(response.isAccepted());

        verify(repository, times(1)).getTransactions(anyString(),  any(OffsetDateTime.class));
        verify(repository, never()).save(any(Transaction.class));
        verify(repository, never()).getTotalTransactionAmount(anyString(), anyInt());
    }

    @Test
    public void testProcessLoadFundReq_FailedTransactionWithHigherDailyLimit() {
        TransactionRequest request = getTransactionRequest("$6000.00");

        when(repository.getTransactions(anyString(), any(OffsetDateTime.class))).thenReturn(Collections.emptyList());

        TransactionResponse response = loadFundService.processLoadFundReq(request);

        assertFalse(response.isAccepted());

        verify(repository, times(1)).getTransactions(anyString(),  any(OffsetDateTime.class));
        verify(repository, never()).save(any(Transaction.class));
        verify(repository, never()).getTotalTransactionAmount(anyString(), anyInt());
    }

    @Test
    public void testProcessLoadFundReq_FailedTransactionWithHigherWeaklyLimit() {
        TransactionRequest request = getTransactionRequest("$2000.00");

        when(repository.getTotalTransactionAmount("123", 7)).thenReturn(Optional.of(new BigDecimal(20000)));
        when(repository.getTransactions(anyString(), any(OffsetDateTime.class))).thenReturn(Collections.emptyList());

        TransactionResponse response = loadFundService.processLoadFundReq(request);

        assertFalse(response.isAccepted());

        verify(repository, times(1)).getTransactions(anyString(),  any(OffsetDateTime.class));
        verify(repository, never()).save(any(Transaction.class));
    }


    private TransactionRequest getTransactionRequest(String amount) {
        TransactionRequest request = new TransactionRequest();
        request.setId("1");
        request.setCustomerId("123");
        request.setLoadAmount(amount);
        request.setTime("2023-01-01T12:00:00Z");
        return request;
    }
}
