package br.com.bank.domain.model

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotBlank

@Entity
@Table
@ToString(includePackage = false, includeNames = true)
@EqualsAndHashCode
class Accounts {

    // All accounts have initial credit of R$1000,00
    public static BigDecimal INITIAL_CREDIT_AVAIABLE = new BigDecimal("1000")

    @Id
    @Column(name="account_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("account_id")
    Long id

    @NotBlank
    @Column(name="document_number", nullable = false)
    @JsonProperty(value = "document_number", required = true)
    String documentNumber

    @Column(name="credit_avaiable", nullable = false)
    @JsonProperty("credit_avaiable")
    BigDecimal creditAvaiable = INITIAL_CREDIT_AVAIABLE

}
