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

// sincon main -- yeonhooy

package net.onrc.openvirtex.elements.datapath.role;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import net.onrc.openvirtex.core.io.ClientChannelPipeline;
import net.onrc.openvirtex.elements.datapath.XidPair;
import net.onrc.openvirtex.elements.datapath.XidTranslator;
import net.onrc.openvirtex.elements.network.OVXNetwork;
import net.onrc.openvirtex.elements.network.PhysicalNetwork;
import net.onrc.openvirtex.core.OpenVirteX;
import net.onrc.openvirtex.core.OpenVirteXController;
import net.onrc.openvirtex.core.cmd.Cmdinjava;
import net.onrc.openvirtex.messages.OVXFlowMod;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import net.onrc.openvirtex.exceptions.UnknownRoleException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.projectfloodlight.openflow.protocol.OFControllerRole;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.jboss.netty.handler.traffic.*;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.TimerTask;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.traffic.GlobalChannelTrafficShapingHandler;


import net.onrc.openvirtex.api.service.handlers.tenant.StartOVXNetwork;;

public class RoleManager implements TimerTask {

    private static Logger log = LogManager.getLogger(RoleManager.class
            .getName());
    private HashMap<Channel, Role> state;
    private final AtomicReference<HashMap<Channel, Role>> currentState;
    private Channel currentMaster;
    
    public static int[][] firstPacketIntime = new int [100][300];
    public static long[][] addReadThroughputStack = new long[100][300];
    public static long[][] addWriteThroughputStack = new long[100][300];
    public static int[] packetinCount = new int [300];
    public static int[][] statMsgCount = new int [100][300]; 
    public static long[][] updateKvalue = new long [100][300];
    public static long[][] currentKvalue = new long [100][300];
    
    
    public static int windowSize = 20;
    public static int windowCount = 0;
    public static int monitorCount = 0;
    public static int monitorId = 1;
    public static int windowId = 1;
    
    //public static long ss = ClientChannelPipeline.firstThroughputLimit[1][1];
    public static ClientChannelPipeline cfact = null;
    public static OpenVirteXController ovxcon = null;
    public static PhysicalNetwork pyhnet = null;
    public static StartOVXNetwork sNetwork = null;
    
    //thread list
    public static List<Thread> channelThreadlist = new ArrayList<Thread>();
    public static int channelThreadId = 0;
    
    //public static OVXNetwork ovxnet = new OVXNetwork(null, null, 0);
    
    
    public enum Role {
        EQUAL,
        MASTER,
        SLAVE,
        NOCHANGE;
    }
    public static long M = 1024 * 1024;
    public static long K = 1024;
    public static int handler_num = 0;
    //public static long adaptiveLimit = 158*K;  //default = 47*M = 49852500
    //public static long defalutAdpativeLimit = 158*K; //default = 47*M
    public static long adaptiveLimit = 47*M;
    public static long defalutAdpativeLimit = 47*M;
    public static long adjustableThroughput = 625*K ;
    public static long globalThrouhgput = 700*K;
    public static long remainThroughput = 700*K;
    //public static long gloablthroughput = 316*K;
    public static long gloablthroughput = 94*M;
    public static long debtThroughput = 0;
    public static boolean initalCheck = false;
    
