package br.com.bank.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class AccountsWithoutDocumentNumberException extends ResponseStatusException {
    AccountsWithoutDocumentNumberException() {
        super(HttpStatus.BAD_REQUEST, "O número de documento é obrigatório!")
    }
}
