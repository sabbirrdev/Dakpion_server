package com.company.efood.sys.entity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "COMMON_AUDIT_LOG")
//@Convert(attributeName  = "jsonb", converter  = JsonBinaryType.class)
public class AuditLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Long id;

    @Column(name = "ENTRY_USER")
    private Integer entryUser;

    @Column(name = "ENTRY_DATE")
    private Date entryDate;

    @Column(name = "TABLE_NAME", length = 100)
    private String tableName;

    @Column(name = "AUDIT_TYPE", length = 20)
    private String auditType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "CURRENT_DATA", columnDefinition = "jsonb")
    private Object currentData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "OLD_DATA", columnDefinition = "jsonb")
    private Object oldData;

    @Column(name = "GET_METHOD")
    private String getMethod;
}
