package com.vault.load.controllers;


import com.vault.load.dto.LoadFundsRequest;
import com.vault.load.dto.TransactionResponse;
import com.vault.load.services.LoadFundService;
import com.vault.load.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class LoadFundsController {

    @Autowired
    LoadFundService service;

    @RequestMapping(value = "/api/v1/load-funds", method = RequestMethod.POST)
    public ResponseEntity<Result> loadFunds(@RequestBody LoadFundsRequest request) throws Exception {
        final List<TransactionResponse> responseList = service.loadFunds(request.getExecutedBy());
        return new ResponseEntity<>(new Result(responseList), HttpStatus.OK);
    }
}

