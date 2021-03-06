package com.antuansoft.mongodb.repositories;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.CrudRepository;

import com.antuansoft.mongodb.domain.Campaign;


public interface CampaignRepositoryDao extends CrudRepository<Campaign, String> {

	@Cacheable(value="history")
	Iterable<Campaign> findAll();
	
	
	
}