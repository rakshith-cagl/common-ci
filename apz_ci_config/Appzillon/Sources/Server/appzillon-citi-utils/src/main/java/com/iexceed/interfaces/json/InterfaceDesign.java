package com.iexceed.interfaces.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "typeclass",
        "nam",
        "type",
        "childs",
        "servicetype",
        "offlinesupport",
        "sslrequired",
        "soapignorenamespaces",
        "encryptionrequired",
        "smsallowed",
        "ussdallowed",
        "twitterallowed",
        "facebookallowed",
        "ejbauthenticationreqd",
        "jmstype",
        "isosignonrequired",
        "isonewlinerequired",
        "isoautogenratefld11",
        "isofield11length",
        "isoreqbinarybitmap",
        "isorespbinarybitmap",
        "ldapauthenticationreq",
        "ldapoperationadd",
        "ldapoperationdelete",
        "ldapoperationsearch",
        "ldapoperationupdate",
        "reporttype",
        "reportrestype",
        "reportpasswordreq",
        "sessionrequired",
        "dbqueryallowed",
        "dbnewallowed",
        "dbmodifyallowed",
        "dbdeleteallowed",
        "dbauthorizeallowed",
        "dbautoauth",
        "dbdeleteresponsetype",
        "dbauthorizeresponsetype",
        "dbqueryresponsetype",
        "dbnewresponsetype",
        "dbmodifyresponsetype"
})
public class InterfaceDesign {

    private String typeclass = "INTERFACE";
    private String nam;
    private String type = "INTERFACE";
    private String servicetype;
    private String offlinesupport;
    private String sslrequired;
    private String soapignorenamespaces;
    private String encryptionrequired;
    private String smsallowed;
    private String ussdallowed;
    private String twitterallowed;
    private String facebookallowed;
    private String ejbauthenticationreqd;
    private String jmstype;
    private String isosignonrequired;
    private String isonewlinerequired;
    private String isoautogenratefld11;
    private String isofield11length;
    private String isoreqbinarybitmap;
    private String isorespbinarybitmap;
    private String ldapauthenticationreq;
    private String ldapoperationadd;
    private String ldapoperationdelete;
    private String ldapoperationsearch;
    private String ldapoperationupdate;
    private String reporttype;
    private String reportrestype;
    private String reportpasswordreq;
    private String sessionrequired;
    private String dbqueryallowed;
    private String dbnewallowed;
    private String dbmodifyallowed;
    private String dbdeleteallowed;
    private String dbauthorizeallowed;
    private String dbautoauth;
    private String dbdeleteresponsetype;
    private String dbauthorizeresponsetype;
    private String dbqueryresponsetype;
    private String dbnewresponsetype;
    private String dbmodifyresponsetype;
    private List<DataModel> dataModel;

    public String getTypeclass() {
        return typeclass;
    }

    public void setTypeclass(String typeclass) {
        this.typeclass = typeclass;
    }

    public String getNam() {
        return nam;
    }

