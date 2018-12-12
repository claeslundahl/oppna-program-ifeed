package se.vgregion.ifeed.service.solr.client;

public class Result {

        private Header responseHeader;
        private Response response;


        public Header getResponseHeader() {
            return responseHeader;
        }

        public void setResponseHeader(Header responseHeader) {
            this.responseHeader = responseHeader;
        }

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }
    }