package com.zaicev.CloudFileStorage.security.exceptions;

public class UsernameAlreadyTakenException extends RuntimeException{
	public UsernameAlreadyTakenException(String username) {
		super("{0} is already taken".formatted(username));
	}
}
