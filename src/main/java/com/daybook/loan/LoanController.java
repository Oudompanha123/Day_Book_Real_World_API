package com.daybook.loan;

import com.daybook.common.CurrentUser;
import com.daybook.loan.LoanDtos.LoanDto;
import com.daybook.loan.LoanDtos.LoanRequest;
import com.daybook.loan.LoanDtos.PaymentRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService service;

    public LoanController(LoanService service) {
        this.service = service;
    }

    @GetMapping
    List<LoanDto> list(@AuthenticationPrincipal Jwt jwt) {
        return service.list(CurrentUser.id(jwt));
    }

    @GetMapping("/{id}")
    LoanDto get(@AuthenticationPrincipal Jwt jwt, @PathVariable long id) {
        return service.get(CurrentUser.id(jwt), id);
    }

    @PostMapping
    ResponseEntity<LoanDto> create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody LoanRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(CurrentUser.id(jwt), req));
    }

    @PutMapping("/{id}")
    LoanDto update(@AuthenticationPrincipal Jwt jwt, @PathVariable long id, @Valid @RequestBody LoanRequest req) {
        return service.update(CurrentUser.id(jwt), id, req);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable long id) {
        service.delete(CurrentUser.id(jwt), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/payments")
    ResponseEntity<LoanDto> addPayment(@AuthenticationPrincipal Jwt jwt, @PathVariable long id,
                                       @Valid @RequestBody PaymentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addPayment(CurrentUser.id(jwt), id, req));
    }

    @DeleteMapping("/{id}/payments/{paymentId}")
    LoanDto deletePayment(@AuthenticationPrincipal Jwt jwt, @PathVariable long id, @PathVariable long paymentId) {
        return service.deletePayment(CurrentUser.id(jwt), id, paymentId);
    }
}
