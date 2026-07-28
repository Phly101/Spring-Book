package com.phly101.library.dto;

import java.util.Map;

public record ValidationErrorResponseRecord(String errorCode, Map<String, String> errorMessages) {
}