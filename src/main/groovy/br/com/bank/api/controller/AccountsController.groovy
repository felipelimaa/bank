package br.com.bank.api.controller

import br.com.bank.domain.model.Accounts
import br.com.bank.domain.service.AccountsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/accounts")
class AccountsController {

    @Autowired
    AccountsService accountsService

    //FIXME: This method is not permanently
    @GetMapping
    ResponseEntity<Accounts> findAll() {
        List<Accounts> accounts = accountsService.findAll()
        return ResponseEntity.ok(accounts) as ResponseEntity<Accounts>
    }

    @PostMapping
    ResponseEntity<Accounts> create(@RequestBody Accounts account) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountsService.create(account))
    }
}
