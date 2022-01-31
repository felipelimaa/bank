package br.com.bank.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class InvalidTransactionsTypesException extends ResponseStatusException {
    InvalidTransactionsTypesException() {
        super(HttpStatus.NOT_FOUND, "Tipo de transação não encontrada!")
    }
}
