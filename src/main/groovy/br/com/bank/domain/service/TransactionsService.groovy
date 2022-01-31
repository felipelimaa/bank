package br.com.bank.domain.service

import br.com.bank.api.dto.TransactionsDTO
import br.com.bank.domain.exception.TransactionsEmptyValuesException
import br.com.bank.domain.exception.TransactionsTransferAccountsIsEqualsException
import br.com.bank.domain.exception.TransactionsValueLessThanZero
import br.com.bank.domain.model.Transactions
import br.com.bank.domain.repository.TransactionsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionsService {

    //TODO: realizar deposito
    //TODO: realizar saque
    //TODO: realizar transferencia

    @Autowired
    TransactionsRepository transactionsRepository

    @Autowired
    AccountsService accountsService

    @Autowired
    TransactionsTypesService transactionsTypesService

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

        // verificar se o valor da transacao comporta o disponivel na conta
        accountsService.transactionValue(accountsSender, calculatedValue)

        if(transactionsTypes.needRecipient.toBoolean()) {
            accountsService.transactionValue(accountsRecipient, transactionsDTO.transactionValue)
        }

        transactions.transactionValue = calculatedValue
        transactions.newAvailableCredit = accountsSender.creditAvaiable

        transactionsRepository.save(transactions)

        transactionsDTO.id = transactions.id
        transactionsDTO.transactionValue = calculatedValue
        transactionsDTO.transactionDate = transactions.transactionDate
        transactionsDTO.newAvailableCredit = accountsSender.creditAvaiable

        return transactionsDTO
    }

}
