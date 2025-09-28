package payloads;

public class DeleteShopPayload {
	

    private ShopDetails shopDetails;
    private String merchantId;

    public DeleteShopPayload() {}

    public DeleteShopPayload(boolean isDelete, String merchantId) {
        this.shopDetails = new ShopDetails(isDelete);
        this.merchantId = merchantId;
    }

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

    // ---------- Inner Class ----------
    public static class ShopDetails {
        private boolean isDelete;

        public ShopDetails() {}
        public ShopDetails(boolean isDelete) {
            this.isDelete = isDelete;
        }

        public boolean getIsDelete() { return isDelete; }
        public void setIsDelete(boolean isDelete) { this.isDelete = isDelete; }

        @Override
        public String toString() {
            return "{isDelete=" + isDelete + "}";
        }
    }

    @Override
    public String toString() {
        return "{shopDetails=" + shopDetails + ", merchantId='" + merchantId + "'}";
    }
	
	
	

}
