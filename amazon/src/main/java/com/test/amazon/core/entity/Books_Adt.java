package com.test.amazon.core.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.Size;

@Entity
public class Books_Adt {

	@Id
	private String asin;
	@Size(max=30)
	private String item;
	@Size(max=30)
	private String category;
	private Integer salesRank;
	private String mediumImage;
	private String author;
	private String binding;
	private String edition;
	private String isbn;
	private String eisbn;
	private String format;
	private String label;
	private String language;
	private String amont;
	private Integer numberofpages;
	private String productGroup;
	private String publisher;
	private Date releaseDate;
	private String title;
	@Size(max=4000)
	private String content;
	
	

	
	 
	
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public Integer getSalesRank() {
		return salesRank;
	}

	public void setSalesRank(Integer salesRank) {
		this.salesRank = salesRank;
	}

	public String getMediumImage() {
		return mediumImage;
	}

	public void setMediumImage(String mediumImage) {
		this.mediumImage = mediumImage;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getBinding() {
		return binding;
	}

	public void setBinding(String binding) {
		this.binding = binding;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getEisbn() {
		return eisbn;
	}

	public void setEisbn(String eisbn) {
		this.eisbn = eisbn;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getAmont() {
		return amont;
	}

	public void setAmont(String amont) {
		this.amont = amont;
	}

	public Integer getNumberofpages() {
		return numberofpages;
	}

	public void setNumberofpages(Integer numberofpages) {
		this.numberofpages = numberofpages;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public byte[] getBookImage() {
		return bookImage;
	}

	public void setBookImage(byte[] bookImage) {
		this.bookImage = bookImage;
	}

	@Lob
	private byte[] bookImage;

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	
}
