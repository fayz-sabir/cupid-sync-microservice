package com.nuitee.app.support;

import jakarta.validation.constraints.NotEmpty;

import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public class IngestionSyncProperties {

    @NotEmpty(message = "Hotel IDs must not be empty")
    private List<Long> hotelIds;

    public List<Long> getHotelIds() {
        return hotelIds;
    }

    public void setHotelIds(List<Long> hotelIds) {
        this.hotelIds = hotelIds;
    }
}