    public void setNam(String nam) {
        this.nam = nam;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServicetype() {
        return servicetype;
    }

    public void setServicetype(String servicetype) {
        this.servicetype = servicetype;
    }

    public String getOfflinesupport() {
        return offlinesupport;
    }

    public void setOfflinesupport(String offlinesupport) {
        this.offlinesupport = offlinesupport;
    }

    public String getSslrequired() {
        return sslrequired;
    }

    public void setSslrequired(String sslrequired) {
        this.sslrequired = sslrequired;
    }

    public String getSoapignorenamespaces() {
        return soapignorenamespaces;
    }

    public void setSoapignorenamespaces(String soapignorenamespaces) {
        this.soapignorenamespaces = soapignorenamespaces;
    }

    public String getEncryptionrequired() {
        return encryptionrequired;
    }

    public void setEncryptionrequired(String encryptionrequired) {
        this.encryptionrequired = encryptionrequired;
    }

    public String getSmsallowed() {
        return smsallowed;
    }

    public void setSmsallowed(String smsallowed) {
        this.smsallowed = smsallowed;
    }

    public String getUssdallowed() {
        return ussdallowed;
    }

    public void setUssdallowed(String ussdallowed) {
        this.ussdallowed = ussdallowed;
    }

    public String getTwitterallowed() {
        return twitterallowed;
    }

    public void setTwitterallowed(String twitterallowed) {
        this.twitterallowed = twitterallowed;
    }

    public String getFacebookallowed() {
        return facebookallowed;
    }

    public void setFacebookallowed(String facebookallowed) {
        this.facebookallowed = facebookallowed;
    }

    public String getEjbauthenticationreqd() {
        return ejbauthenticationreqd;
    }

    public void setEjbauthenticationreqd(String ejbauthenticationreqd) {
        this.ejbauthenticationreqd = ejbauthenticationreqd;
    }

    public String getJmstype() {
        return jmstype;
    }

    public void setJmstype(String jmstype) {
        this.jmstype = jmstype;
    }

    public String getIsosignonrequired() {
        return isosignonrequired;
    }

    public void setIsosignonrequired(String isosignonrequired) {
        this.isosignonrequired = isosignonrequired;
    }

    public String getIsonewlinerequired() {
        return isonewlinerequired;
    }

    public void setIsonewlinerequired(String isonewlinerequired) {
        this.isonewlinerequired = isonewlinerequired;
    }

    public String getIsoautogenratefld11() {
        return isoautogenratefld11;
    }

    public void setIsoautogenratefld11(String isoautogenratefld11) {
        this.isoautogenratefld11 = isoautogenratefld11;
    }

    public String getIsofield11length() {
        return isofield11length;
    }

    public void setIsofield11length(String isofield11length) {
        this.isofield11length = isofield11length;
    }

    public String getIsoreqbinarybitmap() {
        return isoreqbinarybitmap;
    }

    public void setIsoreqbinarybitmap(String isoreqbinarybitmap) {
        this.isoreqbinarybitmap = isoreqbinarybitmap;
    }

    public String getIsorespbinarybitmap() {
        return isorespbinarybitmap;
    }

    public void setIsorespbinarybitmap(String isorespbinarybitmap) {
        this.isorespbinarybitmap = isorespbinarybitmap;
    }

    public String getLdapauthenticationreq() {
        return ldapauthenticationreq;
    }

    public void setLdapauthenticationreq(String ldapauthenticationreq) {
        this.ldapauthenticationreq = ldapauthenticationreq;
    }

    public String getLdapoperationadd() {
        return ldapoperationadd;
    }

    public void setLdapoperationadd(String ldapoperationadd) {
        this.ldapoperationadd = ldapoperationadd;
    }

    public String getLdapoperationdelete() {
        return ldapoperationdelete;
    }

    public void setLdapoperationdelete(String ldapoperationdelete) {
        this.ldapoperationdelete = ldapoperationdelete;
    }

    public String getLdapoperationsearch() {
        return ldapoperationsearch;
    }

    public void setLdapoperationsearch(String ldapoperationsearch) {
        this.ldapoperationsearch = ldapoperationsearch;
    }

    public String getLdapoperationupdate() {
        return ldapoperationupdate;
    }

    public void setLdapoperationupdate(String ldapoperationupdate) {
        this.ldapoperationupdate = ldapoperationupdate;
    }

    public String getReporttype() {
        return reporttype;
    }

    public void setReporttype(String reporttype) {
        this.reporttype = reporttype;
    }

    public String getReportrestype() {
        return reportrestype;
    }

    public void setReportrestype(String reportrestype) {
        this.reportrestype = reportrestype;
    }

    public String getReportpasswordreq() {
        return reportpasswordreq;
    }

    public void setReportpasswordreq(String reportpasswordreq) {
        this.reportpasswordreq = reportpasswordreq;
    }

    public String getSessionrequired() {
        return sessionrequired;
    }

    public void setSessionrequired(String sessionrequired) {
        this.sessionrequired = sessionrequired;
    }

    public String getDbqueryallowed() {
        return dbqueryallowed;
    }

    public void setDbqueryallowed(String dbqueryallowed) {
        this.dbqueryallowed = dbqueryallowed;
    }

    public String getDbnewallowed() {
        return dbnewallowed;
    }

    public void setDbnewallowed(String dbnewallowed) {
        this.dbnewallowed = dbnewallowed;
    }

    public String getDbmodifyallowed() {
        return dbmodifyallowed;
    }

    public void setDbmodifyallowed(String dbmodifyallowed) {
        this.dbmodifyallowed = dbmodifyallowed;
    }

    public String getDbdeleteallowed() {
        return dbdeleteallowed;
    }

    public void setDbdeleteallowed(String dbdeleteallowed) {
        this.dbdeleteallowed = dbdeleteallowed;
    }

    public String getDbauthorizeallowed() {
        return dbauthorizeallowed;
    }

    public void setDbauthorizeallowed(String dbauthorizeallowed) {
        this.dbauthorizeallowed = dbauthorizeallowed;
    }

    public String getDbautoauth() {
        return dbautoauth;
    }

    public void setDbautoauth(String dbautoauth) {
        this.dbautoauth = dbautoauth;
    }

    public String getDbdeleteresponsetype() {
        return dbdeleteresponsetype;
    }

    public void setDbdeleteresponsetype(String dbdeleteresponsetype) {
        this.dbdeleteresponsetype = dbdeleteresponsetype;
    }

    public String getDbauthorizeresponsetype() {
        return dbauthorizeresponsetype;
    }

    public void setDbauthorizeresponsetype(String dbauthorizeresponsetype) {
        this.dbauthorizeresponsetype = dbauthorizeresponsetype;
    }

    public String getDbqueryresponsetype() {
        return dbqueryresponsetype;
    }

    public void setDbqueryresponsetype(String dbqueryresponsetype) {
        this.dbqueryresponsetype = dbqueryresponsetype;
    }

    public String getDbnewresponsetype() {
        return dbnewresponsetype;
    }

    public void setDbnewresponsetype(String dbnewresponsetype) {
        this.dbnewresponsetype = dbnewresponsetype;
    }

    public String getDbmodifyresponsetype() {
        return dbmodifyresponsetype;
    }

    public void setDbmodifyresponsetype(String dbmodifyresponsetype) {
        this.dbmodifyresponsetype = dbmodifyresponsetype;
    }

    public List<DataModel> getDataModel() {
        return dataModel;
    }

    @JsonProperty("childs")
    public void setDataModel(List<DataModel> dataModel) {
        this.dataModel = dataModel;
    }


}
