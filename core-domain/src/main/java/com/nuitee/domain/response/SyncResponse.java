package com.nuitee.domain.response;

import com.nuitee.domain.i18n.Lang;

public record SyncResponse(String message, long hotelId, Lang lang) {
}
