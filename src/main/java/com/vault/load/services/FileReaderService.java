package com.vault.load.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.load.dto.CustomerTransactionKey;
import com.vault.load.dto.TransactionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class FileReaderService {

    private final Resource inputFile;

    public FileReaderService(@Value("classpath:input.txt") Resource inputFile) {
        this.inputFile = inputFile;
    }

    public List<TransactionRequest> loadFundRequests() throws IOException {
        Map<CustomerTransactionKey, TransactionRequest> uniqueRequestMap = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputFile.getInputStream()))) {
            String line;
            ObjectMapper objectMapper = new ObjectMapper();
            while ((line = reader.readLine()) != null) {
                TransactionRequest request = objectMapper.readValue(line, TransactionRequest.class);
                final CustomerTransactionKey key = CustomerTransactionKey.builder().id(request.getId()).customerId(request.getCustomerId()).build();
                if (!uniqueRequestMap.containsKey(key)) {
                    uniqueRequestMap.put(key, request);
                }
            }

            return new ArrayList<>(uniqueRequestMap.values());
        }
    }
}

