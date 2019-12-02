package com.xbboom.bean;

import java.util.Date;

public class Orders {
	int id;
	String ordernum;
	String goods;
	double price;
	double pei;
	double canhe;
	String dates;
	int status;
	String addr;
	String phone;
	String name;
	int types;
	String openid;
	String bz;
	
	public String getBz() {
		return bz;
	}
	public void setBz(String bz) {
		this.bz = bz;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public int getTypes() {
		return types;
	}
	public void setTypes(int types) {
		this.types = types;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOrdernum() {
		return ordernum;
	}
	public void setOrdernum(String ordernum) {
		this.ordernum = ordernum;
	}
	public String getGoods() {
		return goods;
	}
	public void setGoods(String goods) {
		this.goods = goods;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getPei() {
		return pei;
	}
	public void setPei(double pei) {
		this.pei = pei;
	}
	public double getCanhe() {
		return canhe;
	}
	public void setCanhe(double canhe) {
		this.canhe = canhe;
	}
	public String getDates() {
		return dates;
	}
	public void setDates(String dates) {
		this.dates = dates;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "Orders [id=" + id + ", ordernum=" + ordernum + ", goods=" + goods + ", price=" + price + ", pei=" + pei
				+ ", canhe=" + canhe + ", dates=" + dates + ", status=" + status + ", addr=" + addr + ", phone=" + phone
				+ ", name=" + name + ", types=" + types + "]";
	}
	
	
}
