package com.test.amazon.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.amazon.core.entity.Books_Adt;
import com.test.amazon.core.jpa.AmazonItemRepository;
import com.test.amazon.core.jpa.AmazonItemSpecification;

@Service
public class AmazonItemRepositoryService {
	
	@Autowired
	private AmazonItemRepository amazonItemRepository;
	
	@Autowired
	private AmazonItemSpecification specification;
	
	public Books_Adt saveAmazonItemEntity(Books_Adt amazonItemEntity) {
		return amazonItemRepository.saveAndFlush(amazonItemEntity);
	}
	
	public Integer getCountByCategory(String category) {
		List<Books_Adt> booksList = amazonItemRepository.findAll(specification.categoryIs(category));
		if(booksList == null) {
			return 0;
		}else {
			return booksList.size();
		}
	}
}
