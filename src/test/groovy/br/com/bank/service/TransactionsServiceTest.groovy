package br.com.bank.service

import br.com.bank.GeneralTest
import br.com.bank.api.dto.TransactionsDTO
import br.com.bank.domain.exception.AccountsWithoutCreditAvailableException
import br.com.bank.domain.exception.TransactionsNotFoundException
import br.com.bank.domain.exception.TransactionsTransferAccountsIsEqualsException
import br.com.bank.domain.exception.TransactionsValueLessThanZero
import br.com.bank.domain.model.Accounts
import br.com.bank.domain.model.Transactions
import br.com.bank.domain.model.TransactionsTypes
import br.com.bank.domain.repository.AccountsRepository
import br.com.bank.domain.repository.TransactionsRepository
import br.com.bank.domain.service.TransactionsService
import br.com.bank.domain.service.TransactionsTypesService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.InvalidDataAccessApiUsageException

import java.util.stream.Collectors

import static org.junit.jupiter.api.Assertions.*

class TransactionsServiceTest extends GeneralTest {

    @Autowired
    TransactionsRepository transactionsRepository

    @Autowired
    AccountsRepository accountsRepository

    @Autowired
    TransactionsTypesService transactionsTypesService

    @Autowired
    TransactionsService transactionsService

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

        // get transaction
        Transactions transactionsRecovered = transactionsService.findById(transactions.id)

        assertEquals(transactionsRecovered.id, transactions.id)
        assertEquals(transactionsRecovered.accountsSender.id, transactions.accountsSender.id)
        assertEquals(transactionsRecovered.accountsRecipient.id, transactions.accountsRecipient.id)
        assertEquals(transactionsRecovered.transactionsTypes.id, transactions.transactionsTypes.id)
        assertEquals(transactionsRecovered.transactionValue, transactions.transactionValue)

    }

    // handle try to find not exists transaction
    @Test
    void transactions_FindNotFoundTransaction(){
        // clear all db data
        clearDb()

        assertThrows(TransactionsNotFoundException.class, { transactionsService.findById(Integer.MAX_VALUE) })
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
            TransactionsDTO transactionsDTO = new TransactionsDTO(operationTypeId: transactionsType.id, accountsSender: accountsSender.id, accountsRecipient: accountsRecipient.id, transactionValue: 1000.00)

            TransactionsDTO transactionsDTOCreated = transactionsService.create(transactionsDTO)

            if (transactionsType.operationDecrease.toBoolean()) {
                transactionsDTOCreated.transactionValue *= -1
            }

            assertEquals(transactionsDTOCreated.operationTypeId, transactionsDTO.operationTypeId)
            assertEquals(transactionsDTOCreated.accountsSender, transactionsDTO.accountsSender)
            assertEquals(transactionsDTOCreated.accountsRecipient, transactionsDTO.accountsRecipient)
            assertEquals(transactionsDTOCreated.transactionValue, transactionsDTO.transactionValue)
            assertTrue(transactionsDTOCreated.id > 0)
        }).collect(Collectors.toList())
    }

    // handle try to create transaction with invalid data error
    @Test
    void transactions_CreateTransactionsWithError(){
        // clear all db data
        clearDb()

        // create accounts
        Accounts accountsSender = accountsRepository.save(new Accounts(documentNumber: 123))
        Accounts accountsRecipient = accountsSender

        // create new transactionDTO object
        TransactionsDTO transactions = new TransactionsDTO(operationTypeId: null, accountsSender: accountsSender.id, accountsRecipient: accountsRecipient.id, transactionValue: 10)

        // validation transaction
        assertThrows(InvalidDataAccessApiUsageException.class, { transactionsService.create(transactions) })

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

        // validation transfer
        assertThrows(TransactionsTransferAccountsIsEqualsException.class, { transactionsService.create(transactionsTransfer) })
    }

    // handle try to create transaction with transaction value less than zero
    @Test
    void transactions_CreateTransactionsWithValueLessThanZero(){
        // clear all db data
        clearDb()

        // create accounts
        Accounts accountsSender = accountsRepository.save(new Accounts(documentNumber: 123))
        Accounts accountsRecipient = accountsSender

        // get transaction type deposit
        TransactionsTypes transactionsTypesDeposit = transactionsTypesService.findById(1)

        // create new transactionsDTO object
        TransactionsDTO transactionsDTO = new TransactionsDTO(operationTypeId: transactionsTypesDeposit.id, accountsSender: accountsSender.id, accountsRecipient: accountsRecipient.id, transactionValue: -1000.00)

        // validation transaction
        assertThrows(TransactionsValueLessThanZero.class, { transactionsService.create(transactionsDTO) })
    }

    // handle attempt to create transaction where account don't have limit credit available
    @Test
    void transactions_CreateTransactionsWhereAccountDontHaveLimitCreditAvailable() {
        // clear all db data
        clearDb()

        // create accounts
        Accounts accountsSender = accountsRepository.save(new Accounts(documentNumber: 123))
        Accounts accountsRecipient = accountsSender

        // get transaction type draft
        TransactionsTypes transactionsTypesDraft = transactionsTypesService.findById(2)

        // create new transactionsDTO object
        TransactionsDTO transactionsDTO = new TransactionsDTO(operationTypeId: transactionsTypesDraft.id, accountsSender: accountsSender.id, accountsRecipient: accountsRecipient.id, transactionValue: 2000.00)

        // validation transaction
        assertThrows(AccountsWithoutCreditAvailableException.class, { transactionsService.create(transactionsDTO) })
    }

}
