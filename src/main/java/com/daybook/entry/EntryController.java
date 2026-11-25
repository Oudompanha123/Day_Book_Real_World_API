package com.daybook.entry;

import com.daybook.common.CurrentUser;
import com.daybook.entry.EntryDtos.EntryDto;
import com.daybook.entry.EntryDtos.EntryRequest;
import jakarta.validation.Valid;
import java.time.YearMonth;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/entries")
public class EntryController {

    private final EntryService service;

    public EntryController(EntryService service) {
        this.service = service;
    }

    /** GET /api/entries?month=2026-11 for one month; without month, every entry. */
    @GetMapping
    List<EntryDto> list(@AuthenticationPrincipal Jwt jwt, @RequestParam(required = false) YearMonth month) {
        return service.list(CurrentUser.id(jwt), month);
    }

    @PostMapping
    ResponseEntity<EntryDto> create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody EntryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(CurrentUser.id(jwt), req));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@AuthenticationPrincipal Jwt jwt, @PathVariable long id) {
        service.delete(CurrentUser.id(jwt), id);
        return ResponseEntity.noContent().build();
    }
}
