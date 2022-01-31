package br.com.bank.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class AccountsWithoutCreditAvailableException extends ResponseStatusException {
    AccountsWithoutCreditAvailableException() {
        super(HttpStatus.BAD_REQUEST, "O remetente não possui crédito disponível para efetuar a transação.")
    }
}
