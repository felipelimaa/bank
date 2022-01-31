package br.com.bank.domain.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table
@ToString(includePackage = false, includeNames = true)
@EqualsAndHashCode
class Transactions {

    @Id
    @Column(name = "transaction_id")
    @JsonProperty("transaction_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_sender_id", nullable = true)
    Accounts accountsSender

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_recipient_id", nullable = false)
    Accounts accountsRecipient

    @OneToOne
    @JoinColumn(name="transactiontype_id", nullable = false)
    TransactionsTypes transactionsTypes

    @Column(name = "transaction_value")
    @JsonProperty("value")
    BigDecimal transactionValue

    @Column(name = "transaction_date", insertable = false, updatable = false)
    @JsonProperty("transaction_date")
    Date transactionDate = new Date()

    @Column(name = "new_available_credit")
    @JsonIgnoreProperties(value = "new_available_credit", allowGetters = true)
    BigDecimal newAvailableCredit

}
