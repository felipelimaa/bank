package br.com.bank.controller

import br.com.bank.GeneralTest
import br.com.bank.api.exceptionHandler.ProblemType
import br.com.bank.domain.model.Accounts
import br.com.bank.domain.repository.AccountsRepository
import br.com.bank.domain.repository.TransactionsRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.junit.jupiter.api.Assertions.*

class AccountsControllerTest extends GeneralTest {

    @Autowired
    MockMvc mvc

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    AccountsRepository accountsRepository

    @Autowired
    TransactionsRepository transactionsRepository

    // clear all db data
    void clearDb(){
        transactionsRepository.deleteAll()
        accountsRepository.deleteAll()
    }

    Accounts createAccount(){
        return new Accounts(documentNumber: "123")
    }

    // handle new account register with success
    @Test
    void accounts_InsertWithSuccess(){
        // clear all db data
        clearDb()

        def account = createAccount()

        def response = mvc
            .perform(
                post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated())
            .andReturn().response

        Accounts accountsReturned = objectMapper.readerFor(Accounts).readValue(response.contentAsString)
        assertEquals(accountsReturned.documentNumber, account.documentNumber)
        assertNotNull(accountsReturned.id)

    }

    // handle new account register error
    @Test
    void accounts_InsertWithError(){
        def account = new Accounts(documentNumber: null)

        def response = mvc
            .perform(
                post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpectAll(
                status().isBadRequest(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("title").value(ProblemType.INVALID_DATA.title)
            )
            .andReturn().response

    }

    // handle new account register equal an exists document number
    @Test
    void accounts_InsertWithDocumentNumberExists(){
        // clear all db data
        clearDb()
        // create and save new account
        accountsRepository.save(createAccount())

        // create secondary account
        def account = createAccount()

        def response = mvc
            .perform(
                post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpectAll(
                status().isConflict(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("title").value(ProblemType.EXISTS_RESOURCE_EQUALS.title)
            )
            .andReturn().response
    }

    // handle get exist account
    @Test
    void accounts_FindExistAccount(){
        // clear all db data
        clearDb()

        // create account
        Accounts accounts = accountsRepository.save(createAccount())

        // get account
        def response = mvc
            .perform(get("/accounts/${accounts.id}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andReturn().response

        Accounts accountsReturned = objectMapper.readerFor(Accounts).readValue(response.contentAsString)

        assertEquals(accountsReturned.id, accounts.id)
        assertEquals(accountsReturned.documentNumber, accounts.documentNumber)

    }

    // handle get not found account
    @Test
    void accounts_FindNotFoundAccount(){
        // get account
        def response = mvc
            .perform(get("/accounts/${Integer.MAX_VALUE}"))
            .andDo(MockMvcResultHandlers.print())
            .andExpectAll(
                status().isNotFound(),
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("title").value(ProblemType.RESOURCE_NOT_FOUND.title)
            )
            .andReturn().response
    }


}
