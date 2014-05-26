package com.test.amazon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.test.amazon.core.AmazonFetchService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class AmazonTest {

	@Autowired
	AmazonFetchService amazonService;

	@Test
	public void test() throws Exception {

		Map<String, Integer> categoryMap = new HashMap<String, Integer>();
		Map<String, List<String>> subCategoryMap = new HashMap<String, List<String>>();

		String mainCategoryString = "Technical";
		categoryMap.put(mainCategoryString, 20);
		List<String> subCatList = new ArrayList<String>();
		subCatList.add("java");
		subCatList.add("digital");
		subCatList.add("programming");
		subCatList.add("database");
		subCategoryMap.put(mainCategoryString, subCatList);

		mainCategoryString = "Travel";
		categoryMap.put(mainCategoryString, 20);
		subCatList = new ArrayList<String>();
		subCatList.add("Africa");
		subCatList.add("india");
		subCatList.add("Travel");
		subCatList.add("Travel Guides");
		subCategoryMap.put(mainCategoryString, subCatList);

		mainCategoryString = "Sports";
		categoryMap.put(mainCategoryString, 20);
		subCatList = new ArrayList<String>();
		subCatList.add("Basketball");
		subCatList.add("baseball");
		subCatList.add("Cricket");
		subCatList.add("Tennis");
		subCategoryMap.put(mainCategoryString, subCatList);

		mainCategoryString = "Humour";
		categoryMap.put(mainCategoryString, 10);
		subCatList = new ArrayList<String>();
		subCatList.add("Comics");
		subCatList.add("Anecdotes");
		subCatList.add("Cartoons");
		subCatList.add("jokes");
		subCategoryMap.put(mainCategoryString, subCatList);

		mainCategoryString = "Food";
		categoryMap.put(mainCategoryString, 10);
		subCatList = new ArrayList<String>();
		subCatList.add("Cooking");
		subCatList.add("beverages");
		subCatList.add("Cuisine");
		subCategoryMap.put(mainCategoryString, subCatList);

		mainCategoryString = "Art & Photography";
		categoryMap.put(mainCategoryString, 15);
		subCatList = new ArrayList<String>();
		subCatList.add("Art");
		subCatList.add("Photography");
		subCategoryMap.put(mainCategoryString, subCatList);

		mainCategoryString = "Thriller";
		categoryMap.put(mainCategoryString, 20);
		subCatList = new ArrayList<String>();
		subCatList.add("Mystery");
		subCatList.add("Crime");
		subCatList.add("Thriller");
		subCategoryMap.put(mainCategoryString, subCatList);

		Set<String> asinSet = new HashSet<String>();
		for (int i = 1; i <= 10; i++) {
			for (String category : categoryMap.keySet()) {
				int categoryCount = 0;
				int categoryMaxCount = categoryMap.get(category);
				subCatList = subCategoryMap.get(category);
				for (String keyWord : subCatList) {
					if (categoryCount >= categoryMaxCount) {
						break;
					}
					categoryCount = amazonService.saveAmazonData(i, "Books", category, keyWord,
							categoryMaxCount,asinSet);

				}

			}

		}
	}

}
