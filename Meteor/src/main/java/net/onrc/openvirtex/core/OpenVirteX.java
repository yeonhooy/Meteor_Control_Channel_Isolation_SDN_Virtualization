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
package net.onrc.openvirtex.core;

import net.onrc.openvirtex.core.cmd.CmdLineSettings;
import net.onrc.openvirtex.core.cmd.Cmdinjava;
import net.onrc.openvirtex.exceptions.OpenVirteXException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import net.onrc.openvirtex.elements.datapath.role.RoleManager;


public final class OpenVirteX {

    public static final String VERSION = "Libera-0.1";
    private static Logger log = LogManager.getLogger(OpenVirteX.class.getName());

    /**
     * Overrides default constructor to no-op private constructor.
     * Required by checkstyle.
     */
    private OpenVirteX() {
    }

    /**
     * Main method to start the OVX controller. Parses command line arguments.
     *
     * @param args string of command line parameters
     * @throws IOException 
     */
    public static void main(final String[] args) throws OpenVirteXException, IOException {
        final CmdLineSettings settings = new CmdLineSettings();
        final CmdLineParser parser = new CmdLineParser(settings);
        final Cmdinjava cmdline = new Cmdinjava();
        
        try {
            parser.parseArgument(args);
        } catch (final CmdLineException e) {
            parser.printUsage(System.out);
            System.exit(1);
        }

        final OpenVirteXController ctrl = new OpenVirteXController(settings);
//        
        long start = System.currentTimeMillis();
        String sstart = String.valueOf(start);
        String usrdir = System.getProperty("user.dir");
        OpenVirteX.log.info("now directory {}", usrdir);
        String tx_fileName = usrdir+"/Trafficmeter/tx_data.txt";
    	String rx_fileName = usrdir+"/Trafficmeter/rx_data.txt";
    	FileWriter fw = null;
    	FileWriter fr = null;
    	BufferedWriter bw = null;
    	BufferedWriter br = null;
    	
    	try{
    		fw = new FileWriter(tx_fileName, true);
    		fr = new FileWriter(rx_fileName, true);
    		bw = new BufferedWriter(fw);
    		br = new BufferedWriter(fr);
    		
    		bw.write(sstart);
    		bw.newLine();
    		bw.flush();
    		
    		br.write(sstart);
    		br.newLine();
    		br.flush();
    	}catch(IOException e){
    		log.error("{}",e);
    	}finally{
    		try{ fw.close();}catch(IOException e){}
    		try{ bw.close();}catch(IOException e){}
    		try{ fr.close();}catch(IOException e){}
    		try{ br.close();}catch(IOException e){}
    	}
        
        
        String cmds = "touch contorller.txt";
        String[] callCmd = {"/bin/bash","-c",cmds};
        Map map = cmdline.execCommand(callCmd);
        
        System.out.println(callCmd);
        RoleManager.readNumTenants();
        
        OpenVirteX.log.info("******* Suceess to start Meteror!! *******");
        ctrl.run();
    }

}
