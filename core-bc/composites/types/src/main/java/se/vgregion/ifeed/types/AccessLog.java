package se.vgregion.ifeed.types;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "access_log")
public class AccessLog {

    @Id
    @Column(name = "id", length = 1000)
    private String id;

    @Column(name = "time")
    private Date time;

    @Column(name = "path", length = 1000)
    private String path;

    @Column(name = "host")
    private String host;

    @Column(name = "http_status")
    private String httpStatus;

    @Column(name = "ifeed_id")
    private Long ifeedId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(String httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessLog accessLog = (AccessLog) o;

        if (id != null ? !id.equals(accessLog.id) : accessLog.id != null) return false;
        if (time != null ? !time.equals(accessLog.time) : accessLog.time != null) return false;
        if (path != null ? !path.equals(accessLog.path) : accessLog.path != null) return false;
        if (host != null ? !host.equals(accessLog.host) : accessLog.host != null) return false;
        return httpStatus != null ? httpStatus.equals(accessLog.httpStatus) : accessLog.httpStatus == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (httpStatus != null ? httpStatus.hashCode() : 0);
        return result;
    }

    public Long getIfeedId() {
        return ifeedId;
    }

    public void setIfeedId(Long ifeedId) {
        this.ifeedId = ifeedId;
    }
}
