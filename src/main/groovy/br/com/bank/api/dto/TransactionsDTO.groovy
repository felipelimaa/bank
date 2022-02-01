package br.com.bank.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.CreationTimestamp

import java.time.LocalDateTime

class TransactionsDTO {

    @JsonProperty("transaction_id")
    Long id

    @JsonProperty(value ="account_sender", required = true)
    Long accountsSender

    @JsonProperty(value ="account_recipient", required = true)
    Long accountsRecipient

    @JsonProperty(value = "operation_type_id", required = true)
    Long operationTypeId

    @JsonProperty(value = "value", required = true)
    BigDecimal transactionValue

    @JsonProperty(value = "new_available_credit")
    BigDecimal newAvailableCredit

    @JsonProperty(value = "transaction_date")
    LocalDateTime transactionDate

}
