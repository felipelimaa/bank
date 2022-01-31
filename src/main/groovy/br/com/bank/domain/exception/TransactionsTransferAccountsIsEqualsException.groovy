package br.com.bank.domain.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class TransactionsTransferAccountsIsEqualsException extends ResponseStatusException {
    TransactionsTransferAccountsIsEqualsException() {
        super(HttpStatus.BAD_REQUEST, "A conta do remetente é igual a conta do destinatário")
    }
}
