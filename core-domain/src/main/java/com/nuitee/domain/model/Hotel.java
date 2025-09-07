package com.nuitee.domain.model;

import java.util.List;
import java.util.Objects;

public record Hotel(long id, long chainId, String name, double rating, int stars, Coordinates coordinates, Contact contact, Address address,
                    CheckinPolicy checkinPolicy, String mainImageTh, Boolean childAllowed, Boolean petsAllowed) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Hotel hotel)) {
            return false;
        }
        return id == hotel.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
