package br.com.bank.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class TransactionsNotFoundException extends ResponseStatusException {
    TransactionsNotFoundException() {
        super(HttpStatus.NOT_FOUND, "A transação não foi localizada.")
    }
}
