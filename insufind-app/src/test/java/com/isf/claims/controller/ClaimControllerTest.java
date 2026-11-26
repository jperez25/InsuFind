package com.isf.claims.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.isf.claims.model.SearchScope;
import com.isf.claims.model.dto.ClaimDtos.ClaimDetail;
import com.isf.claims.model.dto.ClaimDtos.ClaimPage;
import com.isf.claims.model.dto.ClaimSearchCriteria;
import com.isf.claims.service.ClaimService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** HTTP status codes only. The service is mocked; response bodies and business logic are tested elsewhere. */
@WebMvcTest(ClaimController.class)
class ClaimControllerTest {

    private static final String CLAIMS = "/api/claims";
    private static final String CLAIMS_SEARCH = "/api/claims/search";
    private static final String EMPTY_CRITERIA = "{}";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private ClaimService service;

    @BeforeEach
    void stubService() {
        var emptyPage = new ClaimPage(List.of(), 0, 5, 0, 0);
        when(service.search(any(String.class), any(SearchScope.class), anyInt(), anyInt())).thenReturn(emptyPage);
        when(service.search(any(ClaimSearchCriteria.class), anyInt(), anyInt())).thenReturn(emptyPage);
        when(service.find(anyLong())).thenReturn(Optional.empty());
    }

    // GET /api/claims

    @Test
    void searchWithoutParamsReturns200() throws Exception {
        mvc.perform(get(CLAIMS)).andExpect(status().isOk());
    }

    @Test
    void searchWithUnknownScopeReturns400() throws Exception {
        mvc.perform(get(CLAIMS).param("scope", "NOBODY")).andExpect(status().isBadRequest());
    }

    @Test
    void searchWithNegativePageReturns400() throws Exception {
        mvc.perform(get(CLAIMS).param("page", "-1")).andExpect(status().isBadRequest());
    }

    @Test
    void searchWithSizeZeroReturns400() throws Exception {
        mvc.perform(get(CLAIMS).param("size", "0")).andExpect(status().isBadRequest());
    }

    @Test
    void searchWithSizeAboveMaxReturns400() throws Exception {
        mvc.perform(get(CLAIMS).param("size", "51")).andExpect(status().isBadRequest());
    }

    @Test
    void searchWithNonNumericPageReturns400() throws Exception {
        mvc.perform(get(CLAIMS).param("page", "abc")).andExpect(status().isBadRequest());
    }

    // POST /api/claims/search

    @Test
    void criteriaSearchWithEmptyBodyObjectReturns200() throws Exception {
        mvc.perform(post(CLAIMS_SEARCH).contentType(MediaType.APPLICATION_JSON).content(EMPTY_CRITERIA))
                .andExpect(status().isOk());
    }

    @Test
    void criteriaSearchWithMalformedJsonReturns400() throws Exception {
        mvc.perform(post(CLAIMS_SEARCH).contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criteriaSearchWithMissingBodyReturns400() throws Exception {
        mvc.perform(post(CLAIMS_SEARCH).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void criteriaSearchWithInvalidStateCodeReturns400() throws Exception {
        mvc.perform(post(CLAIMS_SEARCH).contentType(MediaType.APPLICATION_JSON).content("{\"policyState\":\"CALIF\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criteriaSearchWithReversedLossDateRangeReturns400() throws Exception {
        mvc.perform(post(CLAIMS_SEARCH).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lossDateFrom\":\"2025-02-01\",\"lossDateTo\":\"2025-01-01\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criteriaSearchWithUnknownStatusReturns400() throws Exception {
        mvc.perform(post(CLAIMS_SEARCH).contentType(MediaType.APPLICATION_JSON).content("{\"statuses\":[\"NOPE\"]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criteriaSearchWithNonJsonContentTypeReturns415() throws Exception {
        mvc.perform(post(CLAIMS_SEARCH).contentType(MediaType.TEXT_PLAIN).content(EMPTY_CRITERIA))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void criteriaSearchWithInvalidPagingReturns400() throws Exception {
        mvc.perform(post(CLAIMS_SEARCH).contentType(MediaType.APPLICATION_JSON).content(EMPTY_CRITERIA)
                        .param("size", "51"))
                .andExpect(status().isBadRequest());
    }

    // GET /api/claims/{id}

    @Test
    void getExistingClaimReturns200() throws Exception {
        var detail = new ClaimDetail(1L, null, null, null, null, null, null, null, null, null, null, null, null,
                null, List.of());
        when(service.find(1L)).thenReturn(Optional.of(detail));

        mvc.perform(get(CLAIMS + "/1")).andExpect(status().isOk());
    }

    @Test
    void getUnknownClaimReturns404() throws Exception {
        mvc.perform(get(CLAIMS + "/999999")).andExpect(status().isNotFound());
    }

    @Test
    void getClaimWithNonNumericIdReturns400() throws Exception {
        mvc.perform(get(CLAIMS + "/abc")).andExpect(status().isBadRequest());
    }

    // Unsupported methods

    @Test
    void putOnClaimsReturns405() throws Exception {
        mvc.perform(put(CLAIMS)).andExpect(status().isMethodNotAllowed());
    }

    @Test
    void deleteOnCriteriaSearchReturns405() throws Exception {
        mvc.perform(delete(CLAIMS_SEARCH)).andExpect(status().isMethodNotAllowed());
    }
}
