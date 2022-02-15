package com.web.dao;

import org.springframework.data.repository.CrudRepository;

import com.web.model.User;

public interface UserDao extends CrudRepository<User, Integer>{

	public User findByName(String name);
	public User findByPassword(String password);
	
}
