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
package com.cloud.agent.api;

public class SecurityIngressRuleAnswer extends Answer {
    Long logSequenceNumber = null;
    Long vmId = null;
    
    protected SecurityIngressRuleAnswer() {
    }
    
    public SecurityIngressRuleAnswer(SecurityIngressRulesCmd cmd) {
    	super(cmd);
        this.logSequenceNumber = cmd.getSeqNum();
        this.vmId = cmd.getVmId();
    }
    
    public SecurityIngressRuleAnswer(SecurityIngressRulesCmd cmd, boolean result, String detail) {
        super(cmd, result, detail);
        this.logSequenceNumber = cmd.getSeqNum();
        this.vmId = cmd.getVmId();
    }

	public Long getLogSequenceNumber() {
		return logSequenceNumber;
	}

	public Long getVmId() {
		return vmId;
	}

	@Override
	public String toString() {
		return "[NWGRPRuleAns: vmId=" + vmId + ", seqno=" + logSequenceNumber+"]";
	}
    
   
}
