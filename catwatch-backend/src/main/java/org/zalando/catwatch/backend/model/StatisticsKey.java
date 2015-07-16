package org.zalando.catwatch.backend.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;

@SuppressWarnings("serial")
@Embeddable
public class StatisticsKey implements Serializable {

	private long id;

	private Date snapshotDate = null;

	public StatisticsKey() {
		super();
	}

	public StatisticsKey(long id, Date snapshotDate) {
		super();
		this.id = id;
		this.snapshotDate = snapshotDate;
	}

	/**
	 * See {@link Statistics#getId()}
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * See {@link Statistics#getSnapshotDate()}
	 */
	public Date getSnapshotDate() {
		return snapshotDate;
	}

	public void setSnapshotDate(Date snapshotDate) {
		this.snapshotDate = snapshotDate;
	}

}
