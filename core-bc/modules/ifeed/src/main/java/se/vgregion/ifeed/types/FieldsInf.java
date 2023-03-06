package se.vgregion.ifeed.types;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "vgr_ifeed_fields_inf")
public class FieldsInf {

    private static final long serialVersionUID = 1L;

    public FieldsInf() {
        // For the jpa.
    }

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long version;

    private String creatorId;

    @Column(length = 200_000)
    private String text;

}
