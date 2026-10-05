package com.iexceed.appzillon.domain.entity;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;


/**
 * @author arthanarisamy
 */
@Entity
@Table(name = "TB_ASLG_TXN_DETAIL")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "TbAslgTxnDetail.findAll", query = "SELECT t FROM TbAslgTxnDetail t"),
        @NamedQuery(name = "TbAslgTxnDetail.findByTxnRef", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.txnRef = :txnRef"),
        @NamedQuery(name = "TbAslgTxnDetail.findByUserId", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.userId = :userId"),
        @NamedQuery(name = "TbAslgTxnDetail.findByDeviceId", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.deviceId = :deviceId"),
        @NamedQuery(name = "TbAslgTxnDetail.findByStTm", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.stTm = :stTm"),
        @NamedQuery(name = "TbAslgTxnDetail.findByEndTm", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.endTm = :endTm"),
        @NamedQuery(name = "TbAslgTxnDetail.findByInterfaceId", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.interfaceId = :interfaceId"),
        @NamedQuery(name = "TbAslgTxnDetail.findByAppId", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.appId = :appId"),
        @NamedQuery(name = "TbAslgTxnDetail.findByTxnStat", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.txnStat = :txnStat"),
        @NamedQuery(name = "TbAslgTxnDetail.findByCreateBy", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.createBy = :createBy"),
        @NamedQuery(name = "TbAslgTxnDetail.findByCreateTs", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.createTs = :createTs")})

//@NamedQuery(name = "TbAslgTxnDetail.findByRecStat", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.recStat = :recStat"),
//@NamedQuery(name = "TbAslgTxnDetail.findByExtSyCode", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.extSyCode = :extSyCode"),
//@NamedQuery(name = "TbAslgTxnDetail.findByExtTxnRef", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.extTxnRef = :extTxnRef"),
//@NamedQuery(name = "TbAslgTxnDetail.findByLgnRef", query = "SELECT t FROM TbAslgTxnDetail t WHERE t.lgnRef = :lgnRef"),
public class TbAslgTxnDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
//    @GeneratedValue(strategy = GenerationType.TABLE, generator = "LOGGINGSEQUENCE")
    @Column(name = "TXN_REF")
    private String txnRef;
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "USER_APP_ID")
    private String userAppId;
    @Column(name = "APP_USER_ID")
    private String appUserId;
    @Column(name = "SESSION_ID")
    private String sessionId;
    @Column(name = "DEVICE_ID")
    private String deviceId;
    @Column(name = "ST_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date stTm;
    @Column(name = "END_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTm;
    @Column(name = "INTERFACE_ID")
    private String interfaceId;
    @Column(name = "APP_ID")
    private String appId;
    @Column(name = "TXN_STAT")
    private String txnStat;
    @Column(name = "CREATE_BY")
    private String createBy;
    @Column(name = "CREATE_TS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTs;

    @Column(name = "SOURCE")
    private String source;

    @Column(name = "LONGITUDE")
    private String longitude;

    @Column(name = "LATITUDE")
    private String latitude;

    @Column(name = "ORIGINATION")
    private String origination;

    @Column(name = "STATUS")
    private String status;
    @Column(name = "EXT_ST_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date extStTm;

    @Column(name = "EXT_END_TM")
    @Temporal(TemporalType.TIMESTAMP)
    private Date extEndTm;

    @Column(name = "SUBLOCALITY")
    private String sublocality;
    @Column(name = "ADMIN_AREA_LVL_1")
    private String adminAreaLvl1;
    @Column(name = "ADMIN_AREA_LVL_2")
    private String adminAreaLvl2;
    @Column(name = "COUNTRY")
    private String country;
    @Column(name = "FORMATTED_ADDRESS")
    private String formattedAddress;

    @Column(name = "REQ_LD_REFNO")
    private String reqLdRefNo;

    @Column(name = "RES_LD_REFNO")
    private String resLdRefNo;

    @Column(name = "REQ_NO_RECS")
    private int reqNoRecs;

    @Column(name = "RES_NO_RECS")
    private int resNoRecs;

    @Column(name = "INFO1")
    private String info1;

    @Column(name = "INFO2")
    private String info2;

    @Column(name = "INFO3")
    private String info3;

    @Column(name = "INFO4")
    private String info4;

    @Column(name = "INFO5")
    private String info5;

    public TbAslgTxnDetail() {
    }

    public TbAslgTxnDetail(String txnRef) {
        this.txnRef = txnRef;
    }

    public TbAslgTxnDetail(String txnRef, String userId) {
        this.txnRef = txnRef;
        this.userId = userId;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserAppId() {
        return userAppId;
    }

    public void setUserAppId(String userAppId) {
        this.userAppId = userAppId;
    }

    public String getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(String appUserId) {
        this.appUserId = appUserId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Date getStTm() {
        return stTm;
    }

    public void setStTm(Date stTm) {
        this.stTm = stTm;
    }

    public Date getEndTm() {
        return endTm;
    }

    public void setEndTm(Date endTm) {
        this.endTm = endTm;
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTxnStat() {
        return txnStat;
    }

    public void setTxnStat(String txnStat) {
        this.txnStat = txnStat;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTs() {
        return createTs;
    }

    public void setCreateTs(Date createTs) {
        this.createTs = createTs;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getOrigination() {
        return origination;
    }

    public void setOrigination(String origination) {
        this.origination = origination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getExtStTm() {
        return extStTm;
    }

    public void setExtStTm(Date extStTm) {
        this.extStTm = extStTm;
    }

    public Date getExtEndTm() {
        return extEndTm;
    }

    public void setExtEndTm(Date extEndTm) {
        this.extEndTm = extEndTm;
    }

    public String getSublocality() {
        return sublocality;
    }

    public void setSublocality(String sublocality) {
        this.sublocality = sublocality;
    }

    public String getAdminAreaLvl1() {
        return adminAreaLvl1;
    }

    public void setAdminAreaLvl1(String adminAreaLvl1) {
        this.adminAreaLvl1 = adminAreaLvl1;
    }

    public String getAdminAreaLvl2() {
        return adminAreaLvl2;
    }

    public void setAdminAreaLvl2(String adminAreaLvl2) {
        this.adminAreaLvl2 = adminAreaLvl2;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getReqLdRefNo() {
        return reqLdRefNo;
    }

    public void setReqLdRefNo(String reqLdRefNo) {
        this.reqLdRefNo = reqLdRefNo;
    }

    public String getResLdRefNo() {
        return resLdRefNo;
    }

    public void setResLdRefNo(String resLdRefNo) {
        this.resLdRefNo = resLdRefNo;
    }

    public int getReqNoRecs() {
        return reqNoRecs;
    }

    public void setReqNoRecs(int reqNoRecs) {
        this.reqNoRecs = reqNoRecs;
    }

    public int getResNoRecs() {
        return resNoRecs;
    }

    public void setResNoRecs(int resNoRecs) {
        this.resNoRecs = resNoRecs;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public String getInfo3() {
        return info3;
    }

    public void setInfo3(String info3) {
        this.info3 = info3;
    }

    public String getInfo4() {
        return info4;
    }

    public void setInfo4(String info4) {
        this.info4 = info4;
    }

    public String getInfo5() {
        return info5;
    }

    public void setInfo5(String info5) {
        this.info5 = info5;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((txnRef == null) ? 0 : txnRef.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TbAslgTxnDetail other = (TbAslgTxnDetail) obj;
        if (txnRef == null) {
            if (other.txnRef != null)
                return false;
        } else if (!txnRef.equals(other.txnRef))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TbAslgTxnDetail [txnRef=" + txnRef + ", userId=" + userId + ", userAppId=" + userAppId + ", appUserId="
                + appUserId + ", sessionId=" + sessionId + ", deviceId=" + deviceId + ", stTm=" + stTm + ", endTm="
                + endTm + ", interfaceId=" + interfaceId + ", appId=" + appId + ", txnStat=" + txnStat + ", createBy="
                + createBy + ", createTs=" + createTs + ", source=" + source + ", longitude=" + longitude
                + ", latitude=" + latitude + ", origination=" + origination + ", status=" + status + ", extStTm="
                + extStTm + ", extEndTm=" + extEndTm + ", sublocality=" + sublocality + ", adminAreaLvl1="
                + adminAreaLvl1 + ", adminAreaLvl2=" + adminAreaLvl2 + ", country=" + country + ", formattedAddress="
                + formattedAddress + ", reqLdRefNo=" + reqLdRefNo + ", resLdRefNo=" + resLdRefNo + ", reqNoRecs="
                + reqNoRecs + ", resNoRecs=" + resNoRecs + ", info1=" + info1 + ", info2=" + info2 + ", info3=" + info3
                + ", info4=" + info4 + ", info5=" + info5 + "]";
    }

}
