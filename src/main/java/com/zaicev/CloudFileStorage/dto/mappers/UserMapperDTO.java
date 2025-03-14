package com.zaicev.CloudFileStorage.dto.mappers;

import com.zaicev.CloudFileStorage.dto.UserDTO;
import com.zaicev.CloudFileStorage.security.models.User;

public class UserMapperDTO implements MapperDTO<UserDTO, User> {

	@Override
	public User getObject(UserDTO dto) {
		User user = new User();
		user.setUsername(dto.getUsername());
		user.setPassword(dto.getPassword());
		return user;
	}

	@Override
	public UserDTO toDTO(User user) {
		UserDTO dto = new UserDTO(user.getUsername(), user.getPassword());
		return dto;
	}
	
}
