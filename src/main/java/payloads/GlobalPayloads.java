package payloads;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GlobalPayloads {

	private String accountNo;
	private String cifNumber;

	@JsonIgnore // kept in Java, excluded from JSON body
	private String groupMerchantId;

	// Getters
	public String getAccountNo() {
		return accountNo;
	}

	public String getCifNumber() {
		return cifNumber;
	}

	public String getGroupMerchantId() {
		return groupMerchantId;
	}

	// Setters
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public void setCifNumber(String cifNumber) {
		this.cifNumber = cifNumber;
	}

	public void setGroupMerchantId(String groupMerchantId) {
		this.groupMerchantId = groupMerchantId;
	}
	
    // Clean toString for reports (exclude groupMerchantId)
    @Override
    public String toString() {
        return "{accountNo='" + accountNo + '\'' +
               ", cifNumber='" + cifNumber + '\'' +
               '}';
    }
	
	
}
