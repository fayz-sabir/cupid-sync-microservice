package com.nuitee.webapplication.controller.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.nuitee.app.usecase.FullSyncUseCase;
import com.nuitee.app.usecase.GetHotelViewUseCase;
import com.nuitee.app.usecase.GetReviewsUseCase;
import com.nuitee.app.usecase.GetReviewStatsUseCase;
import com.nuitee.app.usecase.SyncHotelUseCase;
import com.nuitee.app.usecase.SyncReviewsUseCase;
import com.nuitee.app.validation.ValidLang;
import com.nuitee.domain.i18n.Lang;
import com.nuitee.domain.response.FullSyncResponse;
import com.nuitee.domain.response.ReviewsSyncResponse;
import com.nuitee.domain.response.SyncResponse;
import com.nuitee.domain.view.HotelView;
import com.nuitee.domain.view.ReviewView;
import com.nuitee.domain.view.ReviewStatsView;

import io.micrometer.core.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/hotels")
@Validated
@Tag(name = "Hotels API v1", description = "Operations related to hotels")
public class HotelControllerV1 {

    private final GetHotelViewUseCase getHotelViewUseCase;
    private final SyncHotelUseCase syncHotel;
    private final SyncReviewsUseCase syncReviews;
    private final GetReviewsUseCase getReviews;
    private final GetReviewStatsUseCase getReviewStats;
    private final FullSyncUseCase fullSyncUseCase;
    private static final Logger LOGGER = LoggerFactory.getLogger(HotelControllerV1.class);

    public HotelControllerV1(GetHotelViewUseCase getHotelViewUseCase,
                             SyncHotelUseCase syncHotel,
                             SyncReviewsUseCase syncReviews,
                             GetReviewsUseCase getReviews,
                             GetReviewStatsUseCase getReviewStats,
                             FullSyncUseCase fullSyncUseCase) {
        this.getHotelViewUseCase = getHotelViewUseCase;
        this.syncHotel = syncHotel;
        this.getReviews = getReviews;
        this.syncReviews = syncReviews;
        this.getReviewStats = getReviewStats;
        this.fullSyncUseCase = fullSyncUseCase;
    }

    @Operation(summary = "Retrieve a hotel", description = "Returns detailed information about a hotel")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hotel details retrieved",
            content = @Content(schema = @Schema(implementation = HotelView.class)))
    })
    @GetMapping("/{id}")
    @Timed(value = "controller.hotel.view")
    public HotelView view(
        @Parameter(description = "Hotel identifier", example = "123")
        @PathVariable("id") @Positive long id,
        @Parameter(description = "Two-letter language code", example = "en")
        @RequestParam(value = "lang", required = false) @ValidLang String lang,
        @Parameter(description = "Fallback language from the Accept-Language header", example = "en-US")
        @RequestHeader(value = "Accept-Language", required = false) String acceptLang,
        @Parameter(description = "Whether to include reviews in the response", example = "true")
        @RequestParam(value = "includeReviews", defaultValue = "false") boolean includeReviews,
        @Parameter(description = "Maximum number of reviews to include", example = "10")
        @RequestParam(value = "reviewLimit", defaultValue = "10") @Min(1) @Max(200) int reviewLimit) {
        String effectiveLang = lang;
        if (effectiveLang == null || effectiveLang.isBlank()) {
            if (acceptLang != null && !acceptLang.isBlank()) {
                effectiveLang = acceptLang.substring(0, 2);
            } else {
                effectiveLang = "en";
            }
        }
        LOGGER.info("Hotel view requested id={} lang={} includeReviews={} reviewLimit={}", id, effectiveLang, includeReviews, reviewLimit);
        return getHotelViewUseCase.execute(id, effectiveLang, includeReviews, reviewLimit);
    }

    @Operation(summary = "Synchronize a hotel", description = "Triggers a synchronization of a hotel's data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hotel synchronization completed",
            content = @Content(schema = @Schema(implementation = SyncResponse.class)))
    })
    @PostMapping("/{id}/sync")
    @Timed(value = "controller.hotel.sync")
    public ResponseEntity<SyncResponse> sync(
        @Parameter(description = "Hotel identifier", example = "123")
        @PathVariable("id") @Positive long id,
        @Parameter(description = "Language to warm the cache with", example = "en")
        @RequestParam(value = "warmLang", required = false, defaultValue = "en") @ValidLang String warmLang) {
        LOGGER.info("Hotel sync requested id={} warmLang={}", id, warmLang);
        syncHotel.execute(id, warmLang);
        return ResponseEntity.ok(new SyncResponse("Hotel sync completed", id, Lang.fromCode(warmLang)));
    }

    @Operation(summary = "Synchronize hotel reviews", description = "Triggers synchronization of reviews for a hotel")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hotel reviews synchronization completed",
            content = @Content(schema = @Schema(implementation = SyncResponse.class)))
    })
    @PostMapping("/{id}/reviews/sync")
    @Timed(value = "controller.hotel.reviews.sync")
    public ResponseEntity<ReviewsSyncResponse> syncReviews(
        @Parameter(description = "Hotel identifier", example = "123")
        @PathVariable("id") @Positive long id,
        @Parameter(description = "Maximum number of reviews to synchronize", example = "10")
        @RequestParam(value = "limit", required = false, defaultValue = "10") @Min(1) @Max(200) int limit) {
        LOGGER.info("Hotel reviews sync requested id={}", id);
        syncReviews.execute(id, limit);
        return ResponseEntity.ok(new ReviewsSyncResponse("Hotel reviews sync completed", id, limit));
    }

    @Operation(summary = "Synchronize all hotels", description = "Triggers synchronization of all hotels")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Full hotels sync completed")
    })
    @PostMapping("/sync-all")
    @Timed(value = "controller.hotel.sync.all")
    public ResponseEntity<FullSyncResponse> syncAll() {
        LOGGER.info("Full hotels sync requested");
        fullSyncUseCase.execute();
        return ResponseEntity.ok(new FullSyncResponse("Full hotels sync completed", List.of(Lang.FR, Lang.ES)));
    }

    @Operation(summary = "List hotel reviews", description = "Returns a list of reviews for a hotel")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hotel reviews retrieved",
            content = @Content(schema = @Schema(implementation = ReviewView.class)))
    })
    @GetMapping("/{id}/reviews")
    @Timed(value = "controller.hotel.reviews")
    public List<ReviewView> reviews(
        @Parameter(description = "Hotel identifier", example = "123")
        @PathVariable("id") @Positive long id,
        @Parameter(description = "Maximum number of reviews to return", example = "10")
        @RequestParam(value = "limit", defaultValue = "10") @Min(1) @Max(200) int limit) {
        LOGGER.info("Hotel reviews requested id={} limit={}", id, limit);
        return getReviews.execute(id, limit);
    }

    @Operation(summary = "Get hotel review statistics", description = "Returns aggregated review statistics for a hotel")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Hotel review statistics retrieved",
            content = @Content(schema = @Schema(implementation = ReviewStatsView.class)))
    })
    @GetMapping("/{id}/reviews/stats")
    @Timed(value = "controller.hotel.reviews.stats")
    public ReviewStatsView reviewStats(
        @Parameter(description = "Hotel identifier", example = "123")
        @PathVariable("id") @Positive long id,
        @Parameter(description = "Maximum number of reviews to analyze", example = "10")
        @RequestParam(value = "limit", defaultValue = "10") @Min(1) @Max(200) int limit) {
        LOGGER.info("Hotel review stats requested id={} limit={}", id, limit);
        return getReviewStats.execute(id, limit);
    }
}
