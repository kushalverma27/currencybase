/*package com.antuansoft.spring.security;

import java.util.Optional;

import com.antuansoft.mongodb.domain.Campaign;
import com.antuansoft.mongodb.domain.User;
import com.antuansoft.mongodb.repositories.UserRegistrationDao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.repository.CrudRepository;

import com.mongodb.MongoClient;

public class UserRegistrationDaoImpl  implements CrudRepository<User, String>{

	MongoTemplate mongoTemplate;
	MongoOperations mOpr;
	@Override
	public <S extends User> S save(S entity) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <S extends User> Iterable<S> save(Iterable<S> entities) {
		User user1 = new User("test","test","test","test","test",1);
		mOpr.save(user1);
		return entities;	
	}
	@Override
	public User findOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean exists(String id) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Iterable<User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Iterable<User> findAll(Iterable<String> ids) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void delete(User entity) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void delete(Iterable<? extends User> entities) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteAll(Iterable<? extends User> arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deleteById(String arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean existsById(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public Iterable<User> findAllById(Iterable<String> arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Optional<User> findById(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <S extends User> Iterable<S> saveAll(Iterable<S> arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public void save(User user) {
	User user1 = new User("test","test","test","test","test",1);
	mOpr.save(user1);	
	}

}
*/