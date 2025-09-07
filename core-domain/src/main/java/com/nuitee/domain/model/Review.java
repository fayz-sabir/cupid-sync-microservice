package com.nuitee.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.nuitee.domain.i18n.Lang;

public record Review(long id, long hotelId, long externalHotelId, int score, String source, LocalDateTime date, String type, Lang language, String pros,
                     String cons, String authorName, String country, String headline) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Review review)) {
            return false;
        }
        return id == review.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
