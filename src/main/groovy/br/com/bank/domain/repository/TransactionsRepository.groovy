package br.com.bank.domain.repository

import br.com.bank.domain.model.Accounts
import br.com.bank.domain.model.Transactions
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionsRepository extends JpaRepository<Transactions, Long>{

    List<Transactions> findByAccountsSenderId(Long id)

    List<Transactions> findByAccountsRecipientIdAndTransactionsTypesNeedRecipient(Long id, String needRecipient)

}