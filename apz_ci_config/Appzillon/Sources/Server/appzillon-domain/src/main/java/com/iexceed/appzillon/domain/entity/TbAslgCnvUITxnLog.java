package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the TB_ASLG_CNVUI_TXN_LOG database table.
 */
@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Table(name = "TB_ASLG_CNVUI_TXN_LOG")
@NamedQuery(name = "TbAslgCnvUITxnLog.findAll", query = "SELECT t FROM TbAslgCnvUITxnLog t")
public class TbAslgCnvUITxnLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "TXN_REF")
    private String txnRef;

    @Column(name = "APP_ID")
    private String appId;

    @Column(name = "CNVUI_ID")
    private String cnvUIId;

    @Column(name = "CREATE_TS")
    private Timestamp createTs;

    @Column(name = "CREATE_USER_ID")
    private String createUserId;

    @Column(name = "RESP_DLG_ID")
    private String respDlgId;

    @Column(name = "SCREEN_DATA")
    private String screenData;

    @Column(name = "UPDATE_TS")
    private Timestamp updateTs;

    public String getTxnRef() {
        return this.txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCnvUIId() {
        return this.cnvUIId;
    }

    public void setCnvUIId(String cnvUIId) {
        this.cnvUIId = cnvUIId;
    }

    public Timestamp getCreateTs() {
        return this.createTs;
    }

    public void setCreateTs(Timestamp createTs) {
        this.createTs = createTs;
    }

    public String getCreateUserId() {
        return this.createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getRespDlgId() {
        return this.respDlgId;
    }

    public void setRespDlgId(String respDlgId) {
        this.respDlgId = respDlgId;
    }

    public String getScreenData() {
        return this.screenData;
    }

    public void setScreenData(String screenData) {
        this.screenData = screenData;
    }

    public Timestamp getUpdateTs() {
        return this.updateTs;
    }

    public void setUpdateTs(Timestamp updateTs) {
        this.updateTs = updateTs;
    }

}
