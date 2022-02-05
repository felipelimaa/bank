package br.com.bank.service

import br.com.bank.GeneralTest
import br.com.bank.domain.exception.AccountsDocumentNumberExistsException
import br.com.bank.domain.exception.AccountsNotFoundException
import br.com.bank.domain.exception.AccountsWithoutDocumentNumberException
import br.com.bank.domain.model.Accounts
import br.com.bank.domain.repository.AccountsRepository
import br.com.bank.domain.repository.TransactionsRepository
import br.com.bank.domain.service.AccountsService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.jupiter.api.Assertions.*

class AccountsServiceTest extends GeneralTest {

    @Autowired
    AccountsService accountsService

    @Autowired
    AccountsRepository accountsRepository

    @Autowired
    TransactionsRepository transactionsRepository

    // clear all db data
    void clearDb(){
        transactionsRepository.deleteAll()
        accountsRepository.deleteAll()
    }

    // handle new account register with success
    @Test
    void accounts_InsertWithSuccess(){
        Accounts accounts = new Accounts(documentNumber: "123")
        Accounts accountsCreated = accountsService.create(accounts)

        assertEquals(accountsCreated.documentNumber, accounts.documentNumber)
        assertTrue(accountsCreated.id > 0)
    }

    // handle new account register error
    @Test
    void accounts_InsertWithError(){
        Accounts accounts = new Accounts(documentNumber: null)
        assertThrows(AccountsWithoutDocumentNumberException.class, { accountsService.create(accounts) })
    }

    // handle new account register equal an exists document number
    @Test
    void accounts_InsertWithDocumentNumberExists(){
        // clear all db data
        clearDb()

        // new account
        Accounts accounts = new Accounts(documentNumber: "123")

        // save first account
        accountsService.create(accounts)

        // save secondary account
        assertThrows(AccountsDocumentNumberExistsException.class, { accountsService.create(accounts) })
    }

    // handle get exist account
    @Test
    void accounts_FindExistAccount(){
        // clear all db data
        clearDb()

        // new and save account
        Accounts accounts = accountsService.create(new Accounts(documentNumber: "123"))

        // get account
        Accounts accountsRecovered = accountsService.findById(accounts.id)

        assertEquals(accountsRecovered.id, accounts.id)
        assertEquals(accountsRecovered.documentNumber, accounts.documentNumber)

    }

    // handle get not found account
    @Test
    void accounts_FindNotFoundAccount(){
        assertThrows(AccountsNotFoundException.class, { accountsService.findById(Integer.MAX_VALUE) })
    }
}
