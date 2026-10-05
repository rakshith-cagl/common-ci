package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Table(name = "TB_ASTP_LD_RECS")
public class TbAstpLdRecs implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private TbAstpLdRecsPK id;
    @Column(name = "DATA1")
    private String data1;

    @Column(name = "DATA2")
    private String data2;

    @Column(name = "DATA3")
    private String data3;

    @Column(name = "DATA4")
    private String data4;

    @Column(name = "DATA5")
    private String data5;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TS", insertable = false)
    private Date createTs;

    public TbAstpLdRecs(TbAstpLdRecsPK id, String data1, String data2, String data3, String data4, String data5) {
        super();
        this.id = id;
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
        this.data4 = data4;
        this.data5 = data5;
    }

    public TbAstpLdRecsPK getId() {
        return id;
    }

    public void setId(TbAstpLdRecsPK id) {
        this.id = id;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    public String getData4() {
        return data4;
    }

    public void setData4(String data4) {
        this.data4 = data4;
    }

    public String getData5() {
        return data5;
    }

    public void setData5(String data5) {
        this.data5 = data5;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

}
