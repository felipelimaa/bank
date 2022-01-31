package br.com.bank.domain.repository

import br.com.bank.domain.model.TransactionsTypes
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionsTypesRepository extends JpaRepository<TransactionsTypes, Long> {
}
