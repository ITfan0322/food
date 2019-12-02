package com.xbboom.bean;

import java.util.Date;

public class Yorder {
	int id;
	String ordernum;
	String addrname;
	String addrphone;
	String addr;
	String name;
	String bz;
	String status;
	Date dates;
	String goods;
	String tips;
	double price;
	int num;
	double pei;
	double canhe;
	String openid;
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
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
	public String getAddrname() {
		return addrname;
	}
	public void setAddrname(String addrname) {
		this.addrname = addrname;
	}
	public String getAddrphone() {
		return addrphone;
	}
	public void setAddrphone(String addrphone) {
		this.addrphone = addrphone;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBz() {
		return bz;
	}
	public void setBz(String bz) {
		this.bz = bz;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getDates() {
		return dates;
	}
	public void setDates(Date dates) {
		this.dates = dates;
	}
	public String getGoods() {
		return goods;
	}
	public void setGoods(String goods) {
		this.goods = goods;
	}
	public String getTips() {
		return tips;
	}
	public void setTips(String tips) {
		this.tips = tips;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
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
	@Override
	public String toString() {
		return "Yorder [id=" + id + ", ordernum=" + ordernum + ", addrname=" + addrname + ", addrphone=" + addrphone
				+ ", addr=" + addr + ", name=" + name + ", bz=" + bz + ", status=" + status + ", dates=" + dates
				+ ", goods=" + goods + ", tips=" + tips + ", price=" + price + ", num=" + num + ", pei=" + pei
				+ ", canhe=" + canhe + "]";
	}
	
}
