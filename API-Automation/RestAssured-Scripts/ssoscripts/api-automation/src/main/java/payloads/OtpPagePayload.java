package payloads;

public class OtpPagePayload {
	
	
	private String otp;
    private String otpFrom;

    // Constructors
    public OtpPagePayload() {}

    public OtpPagePayload(String otp, String otpFrom) {
        this.otp = otp;
        this.otpFrom = otpFrom;
    }

    // Getters and Setters
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getOtpFrom() {
        return otpFrom;
    }

    public void setOtpFrom(String otpFrom) {
        this.otpFrom = otpFrom;
    }
    
    // Override toString for clean logging/reporting
    @Override
    public String toString() {
        return "{otp='" + otp + "', otpFrom='" + otpFrom + "'}";
    }
	

}
