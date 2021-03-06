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
package com.cloud.api.commands;

import org.apache.log4j.Logger;

import com.cloud.api.BaseCmd;
import com.cloud.api.Implementation;
import com.cloud.api.ServerApiException;
import com.cloud.api.response.TemplateResponse;
import com.cloud.template.VirtualMachineTemplate;

@Implementation(description="Updates attributes of a template.", responseObject=TemplateResponse.class)
public class UpdateTemplateCmd extends UpdateTemplateOrIsoCmd {
	public static final Logger s_logger = Logger.getLogger(UpdateTemplateCmd.class.getName());
    private static final String s_name = "updatetemplateresponse";

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////
    
    @Override
    public Boolean isBootable() {
        return null;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////
    
    @Override
    public String getCommandName() {
        return s_name;
    }
    
    @SuppressWarnings("unchecked")
    public TemplateResponse getResponse() {
       return null;
    }
    
    @Override
    public void execute(){
        VirtualMachineTemplate result = _mgr.updateTemplate(this);
        if (result != null) {
            TemplateResponse response = _responseGenerator.createIsoResponse(result);
            response.setObjectName("template");
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        } else {
            throw new ServerApiException(BaseCmd.INTERNAL_ERROR, "Failed to update template");
        }
    }
}
