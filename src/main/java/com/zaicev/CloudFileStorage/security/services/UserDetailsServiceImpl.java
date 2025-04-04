package com.zaicev.CloudFileStorage.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zaicev.CloudFileStorage.security.models.User;
import com.zaicev.CloudFileStorage.security.models.UserDetailsImpl;
import com.zaicev.CloudFileStorage.security.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
		return new UserDetailsImpl(user.getUsername(), user.getPassword(), user.getId(), user.getRoles());
	}

}
