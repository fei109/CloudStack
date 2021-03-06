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

import com.cloud.api.ApiConstants;
import com.cloud.api.BaseCmd;
import com.cloud.api.Implementation;
import com.cloud.api.Parameter;
import com.cloud.api.ServerApiException;
import com.cloud.api.response.AccountResponse;
import com.cloud.api.response.UserResponse;
import com.cloud.user.UserAccount;

@Implementation(description="Creates an account", responseObject=UserResponse.class)
public class CreateAccountCmd extends BaseCmd {
    public static final Logger s_logger = Logger.getLogger(CreateAccountCmd.class.getName());

    private static final String s_name = "createaccountresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name=ApiConstants.ACCOUNT, type=CommandType.STRING, description="Creates the user under the specified account. If no account is specified, the username will be used as the account name.")
    private String accountName;

    @Parameter(name=ApiConstants.ACCOUNT_TYPE, type=CommandType.LONG, required=true, description="Type of the account.  Specify 0 for user, 1 for root admin, and 2 for domain admin")
    private Long accountType;

    @Parameter(name=ApiConstants.DOMAIN_ID, type=CommandType.LONG, description="Creates the user under the specified domain.")
    private Long domainId;

    @Parameter(name=ApiConstants.EMAIL, type=CommandType.STRING, required=true, description="email")
    private String email;

    @Parameter(name=ApiConstants.FIRSTNAME, type=CommandType.STRING, required=true, description="firstname")
    private String firstname;

    @Parameter(name=ApiConstants.LASTNAME, type=CommandType.STRING, required=true, description="lastname")
    private String lastname;

    @Parameter(name=ApiConstants.PASSWORD, type=CommandType.STRING, required=true, description="Hashed password (Default is MD5). If you wish to use any other hashing algorithm, you would need to write a custom authentication adapter See Docs section.")
    private String password;

    @Parameter(name=ApiConstants.TIMEZONE, type=CommandType.STRING, description="Specifies a timezone for this command. For more information on the timezone parameter, see Time Zone Format.")
    private String timezone;

    @Parameter(name=ApiConstants.USERNAME, type=CommandType.STRING, required=true, description="Unique username.")
    private String username;
    
    @Parameter(name=ApiConstants.NETWORK_DOMAIN, type=CommandType.STRING, description="Network domain name of the Vms that belong to the domain")
    private String networkdomain;


    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public String getAccountName() {
        return accountName;
    }

    public Long getAccountType() {
        return accountType;
    }

    public Long getDomainId() {
        return domainId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPassword() {
        return password;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getUsername() {
        return username;
    }

    public String getNetworkdomain() {
        return networkdomain;
    }
    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }
    
    @Override
    public void execute(){
        UserAccount user = _accountService.createAccount(this);
        if (user != null) {
            AccountResponse response = _responseGenerator.createUserAccountResponse(user);
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        } else {
            throw new ServerApiException(BaseCmd.INTERNAL_ERROR, "Failed to create a user account");
        }
    }
}