package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum DeliveryErrorCode implements ErrorCode {
	ORDER_NOT_FOUND(404, "ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."),
	DELIVERY_NOT_FOUND(404, "DELIVERY_NOT_FOUND", "배송 정보를 찾을 수 없습니다.");

	private final int status;
	private final String code;
	private final String message;

	DeliveryErrorCode(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	@Override
	public int getStatus() { return status; }
	@Override
	public String getCode() { return code; }
	@Override
	public String getMessage() { return message; }
}
