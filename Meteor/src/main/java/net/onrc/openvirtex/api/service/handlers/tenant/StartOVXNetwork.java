/*
 * ******************************************************************************
 *  Copyright 2019 Korea University & Open Networking Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ******************************************************************************
 *  Developed by Libera team, Operating Systems Lab of Korea University
 *  ******************************************************************************
 */
package net.onrc.openvirtex.api.service.handlers.tenant;

import java.util.HashMap;
import java.util.Map;

import net.onrc.openvirtex.api.service.handlers.ApiHandler;
import net.onrc.openvirtex.api.service.handlers.HandlerUtils;
import net.onrc.openvirtex.api.service.handlers.TenantHandler;
import net.onrc.openvirtex.elements.OVXMap;
import net.onrc.openvirtex.elements.network.OVXNetwork;
import net.onrc.openvirtex.exceptions.InvalidTenantIdException;
import net.onrc.openvirtex.exceptions.MissingRequiredField;
import net.onrc.openvirtex.exceptions.NetworkMappingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParamsType;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

import net.onrc.openvirtex.core.io.ClientChannelPipeline;



public class StartOVXNetwork extends ApiHandler<Map<String, Object>> {
	
	public static int tenantCount = 0;

    Logger log = LogManager.getLogger(StartOVXNetwork.class.getName());

    @Override
    public JSONRPC2Response process(final Map<String, Object> params) {
        JSONRPC2Response resp = null;

        try {
            final Number tenantId = HandlerUtils.<Number>fetchField(
                    TenantHandler.TENANT, params, true, null);

            HandlerUtils.isValidTenantId(tenantId.intValue());

            final OVXMap map = OVXMap.getInstance();
            final OVXNetwork virtualNetwork = map.getVirtualNetwork(tenantId
                    .intValue());

            virtualNetwork.boot();
            this.log.info("Booted virtual network {}",
                    virtualNetwork.getTenantId());
            tenantCount++;
            Map<String, Object> reply = new HashMap<String, Object>(
                    virtualNetwork.getDBObject());
            reply.put(TenantHandler.IS_BOOTED, virtualNetwork.isBooted());
            resp = new JSONRPC2Response(reply, 0);
            
            //booted virtual network log 

        } catch (final MissingRequiredField e) {
            resp = new JSONRPC2Response(new JSONRPC2Error(
                    JSONRPC2Error.INVALID_PARAMS.getCode(), this.cmdName()
                    + ": Unable to start virtual network : "
                    + e.getMessage()), 0);
        } catch (final InvalidTenantIdException e) {
            resp = new JSONRPC2Response(new JSONRPC2Error(
                    JSONRPC2Error.INVALID_PARAMS.getCode(), this.cmdName()
                    + ": Invalid tenant id : " + e.getMessage()), 0);
        } catch (final NetworkMappingException e) {
            resp = new JSONRPC2Response(new JSONRPC2Error(
                    JSONRPC2Error.INVALID_PARAMS.getCode(), this.cmdName()
                    + ": " + e.getMessage()), 0);
        }
        return resp;
    }

    @Override
    public JSONRPC2ParamsType getType() {
        return JSONRPC2ParamsType.OBJECT;
    }
}
