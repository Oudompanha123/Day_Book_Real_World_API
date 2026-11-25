package com.daybook.entry;

import com.daybook.common.ApiException;
import com.daybook.common.Dates;
import com.daybook.entry.EntryDtos.EntryDto;
import com.daybook.entry.EntryDtos.EntryRequest;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntryService {

    private final EntryRepository entries;

    public EntryService(EntryRepository entries) {
        this.entries = entries;
    }

    /** Newest first for one month, or oldest first for everything (used by the CSV export). */
    @Transactional(readOnly = true)
    public List<EntryDto> list(long userId, YearMonth month) {
        List<Entry> found = month == null
                ? entries.findByUserIdOrderByEntryDateAscCreatedAtAsc(userId)
                : entries.findByUserIdAndEntryDateBetweenOrderByEntryDateDescCreatedAtDesc(
                        userId, month.atDay(1), month.atEndOfMonth());
        return found.stream().map(EntryDto::from).toList();
    }

    @Transactional
    public EntryDto create(long userId, EntryRequest req) {
        if (Dates.isFuture(req.date())) throw ApiException.badRequest("future_date");
        Entry e = new Entry(userId, req.kind(), req.amount(), trim(req.note()),
                req.category().trim(), req.payMethod().trim(), req.date());
        return EntryDto.from(entries.save(e));
    }

    @Transactional
    public void delete(long userId, long id) {
        Entry e = entries.findByIdAndUserId(id, userId).orElseThrow(ApiException::notFound);
        entries.delete(e);
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
