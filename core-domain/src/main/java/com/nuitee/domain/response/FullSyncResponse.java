package com.nuitee.domain.response;

import java.util.List;

import com.nuitee.domain.i18n.Lang;

public record FullSyncResponse(String message, List<Lang> langs) {
}
