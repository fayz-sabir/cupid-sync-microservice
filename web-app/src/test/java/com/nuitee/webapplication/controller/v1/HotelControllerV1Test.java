package com.nuitee.webapplication.controller.v1;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.nuitee.app.usecase.FullSyncUseCase;
import com.nuitee.app.usecase.GetHotelViewUseCase;
import com.nuitee.app.usecase.GetReviewStatsUseCase;
import com.nuitee.app.usecase.GetReviewsUseCase;
import com.nuitee.app.usecase.SyncHotelUseCase;
import com.nuitee.app.usecase.SyncReviewsUseCase;
import com.nuitee.domain.view.HotelView;
import com.nuitee.domain.view.ReviewStatsView;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@Import(GlobalExceptionHandler.class)

@WebMvcTest(controllers = HotelControllerV1.class)
@AutoConfigureMockMvc(addFilters = false)
class HotelControllerV1Test {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GetHotelViewUseCase getHotelViewUseCase;
    @MockBean
    SyncHotelUseCase syncHotelUseCase;
    @MockBean
    SyncReviewsUseCase syncReviewsUseCase;
    @MockBean
    GetReviewsUseCase getReviewsUseCase;
    @MockBean
    GetReviewStatsUseCase getReviewStatsUseCase;
    @MockBean
    FullSyncUseCase fullSyncUseCase;

    @Test
    void viewReturnsHotel() throws Exception {
        HotelView view = new HotelView(
            1L, "Name", 4.0, null, null, null, null, null, null,
            List.of(), List.of(), List.of(), List.of(), List.of()
        );
        when(getHotelViewUseCase.execute(anyLong(), anyString(), anyBoolean(), anyInt()))
            .thenReturn(view);

        mockMvc.perform(get("/api/v1/hotels/1").param("lang", "en"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hotelId").value(1));
    }

    @Test
    void viewInvalidLangReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/hotels/1").param("lang", "zz"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void syncHotelReturnsOk() throws Exception {
        mockMvc.perform(post("/api/v1/hotels/1/sync").param("warmLang", "en"))
            .andExpect(status().isOk());
    }

    @Test
    void syncHotelInvalidId() throws Exception {
        mockMvc.perform(post("/api/v1/hotels/-1/sync").param("warmLang", "en"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void syncReviewsReturnsOk() throws Exception {
        mockMvc.perform(post("/api/v1/hotels/1/reviews/sync").param("limit", "5"))
            .andExpect(status().isOk());
    }

    @Test
    void syncReviewsInvalidLimit() throws Exception {
        mockMvc.perform(post("/api/v1/hotels/1/reviews/sync").param("limit", "0"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void syncAllReturnsOk() throws Exception {
        mockMvc.perform(post("/api/v1/hotels/sync-all"))
            .andExpect(status().isOk());
    }

    @Test
    void reviewsReturnsOk() throws Exception {
        when(getReviewsUseCase.execute(anyLong(), anyInt())).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/hotels/1/reviews"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void reviewsInvalidLimit() throws Exception {
        mockMvc.perform(get("/api/v1/hotels/1/reviews").param("limit", "0"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }

    @Test
    void reviewStatsReturnsOk() throws Exception {
        when(getReviewStatsUseCase.execute(anyLong(), anyInt()))
            .thenReturn(new ReviewStatsView(4.0, 0));
        mockMvc.perform(get("/api/v1/hotels/1/reviews/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.averageScore").value(4.0));
    }

    @Test
    void reviewStatsInvalidLimit() throws Exception {
        mockMvc.perform(get("/api/v1/hotels/1/reviews/stats").param("limit", "201"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
}
