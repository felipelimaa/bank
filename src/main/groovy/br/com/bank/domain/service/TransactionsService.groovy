package br.com.bank.domain.service

import br.com.bank.api.dto.TransactionsAccountsDTO
import br.com.bank.api.dto.TransactionsDTO
import br.com.bank.domain.exception.TransactionsEmptyValuesException
import br.com.bank.domain.exception.TransactionsNotFoundException
import br.com.bank.domain.exception.TransactionsTransferAccountsIsEqualsException
import br.com.bank.domain.exception.TransactionsValueLessThanZero
import br.com.bank.domain.model.Accounts
import br.com.bank.domain.model.Transactions
import br.com.bank.domain.repository.TransactionsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.util.stream.Collectors

@Service
class TransactionsService {

    @Autowired
    TransactionsRepository transactionsRepository

    @Autowired
    AccountsService accountsService

    @Autowired
    TransactionsTypesService transactionsTypesService

    Transactions findById(Long id){
        Transactions transactions = transactionsRepository.findById(id).orElseThrow{ new TransactionsNotFoundException() }

        transactions.accountsSender.creditAvaiable = null
        transactions.accountsRecipient.creditAvaiable = null

        return transactions
    }

    TransactionsAccountsDTO findAllTransactionsByAccount(Long id){
        // find account object
        Accounts accounts = accountsService.findById(id)

        // find transactions send by account selected
        List<Transactions> transactionsSender = transactionsRepository.findByAccountsSenderId(accounts.id)

        // find transactions received by account selected, when transactions types need recipient is true
        List<Transactions> transactionsRecipient = transactionsRepository.findByAccountsRecipientIdAndTransactionsTypesNeedRecipient(accounts.id, "true")

        // set transactionsSender and transactionsRecipient in List of TransactionsAccountsDTO.TransactionsObject
        List<TransactionsAccountsDTO.TransactionsObject> transactions = transactionsSender
                .stream()
                .map(transaction ->
                    new TransactionsAccountsDTO.TransactionsObject(
                        id: transaction.id,
                        transactionValue: transaction.transactionValue,
                        transactionDate: transaction.transactionDate,
                        newAvailableCredit: transaction.newAvailableCredit,
                        transactionsTypes: new TransactionsAccountsDTO.TransactionsObject.TransactionsTypesObject(
                            id: transaction.transactionsTypes.id,
                            description: transaction.transactionsTypes.description
                        ),
                        accountsRecipient: new TransactionsAccountsDTO.TransactionsObject.AccountsRecipient(
                            id: transaction.accountsRecipient.id,
                            documentNumber: transaction.accountsRecipient.documentNumber
                        )
                    )
                )
                .collect(Collectors.toList())

        transactions += transactionsRecipient
                .stream()
                .map(transaction ->
                    new TransactionsAccountsDTO.TransactionsObject(
                        id: transaction.id,
                        transactionValue: transaction.transactionValue,
                        transactionDate: transaction.transactionDate,
                        newAvailableCredit: transaction.newAvailableCredit,
                        transactionsTypes: new TransactionsAccountsDTO.TransactionsObject.TransactionsTypesObject(
                                id: transaction.transactionsTypes.id,
                                description: transaction.transactionsTypes.description
                        ),
                        accountsSender: new TransactionsAccountsDTO.TransactionsObject.AccountsSender(
                            id: transaction.accountsSender.id,
                            documentNumber: transaction.accountsSender.documentNumber
                        )
                    )
                )
                .collect(Collectors.toList())

        // Create TransactionsAccountDTO to assign data gives account and transactions
        def transactionsAccountsDTO = new TransactionsAccountsDTO(id: accounts.id, documentNumber: accounts.documentNumber, creditAvaiable: accounts.creditAvaiable, transactions: transactions)

        return transactionsAccountsDTO
    }

    @Transactional
    TransactionsDTO create(TransactionsDTO transactionsDTO) {
        // verify if required fields with in transaction
        if(transactionsDTO.transactionValue == null
            && transactionsDTO.operationTypeId == null
            && transactionsDTO.accountsSender == null
        ) {
            throw new TransactionsEmptyValuesException()
        }

        def transactionsTypes = transactionsTypesService.findById(transactionsDTO.operationTypeId)

        // verify if transaction type tag is equals T (Transfer)
        if(transactionsTypes.needRecipient.toBoolean()
            && transactionsDTO.accountsSender == transactionsDTO.accountsRecipient
        ) {
            throw new TransactionsTransferAccountsIsEqualsException()
        }

        // verify if value transaction is less than zero
        if(transactionsDTO.transactionValue < 0 ) {
            throw new TransactionsValueLessThanZero()
        }

        def accountsSender = accountsService.findById(transactionsDTO.accountsSender)

        def calculatedValue = transactionsDTO.transactionValue

        // verify if decrease operation and if this is possible of account credit decrease
        if(transactionsTypes.operationDecrease.toBoolean()){
            calculatedValue *= -1
        }

        def accountsRecipient = null

        // ever transaction type is not need recipient the account recipient be equals to account sender
        if(!transactionsTypes.needRecipient.toBoolean()) {
            accountsRecipient = accountsSender
        } else {
            accountsRecipient = accountsService.findById(transactionsDTO.accountsRecipient)
        }

        def transactions = new Transactions(transactionsTypes: transactionsTypes, accountsSender: accountsSender, accountsRecipient: accountsRecipient)

        // verify if this value is possible and it is available to transaction
        accountsService.transactionValue(accountsSender, calculatedValue)

        if(transactionsTypes.needRecipient.toBoolean()) {
            accountsService.transactionValue(accountsRecipient, transactionsDTO.transactionValue)
        }

        transactions.transactionValue = calculatedValue
        transactions.newAvailableCredit = accountsSender.creditAvaiable

        transactions = transactionsRepository.save(transactions)

        transactionsDTO.id = transactions.id
        transactionsDTO.transactionValue = calculatedValue
        transactionsDTO.transactionDate = transactions.transactionDate
        transactionsDTO.newAvailableCredit = accountsSender.creditAvaiable

        return transactionsDTO
    }

}
