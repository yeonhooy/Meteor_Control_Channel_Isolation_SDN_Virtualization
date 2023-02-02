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
package net.onrc.openvirtex.core.io;

import java.util.concurrent.ThreadPoolExecutor;

import net.onrc.openvirtex.core.OpenVirteXController;
import net.onrc.openvirtex.elements.network.PhysicalNetwork;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
//import org.jboss.netty.handler.traffic.ChannelTrafficShapingHandler;

public class SwitchChannelPipeline extends OpenflowChannelPipeline {

    Logger log = LogManager.getLogger(SwitchChannelPipeline.class.getName());

    private ExecutionHandler eh = null;

    public SwitchChannelPipeline(
            final OpenVirteXController openVirteXController,
            final ThreadPoolExecutor pipelineExecutor) {
        super();
        this.ctrl = openVirteXController;
        this.pipelineExecutor = pipelineExecutor;
        this.timer = PhysicalNetwork.getTimer();
        this.idleHandler = new IdleStateHandler(this.timer, 20, 25, 0);
        this.readTimeoutHandler = new ReadTimeoutHandler(this.timer, 30);
        this.eh = new ExecutionHandler(this.pipelineExecutor);

        //this.log.info("*******SwitchChannelPipeline created");
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
    	final int M = 1024 * 1024; //M = 1MB 
        final SwitchChannelHandler handler = new SwitchChannelHandler(this.ctrl);
        //ChannelTrafficShapingHandler channelTrafficShapingHandler = new ChannelTrafficShapingHandler(timer,15*M, 10*M);
      
        final ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("ofmessagedecoder", new OVXMessageDecoder());
        pipeline.addLast("ofmessageencoder", new OVXMessageEncoder());
        pipeline.addLast("idle", this.idleHandler);
        pipeline.addLast("timeout", this.readTimeoutHandler);
        //pipeline.addLast("channel_Traffic_ShpingHandler",  new ChannelTrafficShapingHandler(this.timer,15*M, 10*M));

        pipeline.addLast("handshaketimeout", new HandshakeTimeoutHandler(
                handler, this.timer, 15));

        pipeline.addLast("pipelineExecutor", eh);
        pipeline.addLast("handler", handler);
        
        this.log.info("*******SwitchChannelPipeline getPipeline()*********");
        return pipeline;
    }

}
