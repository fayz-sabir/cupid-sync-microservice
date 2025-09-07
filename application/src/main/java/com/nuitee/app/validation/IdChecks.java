package com.nuitee.app.validation;

import jakarta.validation.constraints.Positive;

public interface IdChecks {
    @Positive(message = "hotelId must be positive")
    long getHotelId();
}