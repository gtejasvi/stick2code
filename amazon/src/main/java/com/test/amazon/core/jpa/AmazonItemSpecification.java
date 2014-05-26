package com.test.amazon.core.jpa;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.test.amazon.core.entity.Books_Adt;

@Component
public class AmazonItemSpecification {

	public Specification<Books_Adt> categoryIs(final String category) {
		return new Specification<Books_Adt>() {
			@Override
			public Predicate toPredicate(Root<Books_Adt> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				return cb.equal(root.<String> get("category"), category);
			}
		};
	}
}
