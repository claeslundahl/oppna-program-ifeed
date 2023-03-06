package se.vgregion.ifeed.types;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Deprecated // Is this used?
@Entity
@Data
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

}
