package com.threemin.model;

import java.util.List;

public class ImageModel {
	private int id;
	private String name;
	private String description;
	private String thumb;
	private String square;
	private String medium;
	private String origin;
	private String url;
	List<Integer> dimensions;
    public List<Integer> getDimensions() {
        return dimensions;
    }
    public void setDimensions(List<Integer> dimensions) {
        this.dimensions = dimensions;
    }
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getThumb() {
		return thumb;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
	}
	public String getSquare() {
		return square;
	}
	public void setSquare(String square) {
		this.square = square;
	}
	public String getMedium() {
		return medium;
	}
	public void setMedium(String medium) {
		this.medium = medium;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
	//constants for editing product
	public static final int TYPE_EDIT_PRODUCT_NO_CHANGE = 0;
	public static final int TYPE_EDIT_PRODUCT_CREATE = 1;
	public static final int TYPE_EDIT_PRODUCT_UPDATE = 2;
	public static final int TYPE_EDIT_PRODUCT_DELETE = 3;
	
	private int typeEditProduct;
	
	public ImageModel() {
	    super();
	    this.typeEditProduct = TYPE_EDIT_PRODUCT_CREATE;
	}
	
	public void setTypeEditProduct(int type) {
	    this.typeEditProduct = type;
	}
	
	public int getTypeEditProduct() {
	    return this.typeEditProduct;
	}
	
}
