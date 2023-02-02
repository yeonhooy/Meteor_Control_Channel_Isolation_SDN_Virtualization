package net.onrc.openvirtex.elements.datapath.role;

import java.text.DateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.onrc.openvirtex.core.io.ClientChannelPipeline;
import net.onrc.openvirtex.elements.network.PhysicalNetwork;


import java.util.concurrent.atomic.AtomicReference;

import net.onrc.openvirtex.exceptions.UnknownRoleException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.projectfloodlight.openflow.protocol.OFControllerRole;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.jboss.netty.handler.traffic.*;
import org.jboss.netty.channel.ChannelPipeline;

public class Sincon {
	public static long adaptiveLimit = 700;
	
	
}
