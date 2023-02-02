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
package net.onrc.openvirtex.elements.host;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class HostSerializer implements JsonSerializer<Host> {

    @Override
    public JsonElement serialize(Host host, Type t, JsonSerializationContext c) {
        final JsonObject result = new JsonObject();
        result.addProperty("hostId", host.getHostId());
        result.addProperty("ipAddress", host.getIp().toSimpleString());
        result.addProperty("mac", host.getMac().toString());
        result.addProperty("dpid", host.getPort().getParentSwitch()
                .getSwitchName());
        result.addProperty("port", host.getPort().getPortNumber());
        return result;
    }
}
