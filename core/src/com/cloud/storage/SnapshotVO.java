/**
 *  Copyright (C) 2010 Cloud.com, Inc.  All rights reserved.
 * 
 * This software is licensed under the GNU General Public License v3 or later.
 * 
 * It is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.cloud.storage;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.cloud.utils.db.GenericDao;
import com.google.gson.annotations.Expose;

@Entity
@Table(name="snapshots")
public class SnapshotVO implements Snapshot {
	
    @Id
    @TableGenerator(name="snapshots_sq", table="sequence", pkColumnName="name", valueColumnName="value", pkColumnValue="snapshots_seq", allocationSize=1)
    @Column(name="id", updatable=false, nullable = false)
    Long id;

    @Column(name="account_id")
    long accountId;

    @Column(name="volume_id")
    long volumeId;

    @Expose
    @Column(name="path")
    String path;

    @Expose
    @Column(name="name")
    String name;
    
    @Expose
    @Column(name="status", updatable = true, nullable=false)
    @Enumerated(value=EnumType.STRING)
    private Status status;

    @Column(name="snapshot_type")
    short snapshotType;

    @Column(name="type_description")
    String typeDescription;

    @Column(name=GenericDao.CREATED_COLUMN)
    Date created;

    @Column(name=GenericDao.REMOVED_COLUMN)
    Date removed;

    @Column(name="backup_snap_id")
    String backupSnapshotId;
    
    @Column(name="prev_snap_id")
    long prevSnapshotId;

    @Column(name="hypervisor_type")
    @Enumerated(value=EnumType.STRING)
    HypervisorType  hypervisorType;
    
    public SnapshotVO() { }

    public SnapshotVO(long id, long accountId, long volumeId, String path, String name, short snapshotType, String typeDescription, HypervisorType hypervisorType) {
        this.id = id;
        this.accountId = accountId;
        this.volumeId = volumeId;
        this.path = path;
        this.name = name;
        this.snapshotType = snapshotType;
        this.typeDescription = typeDescription;
        this.status = Status.Creating;
        this.prevSnapshotId = 0;
        this.hypervisorType = hypervisorType;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public long getAccountId() {
        return accountId;
    }

    @Override
    public long getVolumeId() {
        return volumeId;
    }

    @Override
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
    	this.path = path;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public short getSnapshotType() {
        return snapshotType;
    }
    
    @Override
    public HypervisorType getHypervisorType() {
    	return hypervisorType;
    }
    
    public void setSnapshotType(short snapshotType) {
        this.snapshotType = snapshotType;
    }

    public String getTypeDescription() {
        return typeDescription;
    }
    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public Date getCreated() {
        return created;
    }

    public Date getRemoved() {
        return removed;
    }
    
	@Override
    public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public String getBackupSnapshotId(){
		return backupSnapshotId;
	}
	
    public long getPrevSnapshotId(){
		return prevSnapshotId;
	}
	
	public void setBackupSnapshotId(String backUpSnapshotId){
		this.backupSnapshotId = backUpSnapshotId;
	}
	
	public void setPrevSnapshotId(long prevSnapshotId){
		this.prevSnapshotId = prevSnapshotId;
	}

    public static Type getSnapshotType(Long policyId) {
        if (policyId.equals(MANUAL_POLICY_ID)) {
            return Type.MANUAL;
        } else {
        	return Type.RECURRING;
        }
    }
    
    public static Type getSnapshotType(String snapshotType) {
        if (Type.MANUAL.equals(snapshotType)) {
            return Type.MANUAL;
        }
        if (Type.RECURRING.equals(snapshotType)) {
            return Type.RECURRING;
        }
        if (Type.TEMPLATE.equals(snapshotType)) {
            return Type.TEMPLATE;
        }
        return null;
    }
}
