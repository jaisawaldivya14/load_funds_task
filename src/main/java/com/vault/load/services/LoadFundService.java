package com.vault.load.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.load.dto.TransactionRequest;
import com.vault.load.dto.TransactionResponse;
import com.vault.load.entities.Transaction;
import com.vault.load.entities.TransactionKey;
import com.vault.load.repositories.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LoadFundService {
    @Autowired
    FileReaderService fileReaderService;

    @Autowired
    TransactionRepository repository;

    @Autowired
    FileWriterService fileWriterService;

    @Autowired
    Environment environment;

    @Value("${customer.load.funds.daily.limit.in.usd}")
    private BigDecimal dailyLimit;

    @Value("${customer.load.funds.weekly.limit.in.usd}")
    private BigDecimal weeklyLimit;

    public List<TransactionResponse> loadFunds(String executedBy) throws IOException {
        log.info("[LoadFundService] Load funds Task is executed by  [" + executedBy + "]");

        final List<TransactionResponse> transactionResponse = loadFundsUtils(fileReaderService.loadFundRequests());
        writeToFile(transactionResponse);
        return transactionResponse;
    }

    public TransactionResponse processLoadFundReq(TransactionRequest request) {
        log.debug("Received transaction request  [" + request + "]");
        var transactionAmt = new BigDecimal(request.getLoadAmount().substring(1));

        final var currentDayTransactions = repository.getTransactions(request.getCustomerId(), OffsetDateTime.parse(request.getTime()));

        if (isDailyTransactionCountExceeded(request, currentDayTransactions)) return getLoadFundResp(request, false);

        if (isDailyTransactionLimitExceeded(request, transactionAmt, currentDayTransactions)) return getLoadFundResp(request, false);

        if (isWeeklyTransactionLimitExceeded(request, transactionAmt)) return getLoadFundResp(request, false);

        final Transaction loadFundInstructionsDO = getInstructionsDO(request, transactionAmt);
        repository.save(loadFundInstructionsDO);
        return getLoadFundResp(request, true);
    }

    private boolean isDailyTransactionCountExceeded(TransactionRequest request, List<Transaction> currentDayTransactions) {
        if (currentDayTransactions.size() >= 3) {
            log.error("[LoadFundService] Transaction exceeds daily limit count: [" + request.getId() + "]");
            return true;
        }
        return false;
    }

    private boolean isDailyTransactionLimitExceeded(TransactionRequest request, BigDecimal transactionAmt, List<Transaction> currentDayTransactions) {
        BigDecimal currentDayTotalTxnAmt = currentDayTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (dailyLimit.compareTo(currentDayTotalTxnAmt.add(transactionAmt)) <= 0) {
            log.error("[LoadFundService] Transaction exceeds daily load limit: [" + request.getId() + "]");
            return true;
        }
        return false;
    }

    private Transaction getInstructionsDO(TransactionRequest request, BigDecimal transactionAmt) {
        final Transaction loadFundInstructionsDO = Transaction.builder()
                .amount(transactionAmt)
                .transaction_time(OffsetDateTime.parse(request.getTime()))
                .transactionKey(TransactionKey.builder().customer_id(request.getCustomerId()).transaction_id(request.getId()).build())
                .build();
        return loadFundInstructionsDO;
    }

    private boolean isWeeklyTransactionLimitExceeded(TransactionRequest request, BigDecimal transactionAmt) {
        final var weeklyTransactionAmt = repository.getTotalTransactionAmount(request.getCustomerId(), 7)
                .orElse(BigDecimal.ZERO);

        if (weeklyLimit.compareTo(weeklyTransactionAmt.add(transactionAmt)) <= 0) {
            log.error("[LoadFundService] Transaction exceeds weekly load limit: [" + request.getId() + "]");
            return true;
        }
        return false;
    }

    private TransactionResponse getLoadFundResp(TransactionRequest fundRequest, boolean isSuccess) {
        return TransactionResponse.builder().id(fundRequest.getId()).customerId(fundRequest.getCustomerId()).accepted(isSuccess)
                .build();
    }

    private void writeToFile(List<TransactionResponse> transactionResponse) throws IOException {
        final List<String> txnStrList = transactionResponse.stream().map(this::mapTxnResponseToStr).collect(Collectors.toList());
        fileWriterService.writeListToFile(txnStrList);
    }

    private List<TransactionResponse> loadFundsUtils(List<TransactionRequest> transactionRequests) {
        return transactionRequests
                .stream().map(this::processLoadFundReq)
                .collect(Collectors.toList());
    }

    private String mapTxnResponseToStr(TransactionResponse response) {
        try {
            return new ObjectMapper().writeValueAsString(response);
        } catch (Exception e) {
            log.error("[LoadFundService] Error occurred during mapping resp to string");
            return "{}";
        }
    }
}


