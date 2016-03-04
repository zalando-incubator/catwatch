package org.zalando.catwatch.backend.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.zalando.catwatch.backend.model.util.JsonDateDeserializer;
import org.zalando.catwatch.backend.model.util.JsonDateSerializer;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
@Embeddable
public class StatisticsKey implements Serializable {

    private long id;

    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Date snapshotDate = null;

    public StatisticsKey() {
        super();
    }

    public StatisticsKey(final long id, final Date snapshotDate) {
        super();
        this.id = id;
        this.snapshotDate = snapshotDate;
    }

    /**
     * See {@link Statistics#getId()}.
     */
    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    /**
     * See {@link Statistics#getSnapshotDate()}.
     */
    public Date getSnapshotDate() {
        return snapshotDate;
    }

    public void setSnapshotDate(final Date snapshotDate) {
        this.snapshotDate = snapshotDate;
    }

}
