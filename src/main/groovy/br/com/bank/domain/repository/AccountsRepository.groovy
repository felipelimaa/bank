package br.com.bank.domain.repository

import br.com.bank.domain.model.Accounts
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AccountsRepository extends JpaRepository<Accounts, Long> {

    boolean existsByDocumentNumber(String documentNumber)

}
