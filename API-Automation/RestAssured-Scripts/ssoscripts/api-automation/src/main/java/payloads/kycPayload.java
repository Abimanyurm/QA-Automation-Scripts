package payloads;

public class kycPayload {


    private Kyc kyc;   // <-- wrapper field

    public kycPayload() {
    }

    // Keep the same constructor signature your test already uses
    public kycPayload(String commerceFile,
                      String commerceNumber,
                      int numberOfMerchantLocations,
                      String passportFrontFile,
                      String passportNumber) {

        this.kyc = new Kyc(commerceFile,
                           commerceNumber,
                           numberOfMerchantLocations,
                           passportFrontFile,
                           passportNumber);
    }

    public Kyc getKyc() {
        return kyc;
    }

    public void setKyc(Kyc kyc) {
        this.kyc = kyc;
    }

    // ---------- Inner class represents the actual KYC object ----------
    public static class Kyc {
        private String commerceFile;
        private String commerceNumber;
        private int numberOfMerchantLocations;
        private String passportFrontFile;
        private String passportNumber;

        public Kyc() {
        }

        public Kyc(String commerceFile, String commerceNumber,
                   int numberOfMerchantLocations,
                   String passportFrontFile,
                   String passportNumber) {
            this.commerceFile = commerceFile;
            this.commerceNumber = commerceNumber;
            this.numberOfMerchantLocations = numberOfMerchantLocations;
            this.passportFrontFile = passportFrontFile;
            this.passportNumber = passportNumber;
        }

        public String getCommerceFile() { return commerceFile; }
        public void setCommerceFile(String commerceFile) { this.commerceFile = commerceFile; }

        public String getCommerceNumber() { return commerceNumber; }
        public void setCommerceNumber(String commerceNumber) { this.commerceNumber = commerceNumber; }

        public int getNumberOfMerchantLocations() { return numberOfMerchantLocations; }
        public void setNumberOfMerchantLocations(int numberOfMerchantLocations) {
            this.numberOfMerchantLocations = numberOfMerchantLocations;
        }

        public String getPassportFrontFile() { return passportFrontFile; }
        public void setPassportFrontFile(String passportFrontFile) { this.passportFrontFile = passportFrontFile; }

        public String getPassportNumber() { return passportNumber; }
        public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }
        
        
        
        @Override
        public String toString() {
            return "{commerceFile='" + commerceFile + '\'' +
                    ", commerceNumber='" + commerceNumber + '\'' +
                    ", numberOfMerchantLocations=" + numberOfMerchantLocations +
                    ", passportFrontFile='" + passportFrontFile + '\'' +
                    ", passportNumber='" + passportNumber + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "{kyc=" + (kyc != null ? kyc.toString() : "null") + "}";
    }
    }

