package payloads;

public class SubmitPagePayload {
	
	
	private boolean submitPage;

    // Constructors
    public SubmitPagePayload() {}

    public SubmitPagePayload(boolean submitPage) {
        this.submitPage = submitPage;
    }

    // Getter and Setter
    public boolean isSubmitPage() {
        return submitPage;
    }

    public void setSubmitPage(boolean submitPage) {
        this.submitPage = submitPage;
    }
    
    // Override toString for clean logging
    @Override
    public String toString() {
        return "{submitPage=" + submitPage + "}";
    }
	

}
