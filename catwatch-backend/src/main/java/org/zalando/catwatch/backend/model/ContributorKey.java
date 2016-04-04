package org.zalando.catwatch.backend.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.zalando.catwatch.backend.model.util.JsonDateDeserializer;
import org.zalando.catwatch.backend.model.util.JsonDateSerializer;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class ContributorKey implements Serializable {

    private long id;

    private long organizationId;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Date snapshotDate = null;

    public ContributorKey() {
        super();
    }

    public ContributorKey(final long id, final long organizationId, final Date snapshotDate) {
        super();
        this.id = id;
        this.organizationId = organizationId;
        this.snapshotDate = snapshotDate;
    }

    /**
     * See {@link Contributor#getId()}.
     */
    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    /**
     * See {@link Contributor#getOrganizationId()}.
     */
    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(final long organizationId) {
        this.organizationId = organizationId;
    }

    /**
     * See {@link Contributor#getSnapshotDate()}.
     */
    public Date getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(final Date snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

}
