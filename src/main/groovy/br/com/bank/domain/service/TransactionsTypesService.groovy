package br.com.bank.domain.service

import br.com.bank.domain.exception.InvalidTransactionsTypesException
import br.com.bank.domain.model.TransactionsTypes
import br.com.bank.domain.repository.TransactionsTypesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TransactionsTypesService {

    @Autowired
    TransactionsTypesRepository transactionsTypesRepository

    TransactionsTypes findById(Long id) {
        return transactionsTypesRepository.findById(id).orElseThrow{new InvalidTransactionsTypesException()}
    }

}
