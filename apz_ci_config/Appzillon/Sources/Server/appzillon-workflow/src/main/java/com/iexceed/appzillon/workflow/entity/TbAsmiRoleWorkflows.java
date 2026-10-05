package com.iexceed.appzillon.workflow.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "TB_ASMI_ROLE_WORKFLOWS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbAsmiRoleWorkflows implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TbAsmiRoleWorkflowsPK id;


    public TbAsmiRoleWorkflows() {

    }

    public TbAsmiRoleWorkflows(TbAsmiRoleWorkflowsPK id) {
        this.id = id;
    }

    public TbAsmiRoleWorkflowsPK getId() {
        return id;
    }

    public void setId(TbAsmiRoleWorkflowsPK id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAsmiRoleWorkflows that = (TbAsmiRoleWorkflows) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
