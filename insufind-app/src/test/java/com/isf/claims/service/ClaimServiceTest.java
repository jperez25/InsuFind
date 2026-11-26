package com.isf.claims.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.isf.claims.model.Claim;
import com.isf.claims.model.SearchScope;
import com.isf.claims.model.dto.ClaimSearchCriteria;
import com.isf.claims.repository.ClaimRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

class ClaimServiceTest {

    private final ClaimRepository repository = mock(ClaimRepository.class);
    private final ClaimService service = new ClaimService(repository);

    @Test
    void otherScopeOnlySearchesOtherDrivers() {
        when(repository.search(any(), eq(false), eq(true), eq(false), any(Pageable.class)))
                .thenReturn(new PageImpl<Claim>(List.of()));

        assertThat(service.search("smith", SearchScope.OTHER, 0, 5).items()).isEmpty();

        verify(repository).search(eq("%smith%"), eq(false), eq(true), eq(false), any(Pageable.class));
    }

    @Test
    void likeWildcardsInTheQueryAreEscaped() {
        when(repository.search(any(), eq(true), eq(true), eq(true), any(Pageable.class)))
                .thenReturn(new PageImpl<Claim>(List.of()));

        service.search("  50%_Off ", SearchScope.ANY, 0, 5);

        verify(repository).search(eq("%50\\%\\_off%"), eq(true), eq(true), eq(true), any(Pageable.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    void criteriaSearchSortsNewestIncidentFirstWithIdTieBreaker() {
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<Claim>(List.of()));
        var criteria = new ClaimSearchCriteria(null, null, null, null, null, null, null, null, null, null, null, null, null);

        assertThat(service.search(criteria, 2, 10).items()).isEmpty();

        verify(repository).findAll(any(Specification.class), argThat((Pageable p) ->
                p.getPageNumber() == 2 && p.getPageSize() == 10
                        && p.getSort().toString().equals("incidentDate: DESC,id: DESC")));
    }

    @Test
    void invertedDateRangesAreInvalid() {
        var lossRange = new ClaimSearchCriteria(null, null, null, null,
                java.time.LocalDate.of(2026, 9, 1), java.time.LocalDate.of(2026, 1, 1),
                null, null, null, null, null, null, null);
        var reportedRange = new ClaimSearchCriteria(null, null, null, null, null, null,
                java.time.LocalDate.of(2026, 9, 1), java.time.LocalDate.of(2026, 1, 1),
                null, null, null, null, null);

        assertThat(lossRange.isLossDateRangeValid()).isFalse();
        assertThat(reportedRange.isReportedRangeValid()).isFalse();
    }
}
