package br.com.bank.api.controller

import br.com.bank.api.dto.TransactionsDTO
import br.com.bank.domain.model.Transactions
import br.com.bank.domain.service.TransactionsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/transactions")
class TransactionsController {

    @Autowired
    TransactionsService transactionsService

    @GetMapping("/{id}")
    ResponseEntity<Transactions> findById(@PathVariable Long id){
        return ResponseEntity.ok(transactionsService.findById(id)) as ResponseEntity<Transactions>
    }

    @PostMapping
    ResponseEntity<TransactionsDTO> create(@RequestBody TransactionsDTO transactionsDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionsService.create(transactionsDTO))
    }

}
