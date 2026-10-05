package com.iexceed.appzillon.workflow.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "TB_ASMI_WORKFLOW_LINKED_STAGES")
public class TbAsmiWorkflowLinkedStages {

    @EmbeddedId
    private TbAsmiWorkflowLinkedStagesPK id;

    public TbAsmiWorkflowLinkedStages(TbAsmiWorkflowLinkedStagesPK id) {
        this.id = id;
    }

    public TbAsmiWorkflowLinkedStages() {
    }

    public TbAsmiWorkflowLinkedStagesPK getId() {
        return id;
    }

    public void setId(TbAsmiWorkflowLinkedStagesPK id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbAsmiWorkflowLinkedStages that = (TbAsmiWorkflowLinkedStages) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
