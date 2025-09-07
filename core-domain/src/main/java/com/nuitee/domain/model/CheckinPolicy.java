package com.nuitee.domain.model;

import java.util.List;

public record CheckinPolicy(String checkinStart,
                            String checkinEnd,
                            String checkout,
                            List<String> instructions,
                            String specialInstructions) {
}
