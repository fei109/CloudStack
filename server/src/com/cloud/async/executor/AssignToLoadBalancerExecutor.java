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

package com.cloud.async.executor;

import java.lang.reflect.Type;
import java.util.Map;

import org.apache.log4j.Logger;

import com.cloud.async.AsyncJobManager;
import com.cloud.async.AsyncJobVO;
import com.cloud.async.BaseAsyncJobExecutor;
import com.cloud.serializer.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AssignToLoadBalancerExecutor extends BaseAsyncJobExecutor {
    public static final Logger s_logger = Logger.getLogger(AssignToLoadBalancerExecutor.class.getName());

    @Override
    public boolean execute() {
        /*
        if (getSyncSource() == null) {
            Gson gson = GsonHelper.getBuilder().create();
            AsyncJobManager asyncMgr = getAsyncJobMgr();
            AsyncJobVO job = getJob();
            Type mapType = new TypeToken<Map<String, String>>() {}.getType();

            Map<String, String> params = gson.fromJson(job.getCmdInfo(), mapType);
            for (Object paramObj : params.values()) {
                s_logger.error("%%% %%% deserialized param: " + paramObj);
            }
            LoadBalancerParam param = gson.fromJson(job.getCmdInfo(), LoadBalancerParam.class);
            asyncMgr.syncAsyncJobExecution(job.getId(), "Router", param.getDomainRouterId());

            // always true if it does not have sync-source
            return true;
        } else {
            Gson gson = GsonHelper.getBuilder().create();
            AsyncJobManager asyncMgr = getAsyncJobMgr();
            AsyncJobVO job = getJob();

            LoadBalancerParam param = gson.fromJson(job.getCmdInfo(), LoadBalancerParam.class);
//            return asyncMgr.getExecutorContext().getNetworkMgr().executeAssignToLoadBalancer(this, param);
            return true;
        }
        */
        return true;
    }
}
