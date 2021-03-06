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

package com.cloud.async.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import org.apache.log4j.Logger;

import com.cloud.async.AsyncJob;
import com.cloud.async.AsyncJobResult;
import com.cloud.async.AsyncJobVO;
import com.cloud.utils.db.DB;
import com.cloud.utils.db.Filter;
import com.cloud.utils.db.GenericDaoBase;
import com.cloud.utils.db.SearchBuilder;
import com.cloud.utils.db.SearchCriteria;
import com.cloud.utils.db.Transaction;

@Local(value = { AsyncJobDao.class })
public class AsyncJobDaoImpl extends GenericDaoBase<AsyncJobVO, Long> implements AsyncJobDao {
    private static final Logger s_logger = Logger.getLogger(AsyncJobDaoImpl.class.getName());
	
	private SearchBuilder<AsyncJobVO> pendingAsyncJobSearch;	
	private SearchBuilder<AsyncJobVO> pendingAsyncJobsSearch;	
	private SearchBuilder<AsyncJobVO> expiringAsyncJobSearch;		
	
	public AsyncJobDaoImpl() {
		pendingAsyncJobSearch = createSearchBuilder();
		pendingAsyncJobSearch.and("instanceType", pendingAsyncJobSearch.entity().getInstanceType(), 
			SearchCriteria.Op.EQ);
		pendingAsyncJobSearch.and("instanceId", pendingAsyncJobSearch.entity().getInstanceId(), 
			SearchCriteria.Op.EQ);
		pendingAsyncJobSearch.and("status", pendingAsyncJobSearch.entity().getStatus(), 
				SearchCriteria.Op.EQ);
		pendingAsyncJobSearch.done();
		
		pendingAsyncJobsSearch = createSearchBuilder();
		pendingAsyncJobsSearch.and("instanceType", pendingAsyncJobsSearch.entity().getInstanceType(), 
			SearchCriteria.Op.EQ);
		pendingAsyncJobsSearch.and("accountId", pendingAsyncJobsSearch.entity().getAccountId(), 
			SearchCriteria.Op.EQ);
		pendingAsyncJobsSearch.and("status", pendingAsyncJobsSearch.entity().getStatus(), 
				SearchCriteria.Op.EQ);
		pendingAsyncJobsSearch.done();
		
		expiringAsyncJobSearch = createSearchBuilder();
		expiringAsyncJobSearch.and("created", expiringAsyncJobSearch.entity().getCreated(), 
			SearchCriteria.Op.LTEQ);
		expiringAsyncJobSearch.done();
	}
	
	public AsyncJobVO findInstancePendingAsyncJob(String instanceType, long instanceId) {
        SearchCriteria<AsyncJobVO> sc = pendingAsyncJobSearch.create();
        sc.setParameters("instanceType", instanceType);
        sc.setParameters("instanceId", instanceId);
        sc.setParameters("status", AsyncJobResult.STATUS_IN_PROGRESS);
        
        List<AsyncJobVO> l = listIncludingRemovedBy(sc);
        if(l != null && l.size() > 0) {
        	if(l.size() > 1) {
        		s_logger.warn("Instance " + instanceType + "-" + instanceId + " has multiple pending async-job");
        	}
        	
        	return l.get(0);
        }
        return null;
	}
	
	public List<AsyncJobVO> findInstancePendingAsyncJobs(AsyncJob.Type instanceType, long accountId) {
		SearchCriteria<AsyncJobVO> sc = pendingAsyncJobsSearch.create();
        sc.setParameters("instanceType", instanceType);
        sc.setParameters("accountId", accountId);
        sc.setParameters("status", AsyncJobResult.STATUS_IN_PROGRESS);
        
        return listBy(sc);
	}
	
	public List<AsyncJobVO> getExpiredJobs(Date cutTime, int limit) {
		SearchCriteria<AsyncJobVO> sc = expiringAsyncJobSearch.create();
		sc.setParameters("created", cutTime);
		Filter filter = new Filter(AsyncJobVO.class, "created", true, 0L, (long)limit);
		return listIncludingRemovedBy(sc, filter);
	}

	@DB
	public void resetJobProcess(long msid) {
		String sql = "UPDATE async_job SET job_status=2, job_result='job cancelled because of management server restart' where job_complete_msid=? OR (job_complete_msid IS NULL AND job_init_msid=?)";
		
        Transaction txn = Transaction.currentTxn();
        PreparedStatement pstmt = null;
        try {
            pstmt = txn.prepareAutoCloseStatement(sql);
            pstmt.setLong(1, msid);
            pstmt.setLong(2, msid);
            pstmt.execute();
        } catch (SQLException e) {
        	s_logger.warn("Unable to reset job status for management server " + msid, e);
        } catch (Throwable e) {
        	s_logger.warn("Unable to reset job status for management server " + msid, e);
        }
	}
}
