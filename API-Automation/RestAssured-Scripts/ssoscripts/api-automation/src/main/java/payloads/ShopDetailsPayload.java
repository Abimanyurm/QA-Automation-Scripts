package payloads;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ShopDetailsPayload {

	private ShopDetails shopDetails;
	private String merchantId;
	@JsonIgnore
	public String gmid;

	// getters and setters
	public ShopDetails getShopDetails() {
		return shopDetails;
	}

	public void setShopDetails(ShopDetails shopDetails) {
		this.shopDetails = shopDetails;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	@Override
	public String toString() {
		return "{merchantId='" + merchantId + '\'' + ", shopDetails="
				+ (shopDetails != null ? shopDetails.toString() : "null") + '}';
	}

	// Nested ShopDetails class
	public static class ShopDetails {
		private String location;
		private String address;
		private String area;
		private String city;
		private String state;
		private int postalCode;
		private int noOfPOSDevices;
		private int softPOS;
		private String supervisorNumber;
		private String locationMunicipalityFile;
		private String bizCategoryName;
		private String bizCategoryId;
		private String bussinessAddress;
		private String photoOfThePermisesFile;
		private String signboardFile;
		private String county;
		private String region;
		private BusinessVolume businessVolume;
		private String contactPerson;
		private String contactPersonMobile;
		private String commercialName;
		private String bizWebsite;
		private String business;
		private Boolean transactionRefundAllow;
		private String binSupported;
		private List<String> terminalNumbers;
		private String mcc;
		private String mobile;
		private String phone;
		private Boolean isPg;
		private Boolean seamlessIntegration;
		private String siMid;
		private String lastName;
		private int pobox;
		private String firstName;
		private String email;
		private String entityName;
		private String merchantBusinessNature;
		private List<String> inputs;
		private Double latitude;
		private Double longitude;

		// ✅ Getters and setters
		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getArea() {
			return area;
		}

		public void setArea(String area) {
			this.area = area;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public int getPostalCode() {
			return postalCode;
		}

		public void setPostalCode(int postalCode) {
			this.postalCode = postalCode;
		}

		public int getNoOfPOSDevices() {
			return noOfPOSDevices;
		}

		public void setNoOfPOSDevices(int noOfPOSDevices) {
			this.noOfPOSDevices = noOfPOSDevices;
		}

		public int getSoftPOS() {
			return softPOS;
		}

		public void setSoftPOS(int softPOS) {
			this.softPOS = softPOS;
		}

		public String getSupervisorNumber() {
			return supervisorNumber;
		}

		public void setSupervisorNumber(String supervisorNumber) {
			this.supervisorNumber = supervisorNumber;
		}

		public String getLocationMunicipalityFile() {
			return locationMunicipalityFile;
		}

		public void setLocationMunicipalityFile(String locationMunicipalityFile) {
			this.locationMunicipalityFile = locationMunicipalityFile;
		}

		public String getBizCategoryName() {
			return bizCategoryName;
		}

		public void setBizCategoryName(String bizCategoryName) {
			this.bizCategoryName = bizCategoryName;
		}

		public String getBizCategoryId() {
			return bizCategoryId;
		}

		public void setBizCategoryId(String bizCategoryId) {
			this.bizCategoryId = bizCategoryId;
		}

		public String getBussinessAddress() {
			return bussinessAddress;
		}

		public void setBussinessAddress(String bussinessAddress) {
			this.bussinessAddress = bussinessAddress;
		}

		public String getPhotoOfThePermisesFile() {
			return photoOfThePermisesFile;
		}

		public void setPhotoOfThePermisesFile(String photoOfThePermisesFile) {
			this.photoOfThePermisesFile = photoOfThePermisesFile;
		}

		public String getSignboardFile() {
			return signboardFile;
		}

		public void setSignboardFile(String signboardFile) {
			this.signboardFile = signboardFile;
		}

		public String getCounty() {
			return county;
		}

		public void setCounty(String county) {
			this.county = county;
		}

		public String getRegion() {
			return region;
		}

		public void setRegion(String region) {
			this.region = region;
		}

		public String getContactPerson() {
			return contactPerson;
		}

		public void setContactPerson(String contactPerson) {
			this.contactPerson = contactPerson;
		}

		public String getContactPersonMobile() {
			return contactPersonMobile;
		}

		public void setContactPersonMobile(String contactPersonMobile) {
			this.contactPersonMobile = contactPersonMobile;
		}

		public String getCommercialName() {
			return commercialName;
		}

		public void setCommercialName(String commercialName) {
			this.commercialName = commercialName;
		}

		public String getBizWebsite() {
			return bizWebsite;
		}

		public void setBizWebsite(String bizWebsite) {
			this.bizWebsite = bizWebsite;
		}

		public String getBusiness() {
			return business;
		}

		public void setBusiness(String business) {
			this.business = business;
		}

		public Boolean getTransactionRefundAllow() {
			return transactionRefundAllow;
		}

		public void setTransactionRefundAllow(Boolean transactionRefundAllow) {
			this.transactionRefundAllow = transactionRefundAllow;
		}

		public String getBinSupported() {
			return binSupported;
		}

		public void setBinSupported(String binSupported) {
			this.binSupported = binSupported;
		}

		public List<String> getTerminalNumbers() {
			return terminalNumbers;
		}

		public void setTerminalNumbers(List<String> terminalNumbers) {
			this.terminalNumbers = terminalNumbers;
		}

		public String getMcc() {
			return mcc;
		}

		public void setMcc(String mcc) {
			this.mcc = mcc;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public Boolean getIsPg() {
			return isPg;
		}

		public void setIsPg(Boolean isPg) {
			this.isPg = isPg;
		}

		public Boolean getSeamlessIntegration() {
			return seamlessIntegration;
		}

		public void setSeamlessIntegration(Boolean seamlessIntegration) {
			this.seamlessIntegration = seamlessIntegration;
		}

		public String getSiMid() {
			return siMid;
		}

		public void setSiMid(String siMid) {
			this.siMid = siMid;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public int getPobox() {
			return pobox;
		}

		public void setPobox(int pobox) {
			this.pobox = pobox;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getEntityName() {
			return entityName;
		}

		public void setEntityName(String entityName) {
			this.entityName = entityName;
		}

		public String getMerchantBusinessNature() {
			return merchantBusinessNature;
		}

		public void setMerchantBusinessNature(String merchantBusinessNature) {
			this.merchantBusinessNature = merchantBusinessNature;
		}

		public List<String> getInputs() {
			return inputs;
		}

		public void setInputs(List<String> inputs) {
			this.inputs = inputs;
		}

		public Double getLatitude() {
			return latitude;
		}

		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}

		public Double getLongitude() {
			return longitude;
		}

		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}

		public BusinessVolume getBusinessVolume() {
			return businessVolume;
		}

		public void setBusinessVolume(BusinessVolume businessVolume) {
			this.businessVolume = businessVolume;
		}

		@Override
		public String toString() {
			return "{location='" + location + '\'' + ", address='" + address + '\'' + ", area='" + area + '\''
					+ ", city='" + city + '\'' + ", state='" + state + '\'' + ", postalCode=" + postalCode
					+ ", noOfPOSDevices=" + noOfPOSDevices + ", softPOS=" + softPOS + ", supervisorNumber='"
					+ supervisorNumber + '\'' + ", terminalNumbers=" + terminalNumbers + ", businessVolume="
					+ (businessVolume != null ? businessVolume.toString() : "null") + '}';
		}

		// Nested BusinessVolume class
		public static class BusinessVolume {
			private int min;
			private int max;

			public int getMin() {
				return min;
			}

			public void setMin(int min) {
				this.min = min;
			}

			public int getMax() {
				return max;
			}

			public void setMax(int max) {
				this.max = max;
			}

			@Override
			public String toString() {
				return "{min=" + min + ", max=" + max + '}';
			}
		}

	}

	// getters/setters
}
