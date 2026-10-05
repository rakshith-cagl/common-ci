package com.iexceed.appzillon.domain.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the TB_ASMI_LAYOUT_DESIGN database table.
 */
@Entity
@Table(name = "TB_ASMI_LAYOUT_DESIGN")
@NamedQuery(name = "TbAsmiLayoutDesign.findAll", query = "SELECT t FROM TbAsmiLayoutDesign t")
public class TbAsmiLayoutDesign implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TbAsmiLayoutDesignPK id;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "DESIGN_ICON")
    private String designIcon;

    @Column(name = "DESIGN_NAME")
    private String designName;

    public TbAsmiLayoutDesign() {
        // Default constructor
    }

    public TbAsmiLayoutDesignPK getId() {
        return this.id;
    }

    public void setId(TbAsmiLayoutDesignPK id) {
        this.id = id;
    }

    public String getDesignIcon() {
        return designIcon;
    }

    public void setDesignIcon(String designIcon) {
        this.designIcon = designIcon;
    }

    public String getDesignName() {
        return designName;
    }

    public void setDesignName(String designName) {
        this.designName = designName;
    }
}