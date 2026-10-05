package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the TB_ASMI_CNVUI_SCR database table.
 */
@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ToString
@Table(name = "TB_ASMI_CNVUI_SCR")
@NamedQuery(name = "TbAsmiCnvUIScr.findAll", query = "SELECT t FROM TbAsmiCnvUIScr t")
public class TbAsmiCnvUIScr implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TbAsmiCnvUIScrPK id;

    @Column(name = "CREATE_TS")
    private Timestamp createTs;

    @Column(name = "CREATE_USER_ID")
    private String createUserId;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "SCREEN_DEF")
    private String screenDef;

    @Column(name = "SCREEN_DESC")
    private String screenDesc;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "SCREEN_DESIGN")
    private String screenDesign;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "SCREEN_HTML")
    private String screenHtml;

    @Column(name = "VERSION_NO")
    private int versionNo;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "SCREEN_LAYOUT")
    private String screenLayout;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "TEMPLATE")
    private String template;

    public TbAsmiCnvUIScrPK getId() {
        return this.id;
    }

    public void setId(TbAsmiCnvUIScrPK id) {
        this.id = id;
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

    public String getScreenDef() {
        return this.screenDef;
    }

    public void setScreenDef(String screenDef) {
        this.screenDef = screenDef;
    }

    public String getScreenDesc() {
        return this.screenDesc;
    }

    public void setScreenDesc(String screenDesc) {
        this.screenDesc = screenDesc;
    }

    public String getScreenDesign() {
        return this.screenDesign;
    }

    public void setScreenDesign(String screenDesign) {
        this.screenDesign = screenDesign;
    }

    public String getScreenHtml() {
        return this.screenHtml;
    }

    public void setScreenHtml(String screenHtml) {
        this.screenHtml = screenHtml;
    }

    public int getVersionNo() {
        return this.versionNo;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    public String getScreenLayout() {
        return screenLayout;
    }

    public void setScreenLayout(String screenLayout) {
        this.screenLayout = screenLayout;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}