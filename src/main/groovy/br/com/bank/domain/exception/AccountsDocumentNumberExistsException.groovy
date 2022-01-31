package br.com.bank.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class AccountsDocumentNumberExistsException extends ResponseStatusException{
    AccountsDocumentNumberExistsException() {
        super(HttpStatus.CONFLICT, "O número de documento já existe")
    }
}
