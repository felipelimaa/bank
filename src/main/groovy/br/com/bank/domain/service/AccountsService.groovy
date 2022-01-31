package br.com.bank.domain.service

import br.com.bank.domain.exception.AccountsDocumentNumberExistsException
import br.com.bank.domain.exception.AccountsWithoutDocumentNumberException
import br.com.bank.domain.model.Accounts
import br.com.bank.domain.repository.AccountsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.transaction.Transactional

@Service
class AccountsService {

    @Autowired
    AccountsRepository accountsRepository

    List<Accounts> findAll() {
        return accountsRepository.findAll()
    }

    @Transactional
    Accounts create(Accounts account) {
        // Feature for check if exists document number within payload
        if(!account.documentNumber) {
            throw new AccountsWithoutDocumentNumberException()
        }

        boolean accountExists = accountsRepository.existsByDocumentNumber(account.documentNumber)

        // Feature for restrict create account with same document number
        if(accountExists) {
            throw new AccountsDocumentNumberExistsException()
        }

        return accountsRepository.save(account)
    }

}
