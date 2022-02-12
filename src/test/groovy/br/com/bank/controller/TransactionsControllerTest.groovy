package br.com.bank.controller

import br.com.bank.GeneralTest
import br.com.bank.api.dto.TransactionsDTO
import br.com.bank.api.exceptionHandler.ProblemType
import br.com.bank.domain.model.Accounts
import br.com.bank.domain.model.Transactions
import br.com.bank.domain.model.TransactionsTypes
import br.com.bank.domain.repository.AccountsRepository
import br.com.bank.domain.repository.TransactionsRepository
import br.com.bank.domain.service.TransactionsTypesService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

import java.util.stream.Collectors

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.junit.jupiter.api.Assertions.*

class TransactionsControllerTest extends GeneralTest {

    @Autowired
    MockMvc mvc

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    TransactionsRepository transactionsRepository

    @Autowired
    AccountsRepository accountsRepository

    @Autowired
    TransactionsTypesService transactionsTypesService

    // clear all db data
    void clearDb(){
        transactionsRepository.deleteAll()
        accountsRepository.deleteAll()
    }

    // handle get exists transaction
    @Test
    void transactions_FindExistsTransaction() {
        // clear all db data
        clearDb()

        // create accounts
        Accounts accountsSender = accountsRepository.save(new Accounts(documentNumber: 123))

        // find transaction type
        TransactionsTypes transactionsTypesDeposit = transactionsTypesService.findById(1)

        // create transaction
        Transactions transactions = transactionsRepository.save(new Transactions(transactionsTypes: transactionsTypesDeposit, accountsSender: accountsSender, accountsRecipient: accountsSender, transactionValue: 1000.00, newAvailableCredit: 2000.00))

        // find transaction
        def response = mvc
            .perform(get("/transactions/${transactions.id}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andReturn().response

        Transactions transactionsRecovered = objectMapper.readerFor(Transactions).readValue(response.contentAsString)

        assertEquals(transactionsRecovered.id, transactions.id)
        assertEquals(transactionsRecovered.accountsSender.id, transactions.accountsSender.id)
        assertEquals(transactionsRecovered.accountsRecipient.id, transactions.accountsRecipient.id)
        assertEquals(transactionsRecovered.transactionsTypes.id, transactions.transactionsTypes.id)
        assertEquals(transactionsRecovered.transactionValue, transactions.transactionValue)


    }

    // handle try to find not exists transaction
    @Test
    void transactions_FindNotFoundTransaction(){
        def response = mvc
            .perform(get("/transactions/${Integer.MAX_VALUE}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("title").value(ProblemType.RESOURCE_NOT_FOUND.title)
            )
            .andReturn().response
    }

    // handle create transaction with different transaction type
    @Test
    void transactions_CreateTransactionsWithDifferentTransactionType() {
        // clear all db data
        clearDb()

        // create accounts
        Accounts accountsSender = accountsRepository.save(new Accounts(documentNumber: 123))
        Accounts accountsRecipient = accountsRepository.save(new Accounts(documentNumber: 456))

        // find transactions types
        List<TransactionsTypes> transactionsTypes = new ArrayList<>()
        transactionsTypes.add(transactionsTypesService.findById(1))
        transactionsTypes.add(transactionsTypesService.findById(2))
        transactionsTypes.add(transactionsTypesService.findById(3))

        transactionsTypes.stream().map(transactionsType -> {
            TransactionsDTO transactionsDTO = new TransactionsDTO(operationTypeId: transactionsType.id, accountsSender: accountsSender.id, accountsRecipient: accountsRecipient.id, transactionValue: 1.00)

            def response = mvc
                .perform(
                    post("/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(transactionsDTO))
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andReturn().response

            TransactionsDTO transactionsDTOReturned = objectMapper.readerFor(TransactionsDTO).readValue(response.contentAsString)

            if (transactionsType.operationDecrease.toBoolean()) {
                transactionsDTOReturned.transactionValue *= -1
            }

            assertEquals(transactionsDTOReturned.operationTypeId, transactionsDTO.operationTypeId)
            assertEquals(transactionsDTOReturned.accountsSender, transactionsDTO.accountsSender)
            assertEquals(transactionsDTOReturned.accountsRecipient, transactionsDTO.accountsRecipient)
            assertEquals(transactionsDTOReturned.transactionValue, transactionsDTO.transactionValue)
            assertTrue(transactionsDTOReturned.id > 0)
        })
        .collect(Collectors.toList())
    }

    // handle try to create transaction with invalid data error
    @Test
    void transactions_CreateTransactionsWithError(){
        // clear all db data
        clearDb()

        // create accounts
        Accounts accountsSender = accountsRepository.save(new Accounts(documentNumber: 123))
        Accounts accountsRecipient = accountsRepository.save(new Accounts(documentNumber: 456))

        // create new transactionDTO object
        TransactionsDTO transactions = new TransactionsDTO(operationTypeId: null, accountsSender: accountsSender.id, accountsRecipient: accountsRecipient.id, transactionValue: 10)

        def response = mvc
            .perform(
                post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactions))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("title").value(ProblemType.INVALID_DATA.title)
            )
            .andReturn().response
    }

    // handle try to create transaction with transaction type equals transfer and account recipient equals account sender
    @Test
    void transactions_CreateTransactionsWithTransferAndSenderEqualsRecipient() {
        // clear all db data
        clearDb()

        // create accounts
        Accounts accountsSender = accountsRepository.save(new Accounts(documentNumber: 123))
        Accounts accountsRecipient = accountsSender

        // get transaction type transfer
        TransactionsTypes transactionsTypesTransfer = transactionsTypesService.findById(3)

        // create new transactionsDTO object
        TransactionsDTO transactionsTransfer = new TransactionsDTO(operationTypeId: transactionsTypesTransfer.id, accountsSender: accountsSender.id, accountsRecipient: accountsRecipient.id, transactionValue: 1000.00)

        def response = mvc
            .perform(
                post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionsTransfer))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("title").value(ProblemType.INVALID_DATA.title)
            )
            .andReturn().response
    }

    // handle try to create transaction with transaction value less than zero
    @Test
    void transactions_CreateTransactionsWithValueLessThanZero(){
        // clear all db data
        clearDb()

        // create accounts
        Accounts accountsSender = accountsRepository.save(new Accounts(documentNumber: 123))
        Accounts accountsRecipient = accountsRepository.save(new Accounts(documentNumber: 456))

        // get transaction type transfer
        TransactionsTypes transactionsTypes = transactionsTypesService.findById(3)

        // create new transactionDTO object
        TransactionsDTO transactions = new TransactionsDTO(operationTypeId: transactionsTypes.id, accountsSender: accountsSender.id, accountsRecipient: accountsRecipient.id, transactionValue: -10.00)

        def response = mvc
            .perform(
                post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactions))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("title").value(ProblemType.INVALID_DATA.title)
            )
            .andReturn().response
    }

    // handle attempt to create transaction where account don't have limit credit available
    @Test
    void transactions_CreateTransactionsWhereAccountDontHaveLimitCreditAvailable() {
        // clear all db data
        clearDb()

        // create accounts
        Accounts accountsSender = accountsRepository.save(new Accounts(documentNumber: 123))
        Accounts accountsRecipient = accountsSender

        // get transaction type transfer
        TransactionsTypes transactionsTypes = transactionsTypesService.findById(2)

        // create new transactionDTO object
        TransactionsDTO transactions = new TransactionsDTO(operationTypeId: transactionsTypes.id, accountsSender: accountsSender.id, accountsRecipient: accountsRecipient.id, transactionValue: 2000.00)

        def response = mvc
            .perform(
                post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactions))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("title").value(ProblemType.NO_CREDIT_AVAILABLE.title)
            )
            .andReturn().response
    }


}
