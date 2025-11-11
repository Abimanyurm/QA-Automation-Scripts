package payloads;

public class ConfirmPagePayload {
	
	
	private ConfirmPage confirmPage;

    // Constructor
    public ConfirmPagePayload() {}

    public ConfirmPagePayload(ConfirmPage confirmPage) {
        this.confirmPage = confirmPage;
    }

    // Getter and Setter
    public ConfirmPage getConfirmPage() {
        return confirmPage;
    }

    public void setConfirmPage(ConfirmPage confirmPage) {
        this.confirmPage = confirmPage;
    }
    
    @Override
    public String toString() {
        return "{confirmPage=" + (confirmPage != null ? confirmPage.toString() : "null") + "}";
    }

    // ---------- Nested class ----------
    public static class ConfirmPage {
        private boolean termsCondition;

        public ConfirmPage() {}

        public ConfirmPage(boolean termsCondition) {
            this.termsCondition = termsCondition;
        }

        public boolean isTermsCondition() {
            return termsCondition;
        }

        public void setTermsCondition(boolean termsCondition) {
            this.termsCondition = termsCondition;
        }
        
        @Override
        public String toString() {
            return "{termsCondition=" + termsCondition + "}";
    }
	
	
	
	

}}
