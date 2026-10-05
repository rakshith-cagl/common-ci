package com.iexceed.appzillon.domain.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by diganta.kumar@i-exceed.com on 10/7/17 7:53 PM
 */
@Entity
@Data
@EqualsAndHashCode
@NoArgsConstructor
@Table(name = "TB_ASCZ_TEMPLATE_OBJECTS")
public class TbAsczTemplateObjects implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TbAsczTemplateObjectsPK id;
    @Column(name = "CHILD_SEQ")
    private int childSeq;

    public TbAsczTemplateObjectsPK getId() {
        return id;
    }

    public void setId(TbAsczTemplateObjectsPK id) {
        this.id = id;
    }

    public int getChildSeq() {
        return childSeq;
    }

    public void setChildSeq(int childSeq) {
        this.childSeq = childSeq;
    }

    @Override
    public String toString() {
        return "TbAsczTemplateObjects{" +
                "id=" + id +
                ", childSeq=" + childSeq +
                '}';
    }
}
