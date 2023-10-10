package com.vault.load.controllers;

import com.vault.load.dto.TransactionResponse;
import com.vault.load.services.LoadFundService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LoadFundsControllerTest {

    @InjectMocks
    LoadFundsController controller;

    @Mock
    LoadFundService service;

    private MockMvc mvc;

    private static final String POST_FUND_DEPOSIT_ENDPOINT = "/api/v1/load-funds";

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
    }

    @Test
    public void testLoadFund_SuccessResp() throws Exception {
        final TransactionResponse mockResp = TransactionResponse.builder().id("15887").customerId("528").accepted(true).build();
        when(service.loadFunds("Admin")).thenReturn(Collections.singletonList(mockResp));

        ResultActions resultActions = mvc.perform(post(POST_FUND_DEPOSIT_ENDPOINT)
                .content(createRequestBody())
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andDo(print());
        resultActions.andExpect(status().isOk());
        String expectedResponse = "{\"data\":[{\"id\":\"15887\",\"customer_id\":\"528\",\"accepted\":true}]}";
        resultActions.andExpect(content().string(expectedResponse));
    }

    @Test
    public void testLoadFund_FailedResp() throws Exception {
        final TransactionResponse mockResp = TransactionResponse.builder().id("15887").customerId("528").accepted(false).build();
        when(service.loadFunds("Admin")).thenReturn(Collections.singletonList(mockResp));

        ResultActions resultActions = mvc.perform(post(POST_FUND_DEPOSIT_ENDPOINT)
                .content(createRequestBody())
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andDo(print());
        resultActions.andExpect(status().isOk());
        String expectedResponse = "{\"data\":[{\"id\":\"15887\",\"customer_id\":\"528\",\"accepted\":false}]}";
        resultActions.andExpect(content().string(expectedResponse));
    }

    private String createRequestBody() {
        return "{\n" +
                "    \"executed_by\": \"Admin\"\n" +
                "}";
    }
}