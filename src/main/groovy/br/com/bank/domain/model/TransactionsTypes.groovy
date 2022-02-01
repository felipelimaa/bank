package br.com.bank.domain.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table
@ToString(includePackage = false,includeNames = true)
@EqualsAndHashCode
class TransactionsTypes {

    @Id
    @Column(name = "transactiontype_id")
    @JsonProperty("transactiontype_id")
    Long id

    @Column(name = "description")
    @JsonProperty("description")
    String description

    @Column(name = "need_recipient")
    @JsonIgnore
    String needRecipient

    @Column(name = "operation_decrease")
    @JsonIgnore
    String operationDecrease

}
