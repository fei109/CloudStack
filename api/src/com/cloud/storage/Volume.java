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
import java.util.Set;

import com.cloud.acl.ControlledEntity;
import com.cloud.async.AsyncInstanceCreateStatus;
import com.cloud.template.BasedOn;
import com.cloud.utils.fsm.FiniteState;
import com.cloud.utils.fsm.StateMachine;


public interface Volume extends ControlledEntity, BasedOn {
	enum VolumeType {UNKNOWN, ROOT, SWAP, DATADISK, ISO};
	
	enum MirrorState {NOT_MIRRORED, ACTIVE, DEFUNCT};
	
	enum State implements FiniteState<State, Event> {
	    Allocated("The volume is allocated but has not been created yet."),
	    Creating("The volume is being created.  getPoolId() should reflect the pool where it is being created."),
	    Ready("The volume is ready to be used."),
	    Used("The volume is used"),
	    Destroy("The volume is set to be desctroyed but can be recovered."),
	    Destroyed("The volume is destroyed.  Should be removed.");
	    
	    String _description;
	    
	    private State(String description) {
	        _description = description;
	    }

        @Override
        public StateMachine<State, Event> getStateMachine() {
            return s_fsm;
        }

        @Override
        public State getNextState(Event event) {
            return s_fsm.getNextState(this, event);
        }

        @Override
        public List<State> getFromStates(Event event) {
            return s_fsm.getFromStates(this, event);
        }

        @Override
        public Set<Event> getPossibleEvents() {
            return s_fsm.getPossibleEvents(this);
        }
        
        @Override
        public String getDescription() {
            return _description;
        }
        
        private final static StateMachine<State, Event> s_fsm = new StateMachine<State, Event>();
        static {
            s_fsm.addTransition(Allocated, Event.Create, Creating);
            s_fsm.addTransition(Allocated, Event.Destroy, Destroyed);
            s_fsm.addTransition(Creating, Event.OperationRetry, Creating);
            s_fsm.addTransition(Creating, Event.OperationFailed, Allocated);
            s_fsm.addTransition(Creating, Event.OperationSucceeded, Ready);
            s_fsm.addTransition(Creating, Event.Destroy, Destroy);
            s_fsm.addTransition(Ready, Event.Destroy, Destroy);
            s_fsm.addTransition(Ready, Event.Start, Used);
            s_fsm.addTransition(Destroy, Event.OperationSucceeded, Destroyed);
            s_fsm.addTransition(Destroy, Event.OperationFailed, Destroy);
            s_fsm.addTransition(Destroy, Event.OperationRetry, Destroy);
        }
	}
	
	enum Event {
	    Create,
	    Start,
	    OperationFailed,
	    OperationSucceeded,
	    OperationRetry,
	    Destroy;
	}
	
	enum SourceType {
		Snapshot,DiskOffering,Template,Blank
	}
	
	long getId();
	/**
     * @return the volume name
     */
    String getName();
    
    /**
     * @return total size of the partition
     */
    long getSize();
    
    void setSize(long size);
    
    /**
     * @return the vm instance id
     */
    Long getInstanceId();
    
    /**
     * @return the folder of the volume
     */
    String getFolder();
    
    /**
     * @return the path created.
     */
    String getPath();
    
    Long getPodId();
    
    long getDataCenterId();
    
    VolumeType getVolumeType();
    
    Storage.StorageResourceType getStorageResourceType();
    
	Long getPoolId();
	
	State getState();
	
	SourceType getSourceType();
	
	void setSourceType(SourceType sourceType);

	void setSourceId(Long sourceId);

	Long getSourceId();

	Date getAttached();

	void setAttached(Date attached);
	Long getDeviceId();
	
	Date getCreated();
	AsyncInstanceCreateStatus getStatus();
	
	boolean getDestroyed();
	
	long getDiskOfferingId();
	
	String getChainInfo();
}
