package com.kaustubh.ecommerce;import java.util.ArrayList;public class ProductCategory {    private String title, img, id, subCategory;    private String address, date;    public ProductCategory() {    }    public String getAddress() {        return address;    }    public void setAddress(String address) {        this.address = address;    }    public String getDate() {        return date;    }    public void setDate(String date) {        this.date = date;    }    public String getName() {        return name;    }    public void setName(String name) {        this.name = name;    }    public ArrayList<ProductCategory> getList() {        return list;    }    public void setList(ArrayList<ProductCategory> list) {        this.list = list;    }    public ProductCategory(String id, String name, String img, String subCategory) {        this.id = id;        this.title = name;        this.img = img;        this.subCategory = subCategory;    }    public ProductCategory(String id, String name, String img, String subCategory, String address, String date) {        this.id = id;        this.title = name;        this.img = img;        this.subCategory = subCategory;        this.address = address;        this.date = date;    }    public String getSubCategory() {        return subCategory;    }    public void setSubCategory(String subCategory) {        this.subCategory = subCategory;    }    public String getTitle() {        return title;    }    public void setTitle(String title) {        this.title = title;    }    public String getImg() {        return img;    }    public void setImg(String img) {        this.img = img;    }    public String getId() {        return id;    }    public void setId(String id) {        this.id = id;    }    public void setProductList(ArrayList<ProductCategory> productList) {        this.list = productList;    }    private String name;    private ArrayList<ProductCategory> list = new ArrayList<ProductCategory>();    public ArrayList<ProductCategory> getProductList() {        return list;    }}