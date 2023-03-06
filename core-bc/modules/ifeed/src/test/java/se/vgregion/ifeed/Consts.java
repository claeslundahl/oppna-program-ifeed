package se.vgregion.ifeed;

public class Consts {

    public static class Prod {
        public static final String clientCertificateFile = "C:\\Users\\clalu4\\Desktop\\Todos\\2023\\01\\ifeed-integration\\ifeed-admin.vgregion.se.cer.pfx";
        public static final String clientCertificatePassword = "Zd4fDg56hG67hF{gftf";
        public static final String address = "https://qa.mtls-api-internal.vgregion.se/proxy-datakatalogen/glossary";
    }

    public static class Test {
        public static final String clientCertificateFile = "C:\\Users\\clalu4\\Desktop\\Todos\\2023\\01\\ifeed-integration\\ifeed-admin-test.vgregion.se.cer.pfx";
        public static final String clientCertificatePassword = "Zd4fDg56hG67hF{gftf";
        public static final String address = "https://qa.mtls-api-internal.vgregion.se/proxy-datakatalogen/glossary";
    }

    public static class Stage {
        public static final String clientCertificateFile = "C:\\Users\\clalu4\\Desktop\\Todos\\2023\\01\\ifeed-integration\\ifeed-admin-stage.vgregion.se.cer.pfx";
        public static final String clientCertificatePassword = "Zd4fDg56hG67hF{gftf";
        public static final String address = "https://qa.mtls-api-internal.vgregion.se/proxy-datakatalogen/glossary";
    }

}
