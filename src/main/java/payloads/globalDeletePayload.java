package payloads;

public class globalDeletePayload {
	
	
    private String type;
    private String cifNumber;

    // Constructors
    public globalDeletePayload() {}

    public globalDeletePayload(String type, String cifNumber) {
        this.type = type;
        this.cifNumber = cifNumber;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCifNumber() {
        return cifNumber;
    }

    public void setCifNumber(String cifNumber) {
        this.cifNumber = cifNumber;
    }

    // Override toString for clean logging
    @Override
    public String toString() {
        return "{type=" + type + ", cifNumber=" + cifNumber + "}";
    }
}
	


