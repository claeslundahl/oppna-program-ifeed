package se.vgregion.ifeed.service.solr.client;

import java.util.Map;

public class Header {

        private boolean zkConnected;
        private int status;
        private int QTime;
        private Map<String, String> params;

        public boolean isZkConnected() {
            return zkConnected;
        }

        public void setZkConnected(boolean zkConnected) {
            this.zkConnected = zkConnected;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getQTime() {
            return QTime;
        }

        public void setQTime(int QTime) {
            this.QTime = QTime;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }
    }