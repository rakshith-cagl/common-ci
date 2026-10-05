package com.iexceed.appzillon.domain.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class TbAstpLdRecsPK implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "REF_NO")
    private String refNo;
    @Column(name = "SEQ_NO")
    private int seqNo;

    public TbAstpLdRecsPK() {

    }

    public TbAstpLdRecsPK(String refNo, int seqNo) {
        this.refNo = refNo;
        this.seqNo = seqNo;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((refNo == null) ? 0 : refNo.hashCode());
        result = prime * result + seqNo;
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
        TbAstpLdRecsPK other = (TbAstpLdRecsPK) obj;
        if (refNo == null) {
            if (other.refNo != null)
                return false;
        } else if (!refNo.equals(other.refNo))
            return false;
        if (seqNo != other.seqNo)
            return false;
        return true;
    }


}
