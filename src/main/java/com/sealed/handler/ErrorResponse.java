package com.sealed.handler;

public record ErrorResponse(String status, String message, Integer code) {
}
