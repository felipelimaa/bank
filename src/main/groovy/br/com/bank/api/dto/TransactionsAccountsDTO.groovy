package br.com.bank.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.builder.Builder

import java.time.LocalDateTime

@Builder
class TransactionsAccountsDTO {

    @JsonProperty("account_id")
    Long id

    @JsonProperty(value ="document_number")
    String documentNumber

    @JsonProperty(value = "credit_avaiable")
    BigDecimal creditAvaiable

    //@JsonProperty(value = "transactions")
    List<TransactionsObject> transactions

    static class TransactionsObject {
        Long id

        BigDecimal transactionValue

        LocalDateTime transactionDate

        BigDecimal newAvailableCredit

        TransactionsTypesObject transactionsTypes

        AccountsRecipient accountsRecipient

        AccountsSender accountsSender

        static class AccountsRecipient {
            Long id

            String documentNumber
        }

        static class AccountsSender {
            Long id

            String documentNumber
        }

        static class TransactionsTypesObject {
            Long id

            String description
        }

    }

}
