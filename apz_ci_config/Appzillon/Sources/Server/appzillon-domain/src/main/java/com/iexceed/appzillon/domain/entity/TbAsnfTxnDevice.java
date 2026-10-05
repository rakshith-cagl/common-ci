package com.iexceed.appzillon.domain.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * The persistent class for the TB_ASNF_TXN_DEVICES database table.
 */
@Entity
@Table(name = "TB_ASNF_TXN_DEVICES")
@NamedQuery(name = "TbAsnfTxnDevice.findAll", query = "SELECT t FROM TbAsnfTxnDevice t")
public class TbAsnfTxnDevice implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TbAsnfTxnDevicePK id;

    public TbAsnfTxnDevice() {
        // Default constructor
    }

    public TbAsnfTxnDevicePK getId() {
        return this.id;
    }

    public void setId(TbAsnfTxnDevicePK id) {
        this.id = id;
    }

}