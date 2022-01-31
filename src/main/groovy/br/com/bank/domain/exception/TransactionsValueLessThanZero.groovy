package br.com.bank.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class TransactionsValueLessThanZero extends ResponseStatusException {
    TransactionsValueLessThanZero() {
        super(HttpStatus.BAD_REQUEST, "O valor da transação não pode ser menor que 0.")
    }
}
