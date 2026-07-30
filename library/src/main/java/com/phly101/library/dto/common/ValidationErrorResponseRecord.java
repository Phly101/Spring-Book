package com.phly101.library.dto.common;

import java.util.Map;

public record ValidationErrorResponseRecord(String errorCode, Map<String, String> errorMessages) {
}