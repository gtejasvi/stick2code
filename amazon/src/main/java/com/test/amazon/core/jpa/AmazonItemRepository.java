package com.test.amazon.core.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.test.amazon.core.entity.Books_Adt;

public interface AmazonItemRepository extends JpaRepository<Books_Adt, Long>,JpaSpecificationExecutor<Books_Adt>{
	
}