    public static int readKvalue(int w, int t, int s) {
		// Inference DATA
    	String usrdir = System.getProperty("user.dir");

		String fileName = usrdir+ "/MeteorPredictor/kvalue/k_"+String.valueOf(w)+"_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";

		FileReader fileReader = null;

		String limitValue = null;
		boolean existvalue = false;
		
		try {
			fileReader = new FileReader(fileName);
			existvalue=true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedReader reader = new BufferedReader(fileReader);

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				log.info("r values calculation : {}", line);
				limitValue = line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int kvalue = Integer.valueOf(limitValue);
		return kvalue;
	}
    
    
    public static class inferenceThread extends Thread{
    	
	    int tid;
	    int maxSw;
	    int wid;
	    //int threadId;
	    
	    public void setthreadId(int t, int s, int w){
	    	this.tid = t;
	    	this.maxSw = s;
	    	this.wid = w;
	    }
	  
    	@Override
    	public void run(){
    
    	String usrdir = System.getProperty("user.dir");
        	
    	//log.info("////////loading... {}-{}-{}",this.wid,this.tid,this.maxSw);
    	String kpath = usrdir+ "/MeteorPredictor/inverseData/inverse_"+String.valueOf(this.wid)+"_"+String.valueOf(this.tid)+"_"+String.valueOf(this.maxSw)+".txt";
    	File kfile = new File(kpath);
    	BufferedReader bufferedReader;
    	StringBuffer readBuffer;
    	String readlimit;
    	if(kfile.exists()){
    	log.info("checkALL!... window: {}-tenantid: {}-totalsw: {}",this.wid,this.tid,this.maxSw);
    	String inferenceCmd = "sh "+usrdir+"/MeteorPredictor/inference.sh "+String.valueOf(this.wid)+" "+String.valueOf(this.tid)+" "+String.valueOf(this.maxSw);	
    	//log.info("cmd: {}", inferenceCmd);
    	//String inferenceCmd = "touch "+String.valueOf(this.wid)+"_"+String.valueOf(this.tid)+"_"+String.valueOf(this.swid)+".txt";
    	//String[] callCmd = {"/bin/bash","-c",inferenceCmd};
        //final Cmdinjava cmdline = new Cmdinjava();
        //Map map = cmdline.execCommand(callCmd);
        long startcmd = System.currentTimeMillis();
        Runtime rt = Runtime.getRuntime();
        Process pc = null;
        try{
        	pc = rt.exec(inferenceCmd);
        	bufferedReader = new BufferedReader(new InputStreamReader(pc.getInputStream()));
        	
        	String line = null;
        	readBuffer = new StringBuffer();
        	
        	while((line = bufferedReader.readLine()) != null){
        		readBuffer.append(line);
        		readBuffer.append(", ");
        		String[] resultline = line.split(" ");
        		int tnum = Integer.valueOf(resultline[0]);
        		int swnum = Integer.valueOf(resultline[1]);
        		long knum = Long.valueOf(resultline[2]);
        		updateKvalue[tnum][swnum] = knum;
        		
        		//log.info("@@@@@@@ result line : {} @@@@@@@ ",knum);
        	}
//        
        	log.info("^^^^^^^^^ r limit value : {} ^^^^^^^^",readBuffer);
//        	
        	
        	
        }catch(IOException e){
        	log.info("error in channelThread({}/{}",this.tid,this.wid);
        	e.printStackTrace();
        }finally{
        	try {
        		InputStream inputStream = pc.getInputStream();
        		InputStream errStream = pc.getErrorStream();
        		try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		try {
					errStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				pc.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	pc.destroy();
        }    	
    		//this.wid++;
    	}
//  
    	
    	
    	}
    }
    
	public static class channelThread extends Thread{
	    	
			ChannelGroup cg = ClientChannelPipeline.cg;
		    int tid;
		    int swid;
		    int wid;
		    int threadId;
		    public void setchannelId(int t, int s){
		    	this.tid = t;
		    	this.swid = s;
		    }
		    public void setwindowId(int w){
		    	this.wid = w;
		    }
		    public void setThreadId(int threadId){
		    	this.threadId = threadId;
		    }
		    public int getThreadId(){
		    	return this.threadId;
		    }
	    	@Override
	    	public void run(){
	    	while(true){
	    	//log.info("////////loading... {}-{}-{}",this.wid,this.tid,this.swid);
    		String usrdir = System.getProperty("user.dir");
	        	
	    	String kpath = usrdir + "/MeteorPredictor/inverseData/inverse_"+String.valueOf(this.wid)+"_"+String.valueOf(this.tid)+"_"+String.valueOf(this.swid)+".txt";
	    	File kfile = new File(kpath);
	    	BufferedReader bufferedReader;
	    	StringBuffer readBuffer;
	    	String readlimit;
	    	if(kfile.exists()){
	    	log.info("check!... {}-{}-{}",this.wid,this.tid,this.swid);
	    	String inferenceCmd = "sh "+usrdir+"/MeteorPredictor/inference.sh "+String.valueOf(this.wid)+" "+String.valueOf(this.tid)+" "+String.valueOf(this.swid);	
	    	//String inferenceCmd = "touch "+String.valueOf(this.wid)+"_"+String.valueOf(this.tid)+"_"+String.valueOf(this.swid)+".txt";
	    	//String[] callCmd = {"/bin/bash","-c",inferenceCmd};
	        //final Cmdinjava cmdline = new Cmdinjava();
	        //Map map = cmdline.execCommand(callCmd);
	        long startcmd = System.currentTimeMillis();
	        Runtime rt = Runtime.getRuntime();
	        Process pc = null;
	        try{
	        	pc = rt.exec(inferenceCmd);
	        	bufferedReader = new BufferedReader(new InputStreamReader(pc.getInputStream()));
	        	
	        	String line = null;
	        	readBuffer = new StringBuffer();
	        	
	        	while((line = bufferedReader.readLine()) != null){
	        		readBuffer.append(line);
	        	}
	        	readlimit = readBuffer.toString();
	        	//log.info("^^^^^^^^^ readlimit : {} ^^^^^^^^",readlimit);
	        	long kvalue = Long.valueOf(readlimit);
	        	//log.info("^^^^^^^^^ kvlaue : {} ^^^^^^^^",kvalue);
	        
	        	updateKvalue[this.tid][this.swid] = kvalue;
	        	long duringTime = System.currentTimeMillis() - startcmd;
	        	//log.info("duringTime({}/{}/{} = {}",this.wid,this.tid,this.swid,duringTime);
		        //gct.setReadLimit(kvalue);
	        	
	        	
	        }catch(IOException e){
	        	log.info("error in channelThread({}/{}",this.tid,this.wid);
	        	e.printStackTrace();
	        }finally{
	        	try {
	        		InputStream inputStream = pc.getInputStream();
	        		InputStream errStream = pc.getErrorStream();
	        		try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		try {
						errStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pc.waitFor();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	pc.destroy();
	        }
	        
	    		this.wid++;
	    	}
//	    	else{
//	    		if (this.wid >1){
//	    		long currentTime = System.currentTimeMillis();
//	    		log.info("({}/{}/{}) no k-value yet ({})",this.wid,this.tid,this.swid,currentTime);
//	    		}
//	    	}
	    	}
	    	
	    	}
	
	    }
    //private static ChannelGroup cg;
	
    static class ChannelQueue{
    	//[throughput,size,wait,interval,windowcount,hostnum,linknum,packetin,stat,flowmod]
    	private  long[][][] shaperQueue  = new long [100][70][12];
    	private  int queuevalue;
    	public ChannelQueue(int qv){ 
    												this.queuevalue=qv;}
    	public int getKey() {return queuevalue;}
    	public void setKey(int qv){this.queuevalue = qv;}
    	
    	public long[][][] getQueue() {return shaperQueue;}
    	public long getThroughput(int wID, int wCount){ return this.shaperQueue[wID][wCount][0];}
    	public long getSize(int wID, int wCount){ return this.shaperQueue[wID][wCount][1];}
    	public long getWait(int wID, int wCount){ return this.shaperQueue[wID][wCount][2];}
    	public long getInterval(int wID, int wCount){ return this.shaperQueue[wID][wCount][3];}
    	public long getWcount(int wID, int wCount){ return this.shaperQueue[wID][wCount][4];}
    	public long getHostNum(int wID, int wCount){ return this.shaperQueue[wID][wCount][5];}
    	public long getLinkNum(int wID, int wCount){ return this.shaperQueue[wID][wCount][6];}
    	public long getPacketinNum(int wID, int wCount){ return this.shaperQueue[wID][wCount][7];}
    	public long getStatNum(int wID, int wCount){ return this.shaperQueue[wID][wCount][8];}
    	public long getFlowmodNum(int wID, int wCount){ return this.shaperQueue[wID][wCount][9];}
    	
    	public void setThroughput(int wID, int wCount, long newValue){ this.shaperQueue[wID][wCount][0] = newValue; }
    	public void setSize(int wID, int wCount, long newValue){ this.shaperQueue[wID][wCount][1] = newValue; }
    	public void setWait(int wID, int wCount, long newValue){ this.shaperQueue[wID][wCount][2] = newValue; }
    	public void setInterval(int wID, int wCount, long newValue){ this.shaperQueue[wID][wCount][3] = newValue; }
    	public void setWcount(int wID, int wCount, long newValue){ this.shaperQueue[wID][wCount][4] = newValue; }
    	public void setHostNum(int wID, int wCount, long newValue){ this.shaperQueue[wID][wCount][5] = newValue; }
    	public void setLinkNum(int wID, int wCount, long newValue){ this.shaperQueue[wID][wCount][6] = newValue; }
    	public void setPacketinNum(int wID, int wCount, long newValue){ this.shaperQueue[wID][wCount][7] = newValue; }
    	public void setStatNum(int wID, int wCount, long newValue){ this.shaperQueue[wID][wCount][8] = newValue; }
    	public void setFlowmodNum(int wID, int wCount, long newValue){ this.shaperQueue[wID][wCount][9] = newValue; }
    	

    }
    
    
    public static ConcurrentHashMap<Integer, ChannelQueue> shaperQueues = new ConcurrentHashMap<Integer, ChannelQueue>();
    
    //Sincon integer;
    public static long[][] datacollectSize = new long[10][150];
    public static long[][] cumulative_throughputs = new long [10][150];
    public static int[][] message_counts = new int [10][150];
    public static long[][] average_throughput = new long [10][150];
    public static int[][] initial_Check = new int[10][150];
    public static long[][] lastMonitortime = new long[10][150];
    public static int initalCheckAll = 0; 
    
    public static long[][] tmp_byte = new long[10][150];
    public static long[][] tmp_lastime = new long[10][150];
    public static long[][] cumByte_window = new long[10][150];
   
    public static long[][] lastCumulativeReadByte = new long [10][150];
    public static long[][] lastTimenow = new long [10][150];
    public static long[][] firstCheckInterval = new long [10][150]; 
    public static long[][] readBytebuffer = new long [10][150];
    public static int[][] monitoringCount = new int [10][150];
    public static long[][] limitavailablenow = new long [10][150];
    public static long [][] buffersize = new long [10][150];
    
    public static long[][] bufferWaitTime = new long [10][150];
    public static int[][] whereiscode = new int [10][150];
    
   
   
    //([tenant][switch])[windoId][count][feature]
    //[throughput,size,wait,interval,windowcount,hostnum,linknum,packetin,stat,flowmod]
    
    
    private Timer timer = PhysicalNetwork.getTimer();
    private Integer refreshInterval = 10;
    private boolean stopTimer = false;
    //private Timer m_timer;
    //public static long adaptiveLimit = 700*M;  //default = 700
    //public static long defalutAdpativeLimit = 700*M;
    
    
    public RoleManager() {
        this.state = new HashMap<Channel, Role>();
        this.currentState = new AtomicReference<HashMap<Channel, Role>>(state);
        
        
    }
    @Override
	public void run(Timeout timeout) throws Exception {
		//log.info("*********************data collector*************************");
        //sendPortStatistics();
        //sendFlowStatistics(0, (short) 0);
		//testCollector();
		//test_dataCollectorTimer();
    	//log.info("^^ yyh: {}/{}/{}",tenatntNum,channelNum,tChannelnum);
    	//dataCollectorTimer();
    	windowDataCollectorTimer();
    	//monitorInverseShapingTimer();
        //allDatacollector();
    	if (!this.stopTimer) {
//            log.info("Scheduling stats collection in {} miliseconds",
//                    this.refreshInterval);
            timeout.getTimer().newTimeout(this, refreshInterval,
                    TimeUnit.MILLISECONDS);
        }
		
	}
    public void datacollect_start() {
        /*
         * Initially start polling quickly. Then drop down to configured value
         */
        log.info("*** Starting data collection thread for ");
        timer.newTimeout(this, 1, TimeUnit.SECONDS);
    }

    public void datacollect_stop() {
        log.info("*** Stopping data collection thread for {}");
        this.stopTimer = true;
    }
    private HashMap<Channel, Role> getState() {
        return new HashMap<>(this.currentState.get());
    }

    private void setState() {
        this.currentState.set(this.state);
    }

    public synchronized void addController(Channel chan) {
        if (chan == null) {
            return;
        }
        this.state = getState();
        this.state.put(chan, Role.EQUAL);
        setState();
      
    }

    public synchronized void setRole(Channel channel, Role role)
            throws IllegalArgumentException, UnknownRoleException {
        if (!this.currentState.get().containsKey(channel)) {
            throw new IllegalArgumentException("Unknown controller "
                    + channel.getRemoteAddress());
        }
        this.state = getState();
        log.info("Setting controller {} to role {}",
                channel.getRemoteAddress(), role);
        switch (role) {
            case MASTER:
                if (channel == currentMaster) {
                    this.state.put(channel, Role.MASTER);
                    break;
                }
                this.state.put(currentMaster, Role.SLAVE);
                this.state.put(channel, Role.MASTER);
                this.currentMaster = channel;
                break;
            case SLAVE:
                if (channel == currentMaster) {
                    this.state.put(channel, Role.SLAVE);
                    currentMaster = null;
                    break;
                }
                this.state.put(channel, Role.SLAVE);
                break;
            case EQUAL:
                if (channel == currentMaster) {
                    this.state.put(channel, Role.EQUAL);
                    this.currentMaster = null;
                    break;
                }
                this.state.put(channel, Role.EQUAL);
                break;
            case NOCHANGE:
                // do nothing
                break;
            default:
                throw new UnknownRoleException("Unkown role : " + role);

        }
        setState();

    }

    public boolean canSend(Channel channel, OFMessage m) {
        Role r = this.currentState.get().get(channel);
        if (r == Role.MASTER || r == Role.EQUAL) {
            return true;
        }
        switch (m.getType()) {
            case GET_CONFIG_REQUEST:
            case QUEUE_GET_CONFIG_REQUEST:
            case PORT_STATUS:
            case STATS_REQUEST:
                return true;
            default:
                return false;
        }
    }

    public boolean canReceive(Channel channel, OFMessage m) {
        Role r = this.currentState.get().get(channel);
//        log.info(r.toString());

        if (r == Role.MASTER || r == Role.EQUAL) {
//            log.info("r == Role.MASTER || r == Role.EQUAL");
            return true;
        }else{
//            log.info(" not r == Role.MASTER || r == Role.EQUAL");
        }
        
       // log.info("can Receive test: msg type {}",m.getType());

        switch (m.getType()) {
            case GET_CONFIG_REPLY:
            case QUEUE_GET_CONFIG_REPLY:
            case PORT_STATUS:
            case STATS_REPLY:
                return true;
            default:
                return true;
        }
        
    }

    public Role getRole(Channel channel) {
        return this.currentState.get().get(channel);
    }

    public long checkAndSend(Channel c, OFMessage m, int tId, int swDec) {
    	
        //log.info("checkAndSend");
        long currentThroughput = -1;
        long averageThroughput = 0;
    	long cumulativesValue = 0;
    	if (canReceive(c, m)) {
            if (c != null && c.isOpen()) {
  
            	//first or end ?
                c.write(Collections.singletonList(m));
                
                switch (m.getType()) {
                case PACKET_IN:
                	packetinCount[tId]++;
                    
                	if (firstPacketIntime[tId][swDec] != 1 ){
                		firstPacketIntime[tId][swDec]=1;
                		
                		//timestamp
                		long start = System.currentTimeMillis();
                		//log.info("{} firstPackettime {} {} {}",m.getType(),tId,swDec,start);
                	}
                	break;
                case STATS_REPLY:
                	statMsgCount[tId][swDec]++;
                	break;
                case FLOW_MOD:
                		//timestamp
                		long start = System.currentTimeMillis();
                		//log.info("{} FLOMODtime {} {} {}",m.getType(),tId,swDec,start);
                		break;
                default:
                	break;
                    
            }
                
                GlobalChannelTrafficShapingHandler gct = (GlobalChannelTrafficShapingHandler) c.getPipeline().get("globalchanneltrafficShapingHandler");
               

                
                currentThroughput = sinconMonitor(gct,tId,swDec);
                int currentChannelNum = ClientChannelPipeline.channelnum; 
                // String result
                //log.info("sinconString: {}",sinconString(m,gct)); 
            	if(currentThroughput >= 0){
            		cumulative_throughputs[tId][swDec]+=currentThroughput;
                    message_counts[tId][swDec] += 1;
                    average_throughput[tId][swDec] = cumulative_throughputs[tId][swDec] / message_counts[tId][swDec];
            	}else if(currentThroughput == -100){ // intialize
            		// case of adaptive throguhput = 0 -> initailize
            		if(initalCheckAll == 0){
            			cumulativesValue = 0;
            			for(int i=1;i<2;i++){
        		    		for(int j=1;j<130;j++){
        		    			cumulativesValue += average_throughput[i][j];
        		    		}
        		    	}
            		}
            		if(initalCheckAll != currentChannelNum){
            			if (initial_Check[tId][swDec] == 0){
            				initalCheckAll++;
            				log.info("( {} / {} ) Channel INTIALIZE---- : {} / 40",tId,swDec,initalCheckAll);
            				averageThroughput = average_throughput[tId][swDec];
            				initial_Check[tId][swDec]++;
            				boolean finishInitial = false;
            				//finishInitial = sinconInitialize(cumulativesValue, initalCheckAll ,averageThroughput, c);
            				if(finishInitial == true){
                    			log.info("Start Initialize throughput!!");
                    			cumulative_throughputs = new long[100][300];
                    			message_counts = new int[100][300];
                    			average_throughput = new long[100][300];
                    			initial_Check = new int[100][300];
                    			initalCheckAll = 0;	
                    		}
            			}
            		}else{
            			initalCheckAll = 0;
            		}
            	}else if(currentThroughput == -1){
            		log.error("send Msg error: No output value for Current Throughput");
            	}
            }
        }
    	return currentThroughput;
    }
    
    
    public long sinconMonitor(GlobalChannelTrafficShapingHandler gct, int tId, int swDec){
    	if(initalCheck == true){
    		adaptiveLimit -= debtThroughput;
    		
    		if(adaptiveLimit < 0){
    			log.warn("No more adpatable throughput, left debt = {} ", adaptiveLimit);
    			adaptiveLimit = 0;
    		}
    		debtThroughput = 0;
    		initalCheck = false;
    	}
    	
    	
    	//0. initial throughput limit
    	long firstLimit = ClientChannelPipeline.firstThroughputLimit[tId][swDec];
    	//long firstLimit = this.cfact.getFirstLimit(tId,swDec);
    	
    	//message count
    	int msgCount = message_counts[tId][swDec];
    	
    	//1. montoring Interval time 
    	long monitorInterval = 0;
    	long currentTime = System.currentTimeMillis();
    	if (lastMonitortime[tId][swDec] == 0){
    		lastMonitortime[tId][swDec] = currentTime;
    		monitorInterval = 0;
    	}
    	else{
    		monitorInterval = currentTime - lastMonitortime[tId][swDec];
    		//lastMonitortime[tId][swDec] = currentTime;
    	}
    	
    	//2. current read Limit
    	long readLimit = gct.getReadChannelLimit(); //4000
    	//3. current write Limit
    	long writeLimit = gct.getWriteChannelLimit(); //4000
    	
    	
    	
    	
    	//log.info("channel limit monitor : {} {} {} {}",tId,swDec,readLimit,writeLimit);
    	long standardReadThroughut = (long) (readLimit * 0.9); //3600 
    	long standardWriteThroughut = (long) (writeLimit * 0.9); 
    	long addReadThroughput = (long) (readLimit * 0.1);  //400
    	long addWriteThroughput = (long) (writeLimit * 0.1);
    	
    	long futureReadThroughput = readLimit + addReadThroughput;  //bonus +10% = 4400
    	long futureWriteThroughput = writeLimit + addWriteThroughput; //bonus +10% = 4400
    	
    	//4. channelNum
    	int currentChannelNum = ClientChannelPipeline.channelnum;
    	//5. tenant Num 
    	//int currentTenatntNum = StartOVXNetwork.tenantCount;
    	int currentTenatntNum = ClientChannelPipeline.tenantnum;
    	//6. switch Num
    	int phySwitchNum = PhysicalNetwork.getPhySwitchNum();
    	
    	//7. vn switch counter
    	//int vnSwitchNum = this.ovxnet.getLinkCounter();
    	// vn host counter
    	//int vnHostNum = this.ovxnet.getHostCounter();
    	
    	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    	String visorName = "";
    	long currentReadthroughput = gct.getTrafficCounter().getLastReadThroughput();
		long currentWritethroughput = gct.getTrafficCounter().getLastWriteThroughput();
    	
		boolean truevalue = true;
		//original ovx 
    	if(truevalue){
    		addReadThroughputStack[tId][swDec] += 0;
         	addWriteThroughputStack[tId][swDec] += 0;
         	visorName="OVXVanila";
    	}
    	
    	//Sincon Algorithm
    	else{
    		visorName="Sincon";
    		
	    	if(currentReadthroughput > standardReadThroughut ){
	         	
	         	if(currentReadthroughput > gct.getReadChannelLimit()){
	         	   //over throughput limit 
	         		log.info("Over Throughput>>");
	         	}
	         	
	         	gct.setReadChannelLimit(futureReadThroughput);
	         	gct.setWriteChannelLimit(futureWriteThroughput);
	         	adaptiveLimit -= addReadThroughput;
	         	addReadThroughputStack[tId][swDec] += addReadThroughput;
	         	addWriteThroughputStack[tId][swDec] += addWriteThroughput;
	         	         	
	         }
	    	
	    	if(adaptiveLimit < 0){
	     		debtThroughput += Math.abs(addReadThroughput);
	     		adaptiveLimit=0;
	     	}
	     	
    	}
     	
    	//7.addthroughputLimit
    	long addstack_read = addReadThroughputStack[tId][swDec];

    	long addstack_write = addWriteThroughputStack[tId][swDec];
     	
    	//log.info("{}-Monitor: {} {} CurrentThroughput {} Limit {} Adapt {} debt {}",visorName,tId,swDec,gct.getTrafficCounter().getLastReadThroughput(),gct.getReadChannelLimit(),adaptiveLimit, debtThroughput);
    	
    	//data collect for machine learning 
    	//dataCollectorTimer();
    	//this.dataCollector(currentTime,tId,swDec,currentChannelNum, currentTenatntNum, phySwitchNum, firstLimit, monitorInterval, readLimit,addstack_read,currentReadthroughput,remainThroughput,msgCount);
    	
    	
    	if(adaptiveLimit ==0){ // adaptiveLimit go zero -> initailaize to average throughput average
    		return -100;
     	}
    	return gct.getTrafficCounter().getLastReadThroughput();
    }
    public String sinconString(OFMessage m, GlobalChannelTrafficShapingHandler nct){
    	String montiorString = " "; 
    	montiorString += 
    				" vSwitchId : "+ m.getXid()+
         			" messageType : "+ m.getType()+ 
         			//" // Traffic Count`s current : " +ct.getTrafficCounter().toString() +
         			//" // Last Data size: " +nct.getTrafficCounter().getLastReadBytes()+"/"+nct.getTrafficCounter().getLastWrittenBytes()+
         			" // Last Time: "+nct.getTrafficCounter().getLastTime()+
         			" // Last throughput: " + nct.getTrafficCounter().getLastReadThroughput() + " / "+ nct.getTrafficCounter().getLastWriteThroughput()+
         			" // inforimation: " + nct.getTrafficCounter().getCheckInterval() +
         			" // current Chanel Limit:  " + nct.getReadChannelLimit() + " / " + nct.getWriteChannelLimit()+
         			//" //cumulative:  " + gct.getTrafficCounter().getCumulativeReadBytes() + " / "+ gct.getTrafficCounter().getCumulativeWrittenBytes();
         			//" //cumTime: " + ct.getTrafficCounter().getLastCumulativeTime()+
         			" //queue size: "+ nct.queuesSize();
        
    	return montiorString;
    }
    public static boolean sinconInitialize(long cumulatives, int initalCheck_All, long average, Channel c){
        GlobalChannelTrafficShapingHandler gct = (GlobalChannelTrafficShapingHandler) c.getPipeline().get("globalchanneltrafficShapingHandler");
    	gct.setReadChannelLimit(average);
    	gct.setWriteChannelLimit(average);
    	if(initalCheck_All == 128){
    		adaptiveLimit = gloablthroughput - cumulatives;
        	initalCheck =true;
        	//c.getPipeline().removeLast();
        	//c.getPipeline().addLast(handler_name,  new GlobalChannelTrafficShapingHandler(PhysicalNetwork.getTimer(), 316*K,316*K,averageValue,averageValue));
        	log.info("SINCON-- Initialize Success!!!");
        	return true;
    	}
    	return false;
    	
    }
    public static void testCollector(){
    	String usrdir = System.getProperty("user.dir");
    	String tx_fileName = usrdir+"/Trafficmeter/tx_data.txt";
    	String rx_fileName = usrdir+"/Trafficmeter/rx_data.txt";
    	FileWriter fw = null;
    	BufferedWriter bw = null;
    	
    	try{
    		fw = new FileWriter(tx_fileName, true);
    		bw = new BufferedWriter(fw);
    		
    		bw.write("test");
    		bw.newLine();
    		
    		bw.flush();
    	}catch(IOException e){
    		log.error("{}",e);
    	}finally{
    		try{ fw.close();}catch(IOException e){}
    		try{ bw.close();}catch(IOException e){}
    	}
    }
    
    public static void test_dataCollectorTimer(){
    	log.info("... Traffic meter collecting ...");
    	String usrdir = System.getProperty("user.dir");
    	String tx_fileName = usrdir+"/Trafficmeter/tx_data.txt";
    	String rx_fileName = usrdir+"/Trafficmeter/rx_data.txt";
    	FileWriter fw = null;
    	BufferedWriter bw = null;
    	
    	//1. Current Time
    	long currentTime = System.currentTimeMillis();
    	//2. tenantNum
    	//int currentTenatntNum = StartOVXNetwork.tenantCount;
    	int currentTenatntNum = ClientChannelPipeline.tenantnum;
    	//3. channelNum
    	int currentChannelNum = ClientChannelPipeline.channelnum; 
    	int[] tenantChannelnum = ClientChannelPipeline.tenantChannelnum;
    	
    	//int 2 Stirng
    	String scurrentTime = String.valueOf(currentTime);
    	String smaxTenant = String.valueOf(currentTenatntNum);
    	String smaxChannel = String.valueOf(currentChannelNum);
    	

        		
    			
    			try{
    	    		fw = new FileWriter(tx_fileName, true);
    	    		bw = new BufferedWriter(fw);
    	    		
    	    		bw.write(scurrentTime);
    	    		bw.write(",");
    	    		bw.write(smaxChannel);
    	    		bw.write(",");
    	    		bw.write(smaxTenant);
    	    		bw.newLine();
    	    		
    	    		bw.flush();
    	    	}catch(IOException e){
    	    		log.error("{}",e);
    	    	}finally{
    	    		try{ fw.close();}catch(IOException e){}
    	    		try{ bw.close();}catch(IOException e){}
    	    	}
    		
    			
    		
    	}
    
    
    
    public static void dataCollectorTimer(){
    	log.info("... Traffic meter collecting ...");
    	String usrdir = System.getProperty("user.dir");
    	String tx_fileName = usrdir+"/Trafficmeter/tx_data.txt";
    	String rx_fileName = usrdir+"/Trafficmeter/rx_data.txt";
    	
    	FileWriter fw = null;
    	BufferedWriter bw = null;

    	//1. Current Time
    	long currentTime = System.currentTimeMillis();
    	//2. tenantNum
    	//int currentTenatntNum = StartOVXNetwork.tenantCount;
    	int currentTenatntNum = ClientChannelPipeline.tenantnum;
    	//3. channelNum
    	int currentChannelNum = ClientChannelPipeline.channelnum; 
    	int[] tenantChannelnum = ClientChannelPipeline.tenantChannelnum;
    	
    	//int 2 Stirng
    	String scurrentTime = String.valueOf(currentTime);
    	String smaxTenant = String.valueOf(currentTenatntNum);
    	String smaxChannel = String.valueOf(currentChannelNum);
    	
    	ChannelGroup cg = ClientChannelPipeline.cg;
    	for (int t=1; t<=currentTenatntNum; t++){
    		int tswNum = tenantChannelnum[t];
    		for (int s=1; s<=tswNum; s++){
    			datacollectSize[t][s]++;
    			//4. firstLimit
    			long firstLimit = ClientChannelPipeline.firstThroughputLimit[t][s];
    			
    			int channelId = OpenVirteXController.channelList[t][s];
    			//log.info("**** Channel ID : {}",channelId);
    			Channel c = cg.find(channelId);
    			GlobalChannelTrafficShapingHandler gct = (GlobalChannelTrafficShapingHandler) c.getPipeline().get("globalchanneltrafficShapingHandler");
    			
    			//5. readLimit
    			long readLimit = gct.getReadChannelLimit();
    			boolean limitcheck = readLimit>0 ? true:false;
    			
    			//6. writeLimit
    			long writeLimit = gct.getWriteChannelLimit();
    			
    			//7. current Read throughput
    			
    			//long currentReadthroughput = gct.getTrafficCounter().getLastReadThroughput();
    			
    			//8. current Write throughput
    			//long currentWritethroughput = gct.getTrafficCounter().getLastWriteThroughput();
    			
    			long nowCumulativeReadByte = gct.getTrafficCounter().getCumulativeReadBytes();
    			
    			
				long nowreadByte = nowCumulativeReadByte - lastCumulativeReadByte[t][s];
				long nowreadTime = currentTime - lastTimenow[t][s];
				
				if (!limitcheck){
					// no shaping 
					if (nowreadTime <=0 || nowreadTime > 15000){
						readBytebuffer[t][s] = 0;
						whereiscode[t][s] = 11;
					}
					else{
						whereiscode[t][s] = 12;
						readBytebuffer[t][s] = (nowreadByte/nowreadTime)*100;
					}
	    			
	    			lastCumulativeReadByte[t][s] = nowCumulativeReadByte;
	    			lastTimenow[t][s] = currentTime;
				}
				
				else{
					//with shaping 
					if (nowreadByte <0 || nowreadTime > 15000){
						whereiscode[t][s] = 21;
						readBytebuffer[t][s] = 0;
						bufferWaitTime[t][s] = 0;
						
					}
					else if(bufferWaitTime[t][s]==0){
						bufferWaitTime[t][s] = (nowreadByte*100)/readLimit;
						if (bufferWaitTime[t][s] == 0){
							whereiscode[t][s] = 22;
							readBytebuffer[t][s]=0;
							
						}
						else{
							bufferWaitTime[t][s] = bufferWaitTime[t][s] - nowreadTime;
							if(bufferWaitTime[t][s] >= 10){
								//wait available
								whereiscode[t][s] = 23;
								readBytebuffer[t][s] = readLimit;
							}
							else{
								//can not wait
								whereiscode[t][s] = 24;
								readBytebuffer[t][s] = (nowreadByte/nowreadTime)*100;
								bufferWaitTime[t][s] = 0;
							}
						}
					}
					
					else if(bufferWaitTime[t][s] != 0){
						bufferWaitTime[t][s] = bufferWaitTime[t][s] - nowreadTime;
						if(bufferWaitTime[t][s] >= 10){
							whereiscode[t][s] = 31;
							readBytebuffer[t][s] = readLimit;
							if (bufferWaitTime[t][s] < 100){
								whereiscode[t][s] = 32;
								bufferWaitTime[t][s]=0;
							}
							
						}
						else {
							whereiscode[t][s] = 33;
							readBytebuffer[t][s] = readLimit;
							bufferWaitTime[t][s]=0;
						}
					}
					
					lastCumulativeReadByte[t][s] = nowCumulativeReadByte;
	    			lastTimenow[t][s] = currentTime;
				}
    			
    			
    			
    			
    			//9. packetIn count
    			int packetInCount = packetinCount[t];
    			//10. statMsg count
    			int statCount = statMsgCount[t][s];
    			//11. flowmod count
    			int flowCount = OVXFlowMod.flow_counts[t][s];
    			long flowmodProtime = OVXFlowMod.processing_times[t][s];
    			String processingTime = String.valueOf(flowmodProtime);
    			//int 2 String
    			String sTid = String.valueOf(t);
    			String sSwdec = String.valueOf(s);
    			String sfirstLimit = String.valueOf(firstLimit);
    			String sreadLimit = String.valueOf(readLimit);
    			String swriteLimit = String.valueOf(writeLimit);
    			String scurrentReadthroughput = String.valueOf(readBytebuffer[t][s]);
    			//String scurrentWritethroughput = String.valueOf(currentWritethroughput);
    			String spacketInCount = String.valueOf(packetInCount);
    			String sstatCount = String.valueOf(statCount);
    			String sflowCount = String.valueOf(flowCount);
    			
    			
    			//
    			String slimitCheck = String.valueOf(limitcheck);
    			String nowcuReadbytes = String.valueOf(nowreadByte);
    			String snowreadTime = String.valueOf(nowreadTime);
    			String swaitbuffer = String.valueOf(bufferWaitTime[t][s]);
    			String swhereiscode = String.valueOf(whereiscode[t][s]);
    			
    			
    			
    			//edge sw, core sw for linear swtich. 
    	    	int hostNum = 0;
    	    	int swlink = 2;
    	    	int swNum = currentChannelNum;
    	    	if (s == 1 || s == swNum){
    	    		hostNum = 1;
    	    		swlink = 1;
    	    	}
    	    	String shostNum = String.valueOf(hostNum);
    	    	String slink = String.valueOf(swlink);
    			
    			if (datacollectSize[t][s] < 721){
    			try{
    	    		fw = new FileWriter(tx_fileName, true);
    	    		bw = new BufferedWriter(fw);
    	    		
    	    		bw.write(scurrentTime);
    	    		bw.write(",");
//    	    		bw.write(sTid);
//    	    		bw.write(",");
    	    		bw.write(sSwdec);
    	    		bw.write(",");
//    	    		bw.write(smaxChannel);
//    	    		bw.write(",");
//    	    		bw.write(smaxTenant);
//    	    		bw.write(",");
    	    		bw.write(shostNum);
    	    		bw.write(",");
    	    		bw.write(slink);
    	    		bw.write(",");
//    	    		bw.write(sreadLimit);
//    	    		bw.write(",");
//    	    		bw.write(sfirstLimit);
//    	    		bw.write(",");
//    	    		bw.write(sreadLimit);
//    	    		bw.write(",");
//    	    		bw.write(swriteLimit);
//    	    		bw.write(",");

    	    		//bw.write(scurrentWritethroughput);
    	    		//bw.write(",");
    	    		bw.write(spacketInCount);
    	    		bw.write(",");
    	    		bw.write(sstatCount);
    	    		bw.write(",");
    	    		bw.write(sflowCount);
    	    		bw.write(",");
    	    		bw.write(scurrentReadthroughput);
    	    		//bw.write(",");
//    	    		bw.write(slimitCheck);
//    	    		bw.write(",");
//    	    		
//    	    		bw.write(nowcuReadbytes);
//    	    		bw.write(",");
//    	    		bw.write(snowreadTime);
//    	    		bw.write(",");
//    	    		bw.write(swaitbuffer);
//    	    		bw.write(",");
//    	    		bw.write(swhereiscode);
    	    		bw.newLine();
    	    		
    	    		
    	    		
    	    		bw.flush();
    	    	}catch(IOException e){
    	    		log.error("{}",e);
    	    	}finally{
    	    		try{ fw.close();}catch(IOException e){}
    	    		try{ bw.close();}catch(IOException e){}
    	    	}
    			}
    		
    			
    		}
    	}
    	
   
    }
    public static boolean readNumTenants(){
		try{
			String usrdir = System.getProperty("user.dir");
            File file = new File(usrdir+"/config.txt");
            FileReader filereader = new FileReader(file);
            BufferedReader bufReader = new BufferedReader(filereader);
            String line = "";
            while((line = bufReader.readLine()) != null){
            	ClientChannelPipeline.tenantnum = Integer.parseInt(line);
            	log.info("runDynamicNumTenants: {}", ClientChannelPipeline.tenantnum);
            }
            bufReader.close();
            filereader.close();
        }catch (FileNotFoundException e) {
            // TODO: handle exception
        }catch(IOException e){
            System.out.println(e);
        }

		log.info("NumTenants: {}", ClientChannelPipeline.tenantnum);
    	return true;
	}
    
    
    public static void windowDataCollectorTimer() throws IOException{
    	
    	String usrdir = System.getProperty("user.dir");
    	
    	ChannelGroup cg = ClientChannelPipeline.cg;
    	
    	//tenantNum
    	//int currentTenatntNum = 1;
    	int currentTenatntNum = ClientChannelPipeline.tenantnum;
    	//channelNum
    	int currentChannelNum = ClientChannelPipeline.channelnum; 
    	//int currentChannelNum = 10; 
    	int[] tenantChannelnum = ClientChannelPipeline.tenantChannelnum;
    	//log.info("timer : windowcount{}, windowId{}",windowCount,windowId);
    	
    	if (windowCount == 0){
    		//filecreate for windowId, tenant_sw
    		for (int t=1; t<=currentTenatntNum; t++){
        		int tswNum = currentChannelNum;
        		for (int s=1; s<=tswNum; s++){
        			
        			String fileName = "collect_"+String.valueOf(windowId)+"_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
        			String filePath = usrdir+"/MeteorPredictor/windowData/"+fileName;
        			File file = new File(filePath);
        			file.createNewFile();
        			//log.info("{}",filePath);
        			
        			
        			if (windowId==1){
        			
        			String queueId = String.valueOf(t)+String.valueOf(s);
        			int queueKey = Integer.valueOf(queueId);
        			
        			ChannelQueue shaperqueue = new ChannelQueue(queueKey);
        			channelThread th = new channelThread();
        			channelThreadlist.add(th);
        			
        			th.setName(queueId);
        			th.setchannelId(t, s);
        			th.setwindowId(windowId);
        			th.setThreadId(channelThreadId);
        			channelThreadId++;
        			//th.start();
        			
        			
        			shaperQueues.put(queueKey, shaperqueue);
        			//ChannelQueue cq_init = shaperQueues.get(queueKey);
        			
        			//shaperQueues.get(queueKey).setKey(queueKey);
        			
        			String fullfileName = "fullData_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
        			String fullfilePath = usrdir+ "/MeteorPredictor/fullData/"+fullfileName;
        			File fullfile = new File(fullfilePath);
        			fullfile.createNewFile();
        			
        			//log.info("^^^^^ qKey: {} : queuechannel size {} / {}  ^^^^",queueKey,shaperQueues.size(),shaperQueues.get(queueKey).getKey());
        			}
        			
        			
        				
        		}
    	}
    	}
    	
    	if (windowCount<windowSize){
    		
    		for (int t=1; t<=currentTenatntNum; t++){
        		int tswNum = tenantChannelnum[t];
        		for (int s=1; s<=tswNum; s++){
        			//log.info("1111------------------------{},{}",windowId,windowCount);
        			String fileName = "collect_"+String.valueOf(windowId)+"_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
        			String filePath = usrdir+ "/MeteorPredictor/windowData/"+fileName;
        			FileWriter fw = null;
        			BufferedWriter bw = null;
        			
        			String fullfileName = "fullData_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
        			String fullfilePath = usrdir+"/MeteorPredictor/fullData/"+fullfileName;
        			FileWriter chfw = null;
        			BufferedWriter chbw = null;
        			
        			//edge sw, core sw for linear swtich. 
        	    	int hostNum = 0;
        	    	int swlink = 2;
        	    	//int swNum = currentChannelNum;
        	    	int swNum = currentChannelNum;
        	    	if (s == 1 || s == swNum){
        	    		hostNum = 1;
        	    		swlink = 1;
        	    	}
        	    	//1.s
        	    	String shostNum = String.valueOf(hostNum);
        	    	//2.h
        	    	String slink = String.valueOf(swlink);
        	    	//3. packetIn count
        			int packetInCount = packetinCount[t];
        			String sPi = String.valueOf(packetInCount);
        			//4. statMsg count
        			int statCount = statMsgCount[t][s];
        			String sSc = String.valueOf(statCount);
        			//5. flowmod count
        			int flowCount = OVXFlowMod.flow_counts[t][s];
        			String sFc = String.valueOf(flowCount);
        			//channelID
        			int channelId = OpenVirteXController.channelList[t][s];
        			Channel c = cg.find(channelId);
        			GlobalChannelTrafficShapingHandler gct = (GlobalChannelTrafficShapingHandler) c.getPipeline().get("globalchanneltrafficShapingHandler");
        			
        			if (updateKvalue[t][s] != currentKvalue[t][s] && updateKvalue[t][s] > 3000){
                 		
//                     	if (updateKvalue[tId][swDec] > 8000){
//                     		updateKvalue[tId][swDec] -=3000;
//                     	}
                     	
                     	
                 		long kValue = updateKvalue[t][s];
                 		gct.setReadChannelLimit(kValue);
                 	
                 		log.info("update k-value: tid> {} swid> {} kvalue> {}",t,s,updateKvalue[t][s]);
                 		currentKvalue[t][s] = updateKvalue[t][s];
                 	}
        			
        			//c.getPipeline().toString();
        			//6. current Read throughput
        			//long currentReadthroughput = gct.getTrafficCounter().getLastReadThroughput();
        			long currentTime = System.currentTimeMillis();
        			long readLimit = gct.getReadChannelLimit();
        			boolean limitcheck = readLimit>0 ? true:false;
        			boolean shapingbool = true;
        			long nowCumulativeReadByte = gct.getTrafficCounter().getCumulativeReadBytes();
        			
        			
        			
    				long nowreadByte = nowCumulativeReadByte - lastCumulativeReadByte[t][s];
    				long nowreadTime = currentTime - lastTimenow[t][s];
        			
        			if (!shapingbool){
    					// no shaping 
    					if (nowreadTime <=0 || nowreadTime > 15000){
    						readBytebuffer[t][s] = 0;
    						nowreadTime = 0;
    						whereiscode[t][s] = 11;
    					}
    					else{
    						whereiscode[t][s] = 12;
    						readBytebuffer[t][s] = (nowreadByte*100)/nowreadTime;
    					}
    	    			
    	    			lastCumulativeReadByte[t][s] = nowCumulativeReadByte;
    	    			lastTimenow[t][s] = currentTime;
    				}
    				else{
    					
    					if (monitoringCount[t][s] == 0){
    						nowreadByte += buffersize[t][s];
    						if(readLimit ==0){
    							limitavailablenow[t][s] = 50000;
    						}else{
    						limitavailablenow[t][s] = readLimit;
    						}
    						readBytebuffer[t][s]= (nowreadByte*100)/nowreadTime;
    						if (limitavailablenow[t][s] - readBytebuffer[t][s] < 0){
    							if (limitavailablenow[t][s] >0){
    								readBytebuffer[t][s]= limitavailablenow[t][s];
    							}
    							else{
    								readBytebuffer[t][s]=0;
    							}
    						}
    						limitavailablenow[t][s] -= readBytebuffer[t][s];
    						monitoringCount[t][s]++;
    					}
    					else{
    						readBytebuffer[t][s]= (nowreadByte*100)/nowreadTime;
    						if (limitavailablenow[t][s] - readBytebuffer[t][s] < 0){
    							if (limitavailablenow[t][s] >0){
    								readBytebuffer[t][s]= limitavailablenow[t][s];
    							}
    							else{
    								readBytebuffer[t][s]=0;
    							}
    						}
    						limitavailablenow[t][s] -= readBytebuffer[t][s];
    						
    						if (monitoringCount[t][s] == 9){
        						if (limitavailablenow[t][s] < 0){
        							buffersize[t][s] = Math.abs(limitavailablenow[t][s]);
        						}
        						else{
        							buffersize[t][s] = 0;
        						}
    							monitoringCount[t][s]=0;
        					}
    						else{
    							monitoringCount[t][s]++;
    						}
    					}
    					
    					//with shaping 
    					
    					
    					//log.info("aaaa------------------------{},{}",windowId,windowCount);
    					lastCumulativeReadByte[t][s] = nowCumulativeReadByte;
    	    			lastTimenow[t][s] = currentTime;
    	    			
    	    			String qId = String.valueOf(t)+String.valueOf(s);
    	    			int qKey = Integer.valueOf(qId);
    	    			//ChannelQueue cq = shaperQueues.get(qKey);
    	    			
    	    			//log.info("bbbb------------------------{},{}",windowId,windowCount);
//    	    			cq.shaperQueue[windowId][windowCount][0] = readBytebuffer[t][s];
//    	    			cq.shaperQueue[windowId][windowCount][1] = nowreadByte;
//    	    			cq.shaperQueue[windowId][windowCount][2] = bufferWaitTime[t][s];
//    	    			cq.shaperQueue[windowId][windowCount][3] = nowreadTime;
//    	    			cq.shaperQueue[windowId][windowCount][4] = windowCount;
//    	    			cq.shaperQueue[windowId][windowCount][5] = hostNum;
//    	    			cq.shaperQueue[windowId][windowCount][6] = swlink;
//    	    			cq.shaperQueue[windowId][windowCount][7] = packetInCount;
//    	    			cq.shaperQueue[windowId][windowCount][8] = statCount;
//    	    			cq.shaperQueue[windowId][windowCount][9] = flowCount;
    	    			//[throughput,size,wait,interval,windowcount,hostnum,linknum,packetin,stat,flowmod]
    	    			shaperQueues.get(qKey).setThroughput(windowId, windowCount, readBytebuffer[t][s]); 
    	    			shaperQueues.get(qKey).setSize(windowId, windowCount, nowreadByte); 
    	    			shaperQueues.get(qKey).setWait(windowId, windowCount, bufferWaitTime[t][s]); 
    	    			shaperQueues.get(qKey).setInterval(windowId, windowCount, nowreadTime); 
    	    			shaperQueues.get(qKey).setWcount(windowId, windowCount, windowCount); 
    	    			shaperQueues.get(qKey).setHostNum(windowId, windowCount, hostNum); 
    	    			shaperQueues.get(qKey).setLinkNum(windowId, windowCount, swlink); 
    	    			shaperQueues.get(qKey).setPacketinNum(windowId, windowCount, packetInCount); 
    	    			shaperQueues.get(qKey).setStatNum(windowId, windowCount, statCount); 
    	    			shaperQueues.get(qKey).setFlowmodNum(windowId, windowCount, flowCount); 
    	    			
    	    			
    				}
        			String sBw = String.valueOf(readBytebuffer[t][s]);
        			
        			//timeseries vlaue
        			String sTime = String.valueOf(windowCount);
        			String realTime = String.valueOf(currentTime);
        			String ssize = String.valueOf(nowreadByte);
        			String swait = String.valueOf(bufferWaitTime[t][s]);
        			String sreadTime = String.valueOf(nowreadTime);
        			String codewhere = String.valueOf(whereiscode[t][s]);
        			String currentLimit = String.valueOf(gct.getReadChannelLimit());
        			
        			long flowmodProtime = OVXFlowMod.processing_times[t][s];
        			String processingTime = String.valueOf(flowmodProtime);
        			long flowmodtime = OVXFlowMod.flowmodtime[t][s];
        			String flowmodcurTime = String.valueOf(flowmodtime);
        			
        			try{
        	    		fw = new FileWriter(filePath, true);
        	    		bw = new BufferedWriter(fw);
        	    		
        	    		bw.write(sTime);
        	    		bw.write(",");
        	    		bw.write(shostNum);
        	    		bw.write(",");
        	    		bw.write(slink);
        	    		bw.write(",");
        	    		bw.write(sPi);
        	    		bw.write(",");
        	    		bw.write(sSc);
        	    		bw.write(",");
        	    		bw.write(sFc);
        	    		bw.write(",");
        	    		bw.write(sBw);
        	    		bw.write(",");
        	    		bw.write(sreadTime);
        	    		//bw.write(",");
        	    		//bw.write(nowreadTime);
        	    		//bw.write(",");
        	    		//bw.write(ssize);
        	    		//bw.write(",");
        	    		//bw.write(sreadTime);
        	    		//bw.write(",");
        	    		//bw.write(codewhere);
        	    		bw.newLine();
        	    		
        	    		bw.flush();
        	    		
        	    		chfw = new FileWriter(fullfilePath, true);
        	    		chbw = new BufferedWriter(chfw);
        	    		

        	    		
//        	    		if(windowId==1 && windowCount==0){
//        	    			chbw.write("wcount,realtime,arriveTime,flowcurrenttime,packetin,stat,flowmod,bw,size,processingtime,limit,k");
//        	    			chbw.newLine();
//        	    		}
        	    		
        	    		chbw.write(sTime);
        	    		chbw.write(",");
        	    		chbw.write(realTime);
        	    		chbw.write(",");
        	    		chbw.write(String.valueOf(OVXFlowMod.arriveTimes[t][s]));
        	    		chbw.write(",");
        	    		chbw.write(flowmodcurTime);
        	    		chbw.write(",");
        	    		chbw.write(sPi);
        	    		chbw.write(",");
        	    		chbw.write(sSc);
        	    		chbw.write(",");
        	    		chbw.write(sFc);
        	    		chbw.write(",");
        	    		chbw.write(sBw);
        	    		chbw.write(",");
        	    		chbw.write(ssize);
        	    		chbw.write(",");
        	    		chbw.write(processingTime);
        	    		chbw.write(",");
        	    		chbw.write(currentLimit);
        	    		chbw.write(",");
        	    		chbw.write(String.valueOf(updateKvalue[t][s]));
        	    		chbw.newLine();
        	    		
        	    		chbw.flush();
        	    		
        	    	}catch(IOException e){
        	    		log.error("{}",e);
        	    	}finally{
        	    		try{ fw.close();}catch(IOException e){}
        	    		try{ bw.close();}catch(IOException e){}
        	    		try{ chfw.close();}catch(IOException e){}
        	    		try{ chbw.close();}catch(IOException e){}
        	    	}
        			//inverse
        			//[tenant][switch][windoId][count][feature]
        			//[throughput,size,wait,interval,windowcount,hostnum,linknum,packetin,stat,flowmod]
        		if(shapingbool){
        			if(windowCount==windowSize-1){
        			String qId = String.valueOf(t)+String.valueOf(s);
	    			int qKey = Integer.valueOf(qId);
	    			//ChannelQueue cq = shaperQueues.get(qKey);
	    			
	    			List<Integer> inversecount = new ArrayList<Integer>();
        			boolean inverse = false;
        			int aggregatePoint = -1;
        			boolean shapingInverse = false;
	        		if(shapingInverse){
	        			for(int w=0; w<windowSize; w++){
	        				if(inverse){
	        					inversecount.add(w);
	        					if(shaperQueues.get(qKey).getWait(windowId, w)==0){
	        						if(aggregatePoint != -1){
	        						shaperQueues.get(qKey).setPacketinNum(windowId,aggregatePoint,shaperQueues.get(qKey).getPacketinNum(windowId,w));
	        						shaperQueues.get(qKey).setStatNum(windowId,aggregatePoint,shaperQueues.get(qKey).getStatNum(windowId,w));
	        						shaperQueues.get(qKey).setFlowmodNum(windowId,aggregatePoint,shaperQueues.get(qKey).getFlowmodNum(windowId,w));
	        						}
	        						inverse = false;
	        					}
	        				}
	        				else if(shaperQueues.get(qKey).getThroughput(windowId, w) >= readLimit){
	        					long newThroughputValue = (shaperQueues.get(qKey).getSize(windowId, w)*100)/shaperQueues.get(qKey).getInterval(windowId, w);
	        					shaperQueues.get(qKey).setThroughput(windowId, w,newThroughputValue);
	        					inverse = true;
	        					aggregatePoint=w;
	        				}
	        			}
        			}
//	        			int [] inverseSize = new int[inversecount.size()];
//	        			for (int v=0;v<inverseSize.length;v++){
//	        				inverseSize[v] = inversecount.get(v).intValue();
//	        			}
	        			String ivfileName = "inverse_"+String.valueOf(windowId)+"_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
	        			String ivfilePath = usrdir+"/MeteorPredictor/inverseData/"+ivfileName;
	        			FileWriter ivfw = null;
	        			BufferedWriter ivbw = null;
	        			
	        			String ivfullfileName = "ivfullData_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
	        			String ivfullfilePath = usrdir+"/MeteorPredictor/inverseFullData/"+ivfullfileName;
	        			FileWriter ivchfw = null;
	        			BufferedWriter ivchbw = null;
	        			int newcount = 0;
	        			try{
    						ivfw = new FileWriter(ivfilePath, true);
    						ivbw = new BufferedWriter(ivfw);
    						
    						ivchfw = new FileWriter(ivfullfilePath, true);
            	    		ivchbw = new BufferedWriter(ivchfw);
    						
    						ivbw.write("Time,hostNum,slink,packetin,stat,flowmod,readThroughput");
    						ivbw.newLine();
    						
    						if(windowId==1){
            	    			ivchbw.write("wcount,hostNum,limitNum,packetin,stat,flowmod,bw,limit,actualBw");
            	    			ivchbw.newLine();
            	    		}
            	    		
    						ivbw.flush();
    						
    						
	        			for(int w=0; w<windowSize; w++){
	        				//if(!inversecount.contains(w)){
	        					
	        						ivbw.write(String.valueOf(newcount));
	        						ivbw.write(",");
	        						ivbw.write(shostNum);
	        						ivbw.write(",");
	        						ivbw.write(slink);
	        						ivbw.write(",");
	        						ivbw.write(String.valueOf(shaperQueues.get(qKey).getPacketinNum(windowId, w)));
	        						ivbw.write(",");
	        						ivbw.write(String.valueOf(shaperQueues.get(qKey).getStatNum(windowId, w)));
	        						ivbw.write(",");
	        						ivbw.write(String.valueOf(shaperQueues.get(qKey).getFlowmodNum(windowId, w)));
	        						ivbw.write(",");
	        						ivbw.write(String.valueOf(shaperQueues.get(qKey).getSize(windowId, w)));
	        						//ivbw.write(String.valueOf(shaperQueues.get(qKey).getThroughput(windowId, w)));
	        						ivbw.newLine();
	                	    		
	        						ivbw.flush();
	        						
	        						
	                	    		

	                	    		
	                	    		
	                	    		
	                	    		ivchbw.write(String.valueOf(newcount));
	                	    		ivchbw.write(",");
	                	    		ivchbw.write(shostNum);
	                	    		ivchbw.write(",");
	                	    		ivchbw.write(slink);
	                	    		ivchbw.write(",");
	                	    		ivchbw.write(String.valueOf(shaperQueues.get(qKey).getPacketinNum(windowId, w)));
	                	    		ivchbw.write(",");
	                	    		ivchbw.write(String.valueOf(shaperQueues.get(qKey).getStatNum(windowId, w)));
	                	    		ivchbw.write(",");
	                	    		ivchbw.write(String.valueOf(shaperQueues.get(qKey).getFlowmodNum(windowId, w)));
	                	    		ivchbw.write(",");
	                	    		ivchbw.write(String.valueOf(shaperQueues.get(qKey).getSize(windowId, w)));
	                	    		ivchbw.write(",");
	                	    		ivchbw.write(String.valueOf(updateKvalue[t][s]));
	                	    		ivchbw.write(",");
	                	    		ivchbw.write(String.valueOf(gct.getReadChannelLimit()));
	                	    		ivchbw.write(",");
	                	    		ivchbw.write(String.valueOf(shaperQueues.get(qKey).getThroughput(windowId, w)));
	        						//ivbw.write(String.valueOf(shaperQueues.get(qKey).getThroughput(windowId, w)));
	                	    		ivchbw.newLine();
	                	    		
	                	    		ivchbw.flush();
	                	    	
	        				newcount++;
	        				//}
	        			}
	        			}catch(IOException e){
            	    		log.error("{}",e);
            	    	}finally{
            	    		try{ ivfw.close();}catch(IOException e){}
            	    		try{ ivbw.close();}catch(IOException e){}
            	    	} 
	        		}
        		
        	}
          }
    	}
    		
    		windowCount++;
    		
    		
    		if (windowCount >= windowSize){
        		
    			//inference
    			for (int t=1; t<=currentTenatntNum; t++){
            		int tswNum = tenantChannelnum[t];
            		//int tswNum = 10;
            		inferenceThread ith = new inferenceThread();
            		ith.setthreadId(t, tswNum, windowId);
            		ith.start();
    			}
    			
    			//Thread thnow = channelThreadlist.get(index);
    			windowCount = 0;
        		windowId++;
        		
        	
        		
        	}
    		
    	}
    	
    	
    	
    	
    }
    
public static void originalWindow() throws IOException{
	
	ChannelGroup cg = ClientChannelPipeline.cg;
	String usrdir = System.getProperty("user.dir");
	
	//tenantNum
	//int currentTenatntNum = StartOVXNetwork.tenantCount;
	int currentTenatntNum = ClientChannelPipeline.tenantnum;
	//channelNum
	int currentChannelNum = ClientChannelPipeline.channelnum; 
	int[] tenantChannelnum = ClientChannelPipeline.tenantChannelnum;
	
	if (windowCount == 0){
		//filecreate for windowId, tenant_sw
		for (int t=1; t<=currentTenatntNum; t++){
    		int tswNum = tenantChannelnum[t];
    		for (int s=1; s<=tswNum; s++){
    			
    			String fileName = "collect_"+String.valueOf(windowId)+"_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
    			String filePath = usrdir+"/MeteorPredictor/windowData/"+fileName;
    			File file = new File(filePath);
    			file.createNewFile();
    			
    			
    			if (windowId==1){
    			
    			String queueId = String.valueOf(t)+String.valueOf(s);
    			int queueKey = Integer.valueOf(queueId);
    			
    			ChannelQueue shaperqueue = new ChannelQueue(queueKey);
    			channelThread th = new channelThread();
    			channelThreadlist.add(th);
    			
    			th.setName(queueId);
    			th.setchannelId(t, s);
    			th.setwindowId(windowId);
    			th.setThreadId(channelThreadId);
    			channelThreadId++;
    			//th.start();
    			
    			
    			shaperQueues.put(queueKey, shaperqueue);
    			//ChannelQueue cq_init = shaperQueues.get(queueKey);
    			
    			//shaperQueues.get(queueKey).setKey(queueKey);
    			
    			String fullfileName = "fullData_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
    			String fullfilePath = usrdir+"/MeteorPredictor/fullData/"+fullfileName;
    			File fullfile = new File(fullfilePath);
    			fullfile.createNewFile();
    			
    			}
    			
    			
    				
    		}
	}
	}
	
	if (windowCount<windowSize){
		
		for (int t=1; t<=currentTenatntNum; t++){
    		int tswNum = tenantChannelnum[t];
    		for (int s=1; s<=tswNum; s++){
    			//log.info("1111------------------------{},{}",windowId,windowCount);
    			String fileName = "collect_"+String.valueOf(windowId)+"_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
    			String filePath = usrdir+"/MeteorPredictor/windowData/"+fileName;
    			FileWriter fw = null;
    			BufferedWriter bw = null;
    			
    			String fullfileName = "fullData_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
    			String fullfilePath = usrdir+"/MeteorPredictor/fullData/"+fullfileName;
    			FileWriter chfw = null;
    			BufferedWriter chbw = null;
    			
    			//edge sw, core sw for linear swtich. 
    	    	int hostNum = 0;
    	    	int swlink = 2;
    	    	int swNum = currentChannelNum;
    	    	if (s == 1 || s == swNum){
    	    		hostNum = 1;
    	    		swlink = 1;
    	    	}
    	    	//1.s
    	    	String shostNum = String.valueOf(hostNum);
    	    	//2.h
    	    	String slink = String.valueOf(swlink);
    	    	//3. packetIn count
    			int packetInCount = packetinCount[t];
    			String sPi = String.valueOf(packetInCount);
    			//4. statMsg count
    			int statCount = statMsgCount[t][s];
    			String sSc = String.valueOf(statCount);
    			//5. flowmod count
    			int flowCount = OVXFlowMod.flow_counts[t][s];
    			String sFc = String.valueOf(flowCount);
    			//channelID
    			int channelId = OpenVirteXController.channelList[t][s];
    			Channel c = cg.find(channelId);
    			GlobalChannelTrafficShapingHandler gct = (GlobalChannelTrafficShapingHandler) c.getPipeline().get("globalchanneltrafficShapingHandler");
    	    	//c.getPipeline().toString();
    			//6. current Read throughput
    			//long currentReadthroughput = gct.getTrafficCounter().getLastReadThroughput();
    			long currentTime = System.currentTimeMillis();
    			long readLimit = gct.getReadChannelLimit();
    			boolean limitcheck = readLimit>0 ? true:false;
    			boolean shapingbool = true;
    			long nowCumulativeReadByte = gct.getTrafficCounter().getCumulativeReadBytes();
    			
    			
    			
				long nowreadByte = nowCumulativeReadByte - lastCumulativeReadByte[t][s];
				long nowreadTime = currentTime - lastTimenow[t][s];
    			
    			if (!limitcheck){
					// no shaping 
					if (nowreadTime <=0 || nowreadTime > 15000){
						readBytebuffer[t][s] = 0;
						nowreadTime = 0;
						whereiscode[t][s] = 11;
					}
					else{
						whereiscode[t][s] = 12;
						readBytebuffer[t][s] = (nowreadByte*100)/nowreadTime;
					}
	    			
	    			lastCumulativeReadByte[t][s] = nowCumulativeReadByte;
	    			lastTimenow[t][s] = currentTime;
				}
				else{
					//with shaping 
					if (nowreadByte <0 || nowreadTime > 15000){
						//log.info("if------------------------{},{}",windowId,windowCount);
						whereiscode[t][s] = 21;
						readBytebuffer[t][s] = 0;
						nowreadTime = 0;
						bufferWaitTime[t][s] = 0;
						//log.info("if end------------------------{},{}",windowId,windowCount);
						
					}
					else if(bufferWaitTime[t][s]==0){
						//log.info("a1------------------------{},{}",windowId,windowCount);
						if (readLimit <=0){
							bufferWaitTime[t][s]=0;
							readBytebuffer[t][s]= (nowreadByte*100)/nowreadTime;
						}
					
						else{
							bufferWaitTime[t][s] = (nowreadByte*100)/readLimit;
						}
						//log.info("a2----------------------{},{}",windowId,windowCount);
						if (bufferWaitTime[t][s] == 0 && readLimit<0){
							//log.info("a3------------------------{},{}",windowId,windowCount);
							whereiscode[t][s] = 22;
							readBytebuffer[t][s]=0;
							//log.info("a4------------------------{},{}",windowId,windowCount);
							
						}
						else{
							//log.info("a5(------------------------{},{}",windowId,windowCount);
							bufferWaitTime[t][s] = bufferWaitTime[t][s] - nowreadTime;
							//log.info("a6(------------------------{},{}",windowId,windowCount);
							if(bufferWaitTime[t][s] >= 10){
								//wait available
								//log.info("a7(------------------------{},{}",windowId,windowCount);
								whereiscode[t][s] = 23;
								readBytebuffer[t][s] = readLimit;
								//log.info("a8(------------------------{},{}",windowId,windowCount);
							}
							else{
								//can not wait
								whereiscode[t][s] = 24;
								readBytebuffer[t][s] = (nowreadByte*100)/nowreadTime;
								bufferWaitTime[t][s] = 0;
							}
						}
					}
					
					else if(bufferWaitTime[t][s] != 0){
						bufferWaitTime[t][s] = bufferWaitTime[t][s] - nowreadTime;
						if(bufferWaitTime[t][s] >= 10){
							whereiscode[t][s] = 31;
							readBytebuffer[t][s] = readLimit;
							if (bufferWaitTime[t][s] < 100){
								whereiscode[t][s] = 32;
								bufferWaitTime[t][s]=0;
							}
							
						}
						else {
							whereiscode[t][s] = 33;
							readBytebuffer[t][s] = readLimit;
							bufferWaitTime[t][s]=0;
						}
					}
					lastCumulativeReadByte[t][s] = nowCumulativeReadByte;
	    			lastTimenow[t][s] = currentTime;
	    			
	    			String qId = String.valueOf(t)+String.valueOf(s);
	    			int qKey = Integer.valueOf(qId);
	    			
	    			shaperQueues.get(qKey).setThroughput(windowId, windowCount, readBytebuffer[t][s]); 
	    			shaperQueues.get(qKey).setSize(windowId, windowCount, nowreadByte); 
	    			shaperQueues.get(qKey).setWait(windowId, windowCount, bufferWaitTime[t][s]); 
	    			shaperQueues.get(qKey).setInterval(windowId, windowCount, nowreadTime); 
	    			shaperQueues.get(qKey).setWcount(windowId, windowCount, windowCount); 
	    			shaperQueues.get(qKey).setHostNum(windowId, windowCount, hostNum); 
	    			shaperQueues.get(qKey).setLinkNum(windowId, windowCount, swlink); 
	    			shaperQueues.get(qKey).setPacketinNum(windowId, windowCount, packetInCount); 
	    			shaperQueues.get(qKey).setStatNum(windowId, windowCount, statCount); 
	    			shaperQueues.get(qKey).setFlowmodNum(windowId, windowCount, flowCount); 
	    			//log.info("cccc------------------------{},{}",windowId,windowCount);
	    			
				}
    			String sBw = String.valueOf(readBytebuffer[t][s]);
    			
    			//timeseries vlaue
    			String sTime = String.valueOf(windowCount);
    			String realTime = String.valueOf(currentTime);
    			String ssize = String.valueOf(nowreadByte);
    			String swait = String.valueOf(bufferWaitTime[t][s]);
    			String sreadTime = String.valueOf(nowreadTime);
    			String codewhere = String.valueOf(whereiscode[t][s]);
    			String currentLimit = String.valueOf(gct.getReadChannelLimit());
    			//log.info("3333------------------------{},{}",windowId,windowCount);
    			try{
    	    		fw = new FileWriter(filePath, true);
    	    		bw = new BufferedWriter(fw);
    	    		//log.info("4444------------------------{},{}",windowId,windowCount);
    	    		bw.write(sTime);
    	    		bw.write(",");
    	    		bw.write(shostNum);
    	    		bw.write(",");
    	    		bw.write(slink);
    	    		bw.write(",");
    	    		bw.write(sPi);
    	    		bw.write(",");
    	    		bw.write(sSc);
    	    		bw.write(",");
    	    		bw.write(sFc);
    	    		bw.write(",");
    	    		bw.write(sBw);
    	    		//bw.write(",");
    	    		//bw.write(ssize);
    	    		//bw.write(",");
    	    		//bw.write(sreadTime);
    	    		//bw.write(",");
    	    		//bw.write(codewhere);
    	    		bw.newLine();
    	    		
    	    		bw.flush();
    	    		
    	    		chfw = new FileWriter(fullfilePath, true);
    	    		chbw = new BufferedWriter(chfw);
    	    		

    	    		
    	    		if(windowId==1 && windowCount==0){
    	    			chbw.write("wcount,realtime,hostNum,limitNum,packetin,stat,flowmod,bw,size,readtime,limit,k");
    	    			chbw.newLine();
    	    		}
    	    		
    	    		chbw.write(sTime);
    	    		chbw.write(",");
    	    		chbw.write(realTime);
    	    		chbw.write(",");
    	    		chbw.write(shostNum);
    	    		chbw.write(",");
    	    		chbw.write(slink);
    	    		chbw.write(",");
    	    		chbw.write(sPi);
    	    		chbw.write(",");
    	    		chbw.write(sSc);
    	    		chbw.write(",");
    	    		chbw.write(sFc);
    	    		chbw.write(",");
    	    		chbw.write(sBw);
    	    		chbw.write(",");
    	    		chbw.write(ssize);
    	    		chbw.write(",");
    	    		chbw.write(sreadTime);
    	    		chbw.write(",");
    	    		chbw.write(currentLimit);
    	    		chbw.write(",");
    	    		chbw.write(String.valueOf(updateKvalue[t][s]));
    	    		chbw.newLine();
    	    		
    	    		chbw.flush();
    	    		
    	    	}catch(IOException e){
    	    		log.error("{}",e);
    	    	}finally{
    	    		try{ fw.close();}catch(IOException e){}
    	    		try{ bw.close();}catch(IOException e){}
    	    		try{ chfw.close();}catch(IOException e){}
    	    		try{ chbw.close();}catch(IOException e){}
    	    	}
    			//inverse
    			//[tenant][switch][windoId][count][feature]
    			//[throughput,size,wait,interval,windowcount,hostnum,linknum,packetin,stat,flowmod]
    		if(shapingbool){
    			if(windowCount==windowSize-1){
    			String qId = String.valueOf(t)+String.valueOf(s);
    			int qKey = Integer.valueOf(qId);
    			//ChannelQueue cq = shaperQueues.get(qKey);
    			
    			//log.info("yy**  qkey: {}/{}, hashkey: {} / {}",qKey,shaperQueues.containsKey(qKey),shaperQueues.get(qKey).getKey(),shaperQueues.keySet());	
    			List<Integer> inversecount = new ArrayList<Integer>();
    			boolean inverse = false;
    			int aggregatePoint = -1;
    			boolean shapingInverse = false;
        		if(shapingInverse){
        			for(int w=0; w<windowSize; w++){
        				if(inverse){
        					inversecount.add(w);
        					if(shaperQueues.get(qKey).getWait(windowId, w)==0){
        						if(aggregatePoint != -1){
        						shaperQueues.get(qKey).setPacketinNum(windowId,aggregatePoint,shaperQueues.get(qKey).getPacketinNum(windowId,w));
        						shaperQueues.get(qKey).setStatNum(windowId,aggregatePoint,shaperQueues.get(qKey).getStatNum(windowId,w));
        						shaperQueues.get(qKey).setFlowmodNum(windowId,aggregatePoint,shaperQueues.get(qKey).getFlowmodNum(windowId,w));
        						}
        						inverse = false;
        					}
        				}
        				else if(shaperQueues.get(qKey).getThroughput(windowId, w) >= readLimit){
        					long newThroughputValue = (shaperQueues.get(qKey).getSize(windowId, w)*100)/shaperQueues.get(qKey).getInterval(windowId, w);
        					//log.info("^^ {}/{}/{}",windowId,w,shaperQueues.get(qKey).getSize(windowId, w));
        					shaperQueues.get(qKey).setThroughput(windowId, w,newThroughputValue);
        					inverse = true;
        					aggregatePoint=w;
        				}
        			}
    			}
//        			int [] inverseSize = new int[inversecount.size()];
//        			for (int v=0;v<inverseSize.length;v++){
//        				inverseSize[v] = inversecount.get(v).intValue();
//        			}
        			String ivfileName = "inverse_"+String.valueOf(windowId)+"_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
        			String ivfilePath = usrdir+"/MeteorPredictor/inverseData/"+ivfileName;
        			FileWriter ivfw = null;
        			BufferedWriter ivbw = null;
        			
        			String ivfullfileName = "ivfullData_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
        			String ivfullfilePath = usrdir+"/MeteorPredictor/inverseFullData/"+ivfullfileName;
        			FileWriter ivchfw = null;
        			BufferedWriter ivchbw = null;
        			int newcount = 0;
        			try{
						ivfw = new FileWriter(ivfilePath, true);
						ivbw = new BufferedWriter(ivfw);
						
						ivchfw = new FileWriter(ivfullfilePath, true);
        	    		ivchbw = new BufferedWriter(ivchfw);
						
						ivbw.write("Time,hostNum,slink,packetin,stat,flowmod,readThroughput");
						ivbw.newLine();
						
						if(windowId==1){
        	    			ivchbw.write("wcount,hostNum,limitNum,packetin,stat,flowmod,bw,limit,actualBw");
        	    			ivchbw.newLine();
        	    		}
        	    		
						ivbw.flush();
						
						
        			for(int w=0; w<windowSize; w++){
        				//if(!inversecount.contains(w)){
        					
        						ivbw.write(String.valueOf(newcount));
        						ivbw.write(",");
        						ivbw.write(shostNum);
        						ivbw.write(",");
        						ivbw.write(slink);
        						ivbw.write(",");
        						ivbw.write(String.valueOf(shaperQueues.get(qKey).getPacketinNum(windowId, w)));
        						ivbw.write(",");
        						ivbw.write(String.valueOf(shaperQueues.get(qKey).getStatNum(windowId, w)));
        						ivbw.write(",");
        						ivbw.write(String.valueOf(shaperQueues.get(qKey).getFlowmodNum(windowId, w)));
        						ivbw.write(",");
        						ivbw.write(String.valueOf(shaperQueues.get(qKey).getSize(windowId, w)));
        						//ivbw.write(String.valueOf(shaperQueues.get(qKey).getThroughput(windowId, w)));
        						ivbw.newLine();
                	    		
        						ivbw.flush();
        						
        						
                	    		

                	    		
                	    		
                	    		
                	    		ivchbw.write(String.valueOf(newcount));
                	    		ivchbw.write(",");
                	    		ivchbw.write(shostNum);
                	    		ivchbw.write(",");
                	    		ivchbw.write(slink);
                	    		ivchbw.write(",");
                	    		ivchbw.write(String.valueOf(shaperQueues.get(qKey).getPacketinNum(windowId, w)));
                	    		ivchbw.write(",");
                	    		ivchbw.write(String.valueOf(shaperQueues.get(qKey).getStatNum(windowId, w)));
                	    		ivchbw.write(",");
                	    		ivchbw.write(String.valueOf(shaperQueues.get(qKey).getFlowmodNum(windowId, w)));
                	    		ivchbw.write(",");
                	    		ivchbw.write(String.valueOf(shaperQueues.get(qKey).getSize(windowId, w)));
                	    		ivchbw.write(",");
                	    		ivchbw.write(String.valueOf(updateKvalue[t][s]));
                	    		ivchbw.write(",");
                	    		ivchbw.write(String.valueOf(gct.getReadChannelLimit()));
                	    		ivchbw.write(",");
                	    		ivchbw.write(String.valueOf(shaperQueues.get(qKey).getThroughput(windowId, w)));
        						//ivbw.write(String.valueOf(shaperQueues.get(qKey).getThroughput(windowId, w)));
                	    		ivchbw.newLine();
                	    		
                	    		ivchbw.flush();
                	    	
        				newcount++;
        				//}
        			}
        			}catch(IOException e){
        	    		log.error("{}",e);
        	    	}finally{
        	    		try{ ivfw.close();}catch(IOException e){}
        	    		try{ ivbw.close();}catch(IOException e){}
        	    	} 
        		}
    		
    	}
      }
	}
		
		windowCount++;
		
		
		if (windowCount >= windowSize){
    		
			//inference
			for (int t=1; t<=currentTenatntNum; t++){
        		int tswNum = tenantChannelnum[t];
        		inferenceThread ith = new inferenceThread();
        		ith.setthreadId(t, tenantChannelnum[t], windowId);
        		ith.start();
			}
			
			//Thread thnow = channelThreadlist.get(index);
			windowCount = 0;
    		windowId++;
    		
    	
    		
    	}
		
	}
	
	
	
	

}
    
public static void monitorInverseShapingTimer() throws IOException{
    	
    	ChannelGroup cg = ClientChannelPipeline.cg;
    	String usrdir = System.getProperty("user.dir");
    	
    	//tenantNum
    	//int currentTenatntNum = StartOVXNetwork.tenantCount;
    	int currentTenatntNum = ClientChannelPipeline.tenantnum;
    	//channelNum
    	int currentChannelNum = ClientChannelPipeline.channelnum; 
    	int[] tenantChannelnum = ClientChannelPipeline.tenantChannelnum;
    	
    	if (monitorCount ==0){
    		//filecreate for windowId, tenant_sw
    		for (int t=1; t<=currentTenatntNum; t++){
        		int tswNum = tenantChannelnum[t];
        		for (int s=1; s<=tswNum; s++){
        			String fileName = "monitor_"+String.valueOf(monitorId)+"_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
        			String filePath = usrdir+"/MeteorPredictor/Monitor_inverse/"+fileName;
        			File file = new File(filePath);
        			file.createNewFile();
        		}
    	}
    	}
    	
    	if (monitorCount<windowSize){
    		
    		for (int t=1; t<=currentTenatntNum; t++){
        		int tswNum = tenantChannelnum[t];
        		for (int s=1; s<=tswNum; s++){
        			String fileName = "monitor_"+String.valueOf(monitorId)+"_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
        			String filePath = usrdir+"/MeteorPredictor/Monitor_inverse/"+fileName;
        			FileWriter fw = null;
        			BufferedWriter bw = null;
        			
        			//edge sw, core sw for linear swtich. 
        	    	int hostNum = 0;
        	    	int swlink = 2;
        	    	int swNum = currentChannelNum;
        	    	if (s == 1 || s == swNum){
        	    		hostNum = 1;
        	    		swlink = 1;
        	    	}
        	    	//1.s
        	    	String shostNum = String.valueOf(hostNum);
        	    	//2.h
        	    	String slink = String.valueOf(swlink);
        	    	//3. packetIn count
        			int packetInCount = packetinCount[t];
        			String sPi = String.valueOf(packetInCount);
        			//4. statMsg count
        			int statCount = statMsgCount[t][s];
        			String sSc = String.valueOf(statCount);
        			//5. flowmod count
        			int flowCount = OVXFlowMod.flow_counts[t][s];
        			String sFc = String.valueOf(flowCount);
        			//channelID
        			int channelId = OpenVirteXController.channelList[t][s];
        			Channel c = cg.find(channelId);
        			GlobalChannelTrafficShapingHandler gct = (GlobalChannelTrafficShapingHandler) c.getPipeline().get("globalchanneltrafficShapingHandler");
        	    	
        			//6. current Read throughput
        			long currentReadthroughput = gct.getTrafficCounter().getLastReadThroughput();
        			String sBw = String.valueOf(currentReadthroughput);
        			
        		
        			
        			//gct Data
        			String queuesizeData = String.valueOf(gct.queuesSize());
        			String checkInterval = String.valueOf(gct.getCheckInterval());
        			String defalut_maxInterval = String.valueOf(gct.DEFAULT_CHECK_INTERVAL);
        			String maxDeviation = String.valueOf(gct.maxDeviation());
        			String maxTimewait = String.valueOf(gct.getMaxTimeWait());
        			String default_maxWait = String.valueOf(gct.DEFAULT_MAX_TIME);
        			String maxGlobalWriteSize = String.valueOf(gct.getMaxGlobalWriteSize());
        			String maxWriteDealy = String.valueOf(gct.getMaxWriteDelay());
        			String maxWriteSize = String.valueOf(gct.getMaxWriteSize());
        			String readChannelLimit = String.valueOf(gct.getReadChannelLimit());
        			String writeChannelLimit = String.valueOf(gct.getWriteChannelLimit());
        			String readLimit = String.valueOf(gct.getReadLimit());
        			String writeLimit = String.valueOf(gct.getWriteLimit());
        		
        			//tc data
        			TrafficCounter ttc = gct.getTrafficCounter();
        			String timeNano = String.valueOf(ttc.milliSecondFromNano());
        			String checkInterval_ttc = String.valueOf(ttc.getCheckInterval());
        			String lastCumulativeTime = String.valueOf(ttc.getLastCumulativeTime());
        			String lastTime = String.valueOf(ttc.getLastTime());
        			String cumulativeReadBytes = String.valueOf(ttc.getCumulativeReadBytes());
        			String cumulativeWrittenBytes = String.valueOf(ttc.getCumulativeWrittenBytes());
        			String currentReadBytes = String.valueOf(ttc.getCurrentReadBytes());
        			String currentWrittenBytes = String.valueOf(ttc.getCurrentWrittenBytes());
        			String lastReadBytes = String.valueOf(ttc.getLastReadBytes());
        			String lastWrittenBytes = String.valueOf(ttc.getLastWrittenBytes());
        			String lastReadThroughput = String.valueOf(ttc.getLastReadThroughput());
        			String lastWriteThroughput = String.valueOf(ttc.getLastWriteThroughput());
        			String realWriteThroughput = String.valueOf(ttc.getRealWriteThroughput());
        			String realWriteBytes = String.valueOf(ttc.getRealWrittenBytes());
        			//String readTimetoWait = String.valueOf(ttc.readTimeToWait(ttc.getLastReadBytes(), gct.getReadLimit(), gct.getMaxTimeWait(), ttc.milliSecondFromNano()));
        	
        			
        			
        			
        			//timeseries vlaue
        			String sTime = String.valueOf(monitorCount);
        			try{
        	    		fw = new FileWriter(filePath, true);
        	    		bw = new BufferedWriter(fw);
        	    		
        	    		bw.write(sTime);
        	    		bw.write(",");
        	    		bw.write(queuesizeData);
        	    		bw.write(",");
        	    		bw.write(checkInterval);
        	    		bw.write(",");
        	    		bw.write(defalut_maxInterval);
        	    		bw.write(",");
        	    		bw.write(maxDeviation);
        	    		bw.write(",");
        	    		bw.write(maxTimewait);
        	    		bw.write(",");
        	    		bw.write(default_maxWait);
        	    		bw.write(",");
        	    		bw.write(maxGlobalWriteSize);
        	    		bw.write(",");
        	    		bw.write(maxWriteDealy);
        	    		bw.write(",");
        	    		bw.write(maxWriteSize);
        	    		bw.write(",");
        	    		bw.write(readChannelLimit);
        	    		bw.write(",");
        	    		bw.write(writeChannelLimit);
        	    		bw.write(",");
        	    		bw.write(readLimit);
        	    		bw.write(",");
        	    		bw.write(writeLimit);
        	    		bw.write(",");
        	    		bw.write(timeNano);
        	    		bw.write(",");
        	    		bw.write(checkInterval_ttc);
        	    		bw.write(",");
        	    		bw.write(lastCumulativeTime);
        	    		bw.write(",");
        	    		bw.write(lastTime);
        	    		bw.write(",");
        	    		bw.write(cumulativeReadBytes);
        	    		bw.write(",");
        	    		bw.write(cumulativeWrittenBytes);
        	    		bw.write(",");
        	    		bw.write(currentReadBytes);
        	    		bw.write(",");
        	    		bw.write(currentWrittenBytes);
        	    		bw.write(",");
        	    		bw.write(lastReadBytes);
        	    		bw.write(",");
        	    		bw.write(lastWrittenBytes);
        	    		bw.write(",");
        	    		bw.write(lastReadThroughput);
        	    		bw.write(",");
        	    		bw.write(lastWriteThroughput);
        	    		bw.write(",");
        	    		bw.write(realWriteThroughput);
        	    		bw.write(",");
        	    		bw.write(realWriteBytes);
        	    		
        	    		
        	    		
        	    		bw.newLine();
        	    		
        	    		bw.flush();
        	    	}catch(IOException e){
        	    		log.error("{}",e);
        	    	}finally{
        	    		try{ fw.close();}catch(IOException e){}
        	    		try{ bw.close();}catch(IOException e){}
        	    	}
        		}
    	}
    		
    		monitorCount++;
    		
    		if (monitorCount >= windowSize){
    			monitorCount = 0;
        		monitorId++;
        	}
    	}
    	
    	
    	
    	
    }
	public static void allDatacollector() throws IOException{
		ChannelGroup cg = ClientChannelPipeline.cg;
		String usrdir = System.getProperty("user.dir");
    	
    	//tenantNum
    	//int currentTenatntNum = StartOVXNetwork.tenantCount;
		int currentTenatntNum = ClientChannelPipeline.tenantnum;
		//channelNum
    	int currentChannelNum = ClientChannelPipeline.channelnum; 
    	int[] tenantChannelnum = ClientChannelPipeline.tenantChannelnum;
    	
    	if (windowCount ==0){
    		//filecreate for windowId, tenant_sw
    		for (int t=1; t<=currentTenatntNum; t++){
        		int tswNum = tenantChannelnum[t];
        		for (int s=1; s<=tswNum; s++){
        			String input_fileName = "collect_"+String.valueOf(windowId)+"_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
        			String input_filePath = usrdir+"/MeteorPredictor/windowData/"+input_fileName;
        			File input_file = new File(input_filePath);
        			input_file.createNewFile();
        			String mon_fileName = "monitor_"+String.valueOf(windowId)+"_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
        			String mon_filePath = usrdir+"/MeteorPredictor/Monitor_inverse/"+mon_fileName;
        			File mon_file = new File(mon_filePath);
        			mon_file.createNewFile();
        		}
    	}
    	}
    	
    	if (windowCount<windowSize){
    		
    		for (int t=1; t<=currentTenatntNum; t++){
        		int tswNum = tenantChannelnum[t];
        		for (int s=1; s<=tswNum; s++){
        			String input_fileName = "collect_"+String.valueOf(windowId)+"_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
        			String input_filePath = usrdir+"/MeteorPredictor/windowData/"+input_fileName;
        			FileWriter input_fw = null;
        			BufferedWriter input_bw = null;
        			
        			String mon_fileName = "monitor_"+String.valueOf(windowId)+"_"+String.valueOf(t)+"_"+String.valueOf(s)+".txt";
        			String mon_filePath = usrdir+"/MeteorPredictor/Monitor_inverse/"+mon_fileName;
        			FileWriter mon_fw = null;
        			BufferedWriter mon_bw = null;
        			
        	    	int hostNum = 0;
        	    	int swlink = 2;
        	    	int swNum = currentChannelNum;
        	    	if (s == 1 || s == swNum){
        	    		hostNum = 1;
        	    		swlink = 1;
        	    	}
        	    	//1.s
        	    	String shostNum = String.valueOf(hostNum);
        	    	//2.h
        	    	String slink = String.valueOf(swlink);
        	    	//3. packetIn count
        			int packetInCount = packetinCount[t];
        			String sPi = String.valueOf(packetInCount);
        			//4. statMsg count
        			int statCount = statMsgCount[t][s];
        			String sSc = String.valueOf(statCount);
        			//5. flowmod count
        			int flowCount = OVXFlowMod.flow_counts[t][s];
        			String sFc = String.valueOf(flowCount);
        			//channelID
        			int channelId = OpenVirteXController.channelList[t][s];
        			Channel c = cg.find(channelId);
        			GlobalChannelTrafficShapingHandler gct = (GlobalChannelTrafficShapingHandler) c.getPipeline().get("globalchanneltrafficShapingHandler");
        	    	
        			//6. current Read throughput
        			long currentReadthroughput = gct.getTrafficCounter().getLastReadThroughput();
        			String sBw = String.valueOf(currentReadthroughput);
        			
        			//timeseries vlaue
        			String sTime = String.valueOf(windowCount);
        			
        			
        			//gct Data
        			String queuesizeData = String.valueOf(gct.queuesSize());
        			String checkInterval = String.valueOf(gct.getCheckInterval());
        			String defalut_maxInterval = String.valueOf(gct.DEFAULT_CHECK_INTERVAL);
        			String maxDeviation = String.valueOf(gct.maxDeviation());
        			String maxTimewait = String.valueOf(gct.getMaxTimeWait());
        			String default_maxWait = String.valueOf(gct.DEFAULT_MAX_TIME);
        			String maxGlobalWriteSize = String.valueOf(gct.getMaxGlobalWriteSize());
        			String maxWriteDealy = String.valueOf(gct.getMaxWriteDelay());
        			String maxWriteSize = String.valueOf(gct.getMaxWriteSize());
        			String readChannelLimit = String.valueOf(gct.getReadChannelLimit());
        			String writeChannelLimit = String.valueOf(gct.getWriteChannelLimit());
        			String readLimit = String.valueOf(gct.getReadLimit());
        			String writeLimit = String.valueOf(gct.getWriteLimit());
        		
        			//tc data
        			TrafficCounter ttc = gct.getTrafficCounter();
        			long cumReadByte = ttc.getCumulativeReadBytes();
        			long lastTimec = ttc.getLastTime();
        			String timeNano = String.valueOf(ttc.milliSecondFromNano());
        			String checkInterval_ttc = String.valueOf(ttc.getCheckInterval());
        			String lastCumulativeTime = String.valueOf(ttc.getLastCumulativeTime());
        			String lastTime = String.valueOf(lastTimec);
        			String cumulativeReadBytes = String.valueOf(cumReadByte);
        			String cumulativeWrittenBytes = String.valueOf(ttc.getCumulativeWrittenBytes());
        			String currentReadBytes = String.valueOf(ttc.getCurrentReadBytes());
        			String currentWrittenBytes = String.valueOf(ttc.getCurrentWrittenBytes());
        			String lastReadBytes = String.valueOf(ttc.getLastReadBytes());
        			String lastWrittenBytes = String.valueOf(ttc.getLastWrittenBytes());
        			String lastReadThroughput = String.valueOf(ttc.getLastReadThroughput());
        			String lastWriteThroughput = String.valueOf(ttc.getLastWriteThroughput());
        			String realWriteThroughput = String.valueOf(ttc.getRealWriteThroughput());
        			String realWriteBytes = String.valueOf(ttc.getRealWrittenBytes());
        			
        			
					
        			
        			
        			try{
        	    		input_fw = new FileWriter(input_filePath, true);
        	    		input_bw = new BufferedWriter(input_fw);
        	    		
        	    		input_bw.write(sTime);
        	    		input_bw.write(",");
        	    		input_bw.write(shostNum);
        	    		input_bw.write(",");
        	    		input_bw.write(slink);
        	    		input_bw.write(",");
        	    		input_bw.write(sPi);
        	    		input_bw.write(",");
        	    		input_bw.write(sSc);
        	    		input_bw.write(",");
        	    		input_bw.write(sFc);
        	    		input_bw.write(",");
        	    		input_bw.write(sBw);
        	    		input_bw.newLine();
        	    		input_bw.flush();
        	    		
        	    		mon_fw = new FileWriter(mon_filePath, true);
        	    		mon_bw = new BufferedWriter(mon_fw);
        	    		
        	    		mon_bw.write(sTime);
        	    		mon_bw.write(",");
        	    		mon_bw.write(queuesizeData);
        	    		mon_bw.write(",");
        	    		mon_bw.write(checkInterval);
        	    		mon_bw.write(",");
        	    		mon_bw.write(defalut_maxInterval);
        	    		mon_bw.write(",");
        	    		mon_bw.write(maxDeviation);
        	    		mon_bw.write(",");
        	    		mon_bw.write(maxTimewait);
        	    		mon_bw.write(",");
        	    		mon_bw.write(default_maxWait);
        	    		mon_bw.write(",");
        	    		mon_bw.write(maxGlobalWriteSize);
        	    		mon_bw.write(",");
        	    		mon_bw.write(maxWriteDealy);
        	    		mon_bw.write(",");
        	    		mon_bw.write(maxWriteSize);
        	    		mon_bw.write(",");
        	    		mon_bw.write(readChannelLimit);
        	    		mon_bw.write(",");
        	    		mon_bw.write(writeChannelLimit);
        	    		mon_bw.write(",");
        	    		mon_bw.write(readLimit);
        	    		mon_bw.write(",");
        	    		mon_bw.write(writeLimit);
        	    		mon_bw.write(",");
        	    		mon_bw.write(timeNano);
        	    		mon_bw.write(",");
        	    		mon_bw.write(checkInterval_ttc);
        	    		mon_bw.write(",");
        	    		mon_bw.write(lastCumulativeTime);
        	    		mon_bw.write(",");
        	    		mon_bw.write(lastTime);
        	    		mon_bw.write(",");
        	    		mon_bw.write(cumulativeReadBytes);
        	    		mon_bw.write(",");
        	    		mon_bw.write(cumulativeWrittenBytes);
        	    		mon_bw.write(",");
        	    		mon_bw.write(currentReadBytes);
        	    		mon_bw.write(",");
        	    		mon_bw.write(currentWrittenBytes);
        	    		mon_bw.write(",");
        	    		mon_bw.write(lastReadBytes);
        	    		mon_bw.write(",");
        	    		mon_bw.write(lastWrittenBytes);
        	    		mon_bw.write(",");
        	    		mon_bw.write(lastReadThroughput);
        	    		mon_bw.write(",");
        	    		mon_bw.write(lastWriteThroughput);
        	    		mon_bw.write(",");
        	    		mon_bw.write(realWriteThroughput);
        	    		mon_bw.write(",");
        	    		mon_bw.write(realWriteBytes);
 
        	    		mon_bw.newLine();
        	    		
        	    		mon_bw.flush();
        	    		
        	    		//acc_bw.write(accresult);
        	    		//acc_bw.newLine();
        	    		//acc_bw.flush();
        	    		
        	    		
        	    		
        	    	}catch(IOException e){
        	    		log.error("{}",e);
        	    	}finally{
        	    		try{ input_fw.close();}catch(IOException e){}
        	    		try{ input_bw.close();}catch(IOException e){}
        	    		try{ mon_bw.close();}catch(IOException e){}
        	    		try{ mon_fw.close();}catch(IOException e){}
//        	    		try{ acc_bw.close();}catch(IOException e){}
//        	    		try{ acc_fw.close();}catch(IOException e){}
        	    	}
        		}
    	}
    		
    		windowCount++;
    		
    		if (windowCount >= windowSize){
        		windowCount = 0;
        		windowId++;
        	}
    	}
	}
    
    public static boolean dataCollector(long currentTime, int tId, int swDec, int channelNum, int tenantNum, int physwNum, long firstLimit, long monitorInterval, long readLimit, long addstack_read, long currentValue, long remainThroughput, int msgCount){
    	String usrdir = System.getProperty("user.dir");
    	String tx_fileName = usrdir+"/Trafficmeter/tx_data.txt";
    	String rx_fileName = usrdir+"/Trafficmeter/rx_data.txt";
    	
    	FileWriter fw = null;
    	BufferedWriter bw = null;
    	
    	String scurrentTime = String.valueOf(currentTime);
    	String stid = String.valueOf(tId);
    	String sswDec = String.valueOf(swDec);
    	String schannelNum = String.valueOf(channelNum);
    	String stenantNum = String.valueOf(tenantNum);
    	String sphyswNum = String.valueOf(physwNum);
    	String sfirstLimit = String.valueOf(firstLimit);
    	String smonitorInterval = String.valueOf(monitorInterval);
    	String sreadLimit = String.valueOf(readLimit);
    	String sreadstack = String.valueOf(addstack_read);
    	String smsgCount = String.valueOf(msgCount);
    	String sremain = String.valueOf(remainThroughput);
    	String scurrentValue = String.valueOf(currentValue);
    	
    	
    	//edge sw, core sw for linear swtich. 
    	int hostNum = 0;
    	int swlink = 2;
    	int swNum = channelNum;
    	if (swDec == 1 || swDec == channelNum){
    		hostNum = 1;
    		swlink = 1;
    	}
    	String shostNum = String.valueOf(hostNum);
    	String slink = String.valueOf(swlink);
    	//TX data
    	try{
    		fw = new FileWriter(tx_fileName, true);
    		bw = new BufferedWriter(fw);
    		
    		bw.write(scurrentTime);
    		bw.write(",");
    		bw.write(smonitorInterval);
    		bw.write(",");
    		bw.write(stid);
    		bw.write(",");
    		bw.write(sswDec);
    		bw.write(",");
    		bw.write(shostNum);
    		bw.write(",");
    		bw.write(slink);
    		bw.write(",");
    		bw.write(schannelNum);
    		bw.write(",");
    		bw.write(stenantNum);
    		bw.write(",");
    		bw.write(sphyswNum);
    		bw.write(",");
    		bw.write(sreadLimit);
    		bw.write(",");
    		bw.write(sfirstLimit);
    		bw.write(",");
    		bw.write(sreadstack);
    		bw.write(",");
    		bw.write(sremain);
    		bw.write(",");
    		bw.write(smsgCount);
    		bw.write(",");
    		bw.write(scurrentValue);
    		bw.newLine();
    		
    		bw.flush();
    	}catch(IOException e){
    		log.error("{}",e);
    	}finally{
    		try{ fw.close();}catch(IOException e){}
    		try{ bw.close();}catch(IOException e){}
    	}
    	
    	//RX data
    	
    	return true;
    }
    
    public boolean requestInference(int tId, int swDec) throws IOException{
    	String usrdir = System.getProperty("user.dir");
    	String input_fileName = String.valueOf(tId)+"_"+String.valueOf(swDec)+".txt";
    	String input_filePath = usrdir+ "/MeteorPredictor/InputData/" + input_fileName;
    	
    	File file = new File(input_filePath);
    	
    	if (file.exists()){
    		if (file.delete()){
    			//file delete --> create file
    			file.createNewFile();
    		}
    		else{
    			//file create
    			file.createNewFile();
    		}
    	}
    	else{
    		//file create
    		file.createNewFile();
    	}
    	
    	
    	return true;
    }
    public boolean replyInference(){
    	
    	return true;
    }

     public long sendMsg(OFMessage msg, Channel c, int tId, int swDec) {
        //log.info("^^ sendMsg : {}",msg.getType());
//        packetinCount[tId]++;
//        statMsgCount[tId][swDec]++;
    	long throughput = -1;
        if (c != null) {
        	throughput =  checkAndSend(c, msg,tId, swDec);
        } else {
            final Map<Channel, Role> readOnly = Collections
                    .unmodifiableMap(this.currentState.get());
            for (Channel chan : readOnly.keySet()) {
                if (chan == null) {
                    continue;
                }
                throughput = checkAndSend(chan, msg, tId, swDec);
            }
        }
        return throughput;
    }


    public synchronized void removeChannel(Channel channel) {
        this.state = getState();
        this.state.remove(channel);
        setState();
    }

    public synchronized void shutDown() {
        this.state = getState();
        for (Channel c : state.keySet()) {
            if (c != null && c.isConnected()) {
                c.close();
            }
        }
        state.clear();
        setState();
    }

    @Override
    public String toString() {
        return this.currentState.get().toString();
    }
    
    
	
}
