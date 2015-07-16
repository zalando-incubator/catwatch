package org.zalando.catwatch.backend.model;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class ContributorKey implements Serializable{

	private long id;

	private long organizationId;
	
	private Date snapshotDate = null;

	
	public ContributorKey() {
		super();
	}

	public ContributorKey(long id, long organizationId, Date snapshotDate) {
		super();
		this.id = id;
		this.organizationId = organizationId;
		this.snapshotDate = snapshotDate;
	}

	/**
	 * See {@link Contributor#getId()}
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * See {@link Contributor#getOrganizationId()}
	 */
	public long getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(long organizationId) {
		this.organizationId = organizationId;
	}

	/**
	 * See {@link Contributor#getSnapshotDate()}
	 */
	public Date getSnapshotDate() {
		return snapshotDate;
	}

	public void setSnapshotDate(Date snapshotDate) {
		this.snapshotDate = snapshotDate;
	}

}
