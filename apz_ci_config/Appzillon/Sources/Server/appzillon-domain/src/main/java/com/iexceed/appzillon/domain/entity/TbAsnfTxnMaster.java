package com.iexceed.appzillon.domain.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * The persistent class for the TB_ASNF_TXN_MASTER database table.
 */
@Entity
@Table(name = "TB_ASNF_TXN_MASTER")
@NamedQuery(name = "TbAsnfTxnMaster.findAll", query = "SELECT t FROM TbAsnfTxnMaster t")
public class TbAsnfTxnMaster implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "IMAGE_URL")
    private String imageURL;
    @Column(name = "SUBTITLE")
    private String subtitle;
    @Column(name = "CATEGORY")
    private String category;
    @Column(name = "CREATE_TS")
    private Date createTS;
    @EmbeddedId
    private TbAsnfTxnMasterPK id;

    public TbAsnfTxnMaster() {
        // Default constructor
    }

    public TbAsnfTxnMasterPK getId() {
        return this.id;
    }

    public void setId(TbAsnfTxnMasterPK id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreateTS() {
        return createTS;
    }

    public void setCreateTS(Date createTS) {
        this.createTS = createTS;
    }
}