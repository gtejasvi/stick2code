package com.test.amazon.core;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.test.amazon.core.entity.Books_Adt;
import com.test.amazon.core.xsd.ItemSearchResponse;

@Service
public class AmazonFetchService {
	
	
	@Autowired
	//@Qualifier(value="restJaxbTemplate")
	@Qualifier(value="amazonRestTemplate")
	private RestTemplate restTemplate;

	
	@Autowired
	AmazonItemRepositoryService repositoryService;
	
	public static final Logger logger = LoggerFactory.getLogger(AmazonFetchService.class);
	
	public int saveAmazonData(int pageIndex,String searchIndex,String category,String keyWord,int maxCount,Set<String> asinSet) throws Exception{

		int categoryCount = repositoryService.getCountByCategory(category);
		if(categoryCount >= maxCount) {
			return categoryCount;
		}

		
		logger.info("PAGEINDEX["+pageIndex+"],CATEGORY["+category+"],KEYWORD["+keyWord+"]::");
		logger.debug("Entry::category::["+category+"],Count::["+categoryCount+"]ASIN CNT["+asinSet.size()+"]");
		ItemSearchResponse response = getAmazonData(pageIndex,searchIndex,keyWord);
		
		logger.debug("response::"+response);
		
		ItemSearchResponse.Items items = response.getItems();
		
		
		for(ItemSearchResponse.Items.Item item : items.getItem()) {
			Books_Adt amazonItemEntity = new Books_Adt();
			String asin = item.getASIN();
			amazonItemEntity.setAsin(asin);
			String amount = null;
			if(null != item.getItemAttributes().getListPrice()) {
				BigInteger amountI = item.getItemAttributes().getListPrice().getAmount();
				if(null != amountI) {
					amount = ""+ amountI.intValue();
				}
			}

			amazonItemEntity.setAmont(amount);
			if(item.getItemAttributes().getAuthor() == null) {
				continue;
				
			} else if(item.getItemAttributes().getAuthor().size() == 0) {
				continue;
				
			}
			amazonItemEntity.setAuthor(item.getItemAttributes().getAuthor().get(0));
			
			amazonItemEntity.setBinding(item.getItemAttributes().getBinding());
			
			amazonItemEntity.setEdition(item.getItemAttributes().getEdition());
			String eisbnString = null;
			if(null != item.getItemAttributes().getEISBN()) {
				eisbnString = item.getItemAttributes().getEISBN().toString();
			}
			amazonItemEntity.setEisbn(eisbnString);
			amazonItemEntity.setFormat(item.getItemAttributes().getFormat());
			String isbnString = null;
			if(null == item.getItemAttributes().getISBN()) {
				continue;
			}

			amazonItemEntity.setIsbn(item.getItemAttributes().getISBN().toString());
			amazonItemEntity.setItem("");
			amazonItemEntity.setLabel(item.getItemAttributes().getLabel());
			amazonItemEntity.setLanguage(item.getItemAttributes().getLanguages().getLanguage().get(0).getName());
			amazonItemEntity.setMediumImage(item.getMediumImage().getURL());
			if(item.getItemAttributes().getNumberOfPages() == null) {
				continue;
			}
			amazonItemEntity.setNumberofpages(item.getItemAttributes().getNumberOfPages().intValue());
			amazonItemEntity.setProductGroup(item.getItemAttributes().getProductGroup());
			amazonItemEntity.setPublisher(item.getItemAttributes().getPublisher());
			Date pubDate = null;
			try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			String dateOfPublication = item.getItemAttributes().getPublicationDate();
			pubDate = sdf.parse(dateOfPublication);
			}catch(Exception e) {
				continue;
			}
			amazonItemEntity.setReleaseDate(pubDate);
			int salesRank = 0;
			if(null != item.getSalesRank()) {
				salesRank = item.getSalesRank().intValue();
			}
			amazonItemEntity.setSalesRank(salesRank);
			amazonItemEntity.setTitle(item.getItemAttributes().getTitle());
			
			//byte[] image = getImage(item.getMediumImage().getURL());
			//amazonItemEntity.setBookImage(image);
			//amazonItemEntity.setContent(item.getEditorialReviews().getEditorialReview().get(0).getContent());
			amazonItemEntity.setCategory(category);
			logger.debug("category::"+keyWord+",asin::"+asin);
			repositoryService.saveAmazonItemEntity(amazonItemEntity);
			asinSet.add(asin);
		
			categoryCount = repositoryService.getCountByCategory(category);
			if(categoryCount >= maxCount) {
				break;
			}

		}
		logger.debug("category::"+category+",Count::"+categoryCount);
		return categoryCount;
		
		
	}
	
	public byte[] getImage(String imageUrl) throws IOException {
		URL url = new URL(imageUrl);
		BufferedImage image = null;
		image = ImageIO.read(url);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg",bos);
		image = ImageIO.read(url);
		ImageIO.write(image, "jpg",bos);
		
		return bos.toByteArray();

	}
	
	public ItemSearchResponse getAmazonData(int pageIndex,String searchIndex,String keyWord) throws Exception {
		String signRequest = formRequest(pageIndex,searchIndex,keyWord);
		System.out.println("RESET["+signRequest+"]");
		
		URL url = new URL(signRequest);
		URI uri = url.toURI();
		
	
		ItemSearchResponse response = restTemplate.getForObject(uri, ItemSearchResponse.class);
		//restTemplate.getForObject(uri, JAXBElement.class);
		System.out.println(signRequest);
		return response;
	}
	
	
    private static final String AWS_ACCESS_KEY_ID = "<Update>1";
    private static final String AWS_SECRET_KEY = "<Update>1";
    private static final String ENDPOINT = "webservices.amazon.com";

	
	private String formRequest(int pageIndex,String searchIndex,String keyWord) {
        SignedRequestsHelper helper;
        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        String requestUrl = null;
        String title = null;

        /* The helper can sign requests in two forms - map form and string form */
        
        /*
         * Here is an example in map form, where the request parameters are stored in a map.
         */
        System.out.println("Map form example:");
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("ItemPage", (pageIndex)+"");
        params.put("AssociateTag", "bibliofile-21");
        params.put("Keywords", keyWord);
        params.put("SearchIndex", searchIndex);
        //params.put("Signature", "n6hv5aee825JWi5EuPlhnDwjE8hLrN6/ZodwsWPLB0Y=");
        params.put("Operation", "ItemSearch");
        params.put("ResponseGroup", "Medium");

        requestUrl = helper.sign(params);
        System.out.println("Signed Request is \"" + requestUrl + "\"");
        return requestUrl;
        

	}

}
