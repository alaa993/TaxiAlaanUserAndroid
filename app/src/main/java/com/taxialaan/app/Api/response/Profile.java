package com.taxialaan.app.Api.response;

import com.google.gson.annotations.SerializedName;

public class Profile{

	@SerializedName("stripe_cust_id")
	private String stripeCustId;

	@SerializedName("payment_mode")
	private String paymentMode;

	@SerializedName("device_id")
	private String deviceId;

	@SerializedName("latitude")
	private String latitude;

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("rating")
	private String rating;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("wallet_balance")
	private int walletBalance;

	@SerializedName("device_type")
	private String deviceType;

	@SerializedName("otp")
	private int otp;

	@SerializedName("picture")
	private String picture;

	@SerializedName("wallet_id")
	private String walletId;

	@SerializedName("balance")
	private String balance;

	@SerializedName("social_unique_id")
	private String socialUniqueId;

	@SerializedName("sos")
	private String sos;

	@SerializedName("device_token")
	private String deviceToken;

	@SerializedName("currency")
	private String currency;

	@SerializedName("id")
	private int id;

	@SerializedName("lang")
	private String lang;

	@SerializedName("first_name")
	private String firstName;

	@SerializedName("login_by")
	private String loginBy;

	@SerializedName("email")
	private String email;

	@SerializedName("longitude")
	private String longitude;

	public String getStripeCustId() {
		return stripeCustId;
	}

	public void setStripeCustId(String stripeCustId) {
		this.stripeCustId = stripeCustId;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getWalletBalance() {
		return walletBalance;
	}

	public void setWalletBalance(int walletBalance) {
		this.walletBalance = walletBalance;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public int getOtp() {
		return otp;
	}

	public void setOtp(int otp) {
		this.otp = otp;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getSocialUniqueId() {
		return socialUniqueId;
	}

	public void setSocialUniqueId(String socialUniqueId) {
		this.socialUniqueId = socialUniqueId;
	}

	public String getSos() {
		return sos;
	}

	public void setSos(String sos) {
		this.sos = sos;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLoginBy() {
		return loginBy;
	}

	public void setLoginBy(String loginBy) {
		this.loginBy = loginBy;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}