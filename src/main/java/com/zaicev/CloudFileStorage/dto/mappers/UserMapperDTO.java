package com.zaicev.CloudFileStorage.dto.mappers;

import com.zaicev.CloudFileStorage.dto.UserRequestDTO;
import com.zaicev.CloudFileStorage.dto.UserResponseDTO;
import com.zaicev.CloudFileStorage.security.models.User;

public class UserMapperDTO {

	public User getObjectFromRequestDTO(UserRequestDTO dto) {
		User user = new User();
		user.setUsername(dto.getUsername());
		user.setPassword(dto.getPassword());
		return user;
	}

	public UserResponseDTO getResponseDTOFromObject(User user) {
		UserResponseDTO dto = new UserResponseDTO(user.getUsername());
		return dto;
	}

}
