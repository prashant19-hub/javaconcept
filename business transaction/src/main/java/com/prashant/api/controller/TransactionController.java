package com.prashant.api.controller;

import com.prashant.api.dto.TransactionRequest;
import com.prashant.store.model.Transaction;
import com.prashant.store.repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    private final TransactionRepository repository;

    public TransactionController(TransactionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Transaction addTransaction(@RequestBody TransactionRequest request) {
        Transaction transaction = new Transaction(
                request.getCustomerName(),
                request.getProductName(),
                request.getAmount(),
                request.getType()
        );

        return repository.save(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionRequest request
    ) {
        return repository.findById(id)
                .map(transaction -> {
                    transaction.setCustomerName(request.getCustomerName());
                    transaction.setProductName(request.getProductName());
                    transaction.setAmount(request.getAmount());
                    transaction.setType(request.getType());
                    return ResponseEntity.ok(repository.save(transaction));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
