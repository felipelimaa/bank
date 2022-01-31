package br.com.bank.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class TransactionsEmptyValuesException extends ResponseStatusException{
    TransactionsEmptyValuesException() {
        super(HttpStatus.BAD_REQUEST, "É necessário preencher os campos obrigatórios da transação.")
    }
}
