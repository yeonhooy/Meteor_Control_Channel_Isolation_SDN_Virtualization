# !/usr/bin/python
import sys, getopt, time, os

from mininet.net import Mininet
from mininet.topo import Topo
from mininet.log import lg, setLogLevel, info, output, warn
from mininet.cli import CLI
from mininet.link import TCLink
from mininet.node import RemoteController, Host, CPULimitedHost
from mininet.node import OVSSwitch
from mininet.util import irange, dumpNodeConnections
from mininet.util import custom, pmonitor
from mininet.clean import cleanup
import logging
from functools import partial
import time

global bw_core_aggr, bw_aggr_edge
logger = logging.getLogger(__name__)
bw_core_aggr = 1000  # Mbps
bw_aggr_edge = 1000
bw_edge_host = 9999  # Inf.
FANOUT = 10


class I2Topo(Topo):

    def __init__(self, enable_all=True):
        "Create twoPath topology."

        # Add default members to class.
        super(I2Topo, self).__init__()

        # Add core switches
        h1 = self.addHost('h1', ip='192.0.0.1', mac='00:aa:aa:05:00:01')
        h2 = self.addHost('h2', ip='192.0.0.2', mac='00:aa:aa:05:00:02')

        s1 = self.addSwitch('s1', protocols='OpenFlow13')
        s2 = self.addSwitch('s2', protocols='OpenFlow13')
        s3 = self.addSwitch('s3', protocols='OpenFlow13')
        s4 = self.addSwitch('s4', protocols='OpenFlow13')
        s5 = self.addSwitch('s5', protocols='OpenFlow13')

        self.addLink(s1, s2)
        self.addLink(s2, s3)
        self.addLink(s3, s4)
        self.addLink(s4, s5)

        self.addLink(h1, s1)
        self.addLink(h2, s5)

class LinearTraffic(Topo):
    SwitchList = []
    EdgeSwitchList = []
    HostList = []

    def __init__(self,k,t):
        self.num = k
        self.tenant = t

        Topo.__init__(self)
        self.createTopo()
        self.createLink()

    def createTopo(self):
        self.addSwitchs(self.num,1,self.SwitchList)
        self.createHost(self.num, self.tenant)

    def addSwitchs(self, number, level, switch_list):
        temp_sw_dpid = str(level) + "000000000000"
        for x in xrange(0, number + 1):
            POSTFIX = "00"
            PREFIX = str(level) + "00"
            if x >= int(16):
                POSTFIX = "0"
            if x >= int(256):
                POSTFIX = ""

            if x >= int(10):
                PREFIX = str(level) + "0"

            sw_dpid = temp_sw_dpid + POSTFIX + (str(hex(x)).replace("0x", ""))
            print(sw_dpid)
            switch_list.append(self.addSwitch('s' + PREFIX + str(x), protocols='OpenFlow13',
                                              dpid=sw_dpid))
    def createHost(self, NUMBER, Tenant):
        logger.debug("Create Host")
        self.HostList.append(self.addHost("h001",ip="192.0.0.1"))
        for x in range(0, NUMBER*Tenant):
            x=x+2
            PREFIX = "h00"
            if x >= int(10):
                PREFIX = "h0"
            if x >= int(100):
                PREFIX = "h"
            IPaddr = "192.0.0.%d" % (x);
            self.HostList.append(self.addHost(PREFIX + str(x), ip=IPaddr))

    def createLink(self):
        end = self.num
        logger.debug("Add link switch to switch")
        print(self.SwitchList)
        for i in range(0,end):
            self.addLink(self.SwitchList[i],self.SwitchList[i+1],bw=bw_core_aggr)
        logger.debug("Add link Edge to Host")
        self.addLink(self.SwitchList[0],self.HostList[0])
        for j in range(1,end+1):
            for v in range(0,self.tenant):
                hostcount = 1+self.tenant*(j-1)+v
                print(j,hostcount)
                self.addLink(self.SwitchList[j],self.HostList[hostcount])


if __name__ == '__main__':
    setLogLevel('info')
    prompt = "Linear Topology..total physical node numbers? "
    k = raw_input(prompt)
    k = int(k)

    prompt = "tenant num? "
    t = input(prompt)
    t = int(t)

    dn = raw_input("virtual node number per tenant? ")
    dn = int(dn)
    
    ip = raw_input("Meteor`s IP address? ")
    #ip = '20.0.0.2'
    port = 6633

    #Link capacity
    capa = 100

    bw_core_aggr = capa  # Mbps
    bw_aggr_edge = capa

    lin_topo = LinearTraffic(k,t)
    c = RemoteController('c', ip=ip, port=port)
    net = Mininet(topo=lin_topo, autoSetMacs=True, controller=None, link=TCLink)
    net.addController(c)
    net.start()

    info("[Link capacitiy] core-aggr: %d Mbps, aggr-edge: %d Mbps, edge-host: %d Mbps \n" % (
    bw_core_aggr, bw_aggr_edge, bw_edge_host))

    hosts = [0]
    hosts.extend(net.hosts)

    current_time = time.strftime("%m%d_%H%M%S", time.gmtime())
    current_time = (str(current_time))

    raw_input("Generate traffic? ")
    c.cmd('date +"%Y-%m-%d %H:%M:%S.%N" > date.txt')

    for te in range(0,t):
        snum = 2+te
        dnum = 2+(dn-1)*t+te
        print("snum: ",snum," dnum: ",dnum)
        s1 = hosts[snum]
        c1 = hosts[dnum]
        print("src: " + s1.name, "/ dst: " + c1.name)

        for j in range(0, 1):
            port = port + 1
            print("Generate Traffic bewteen %s and %s using port(%s)" % (c1.params['ip'], s1.params['ip'], port))
            result1 = c1.cmd("iperf -s -1 -p %s > iperfResult/host_%s_1.txt &" % (port, te))
            result2 = s1.cmd("iperf -c %s -p %s -n 1 -l 100 > iperfResult/host_%s_2.txt &" % (c1.params['ip'], port, te))
            print("traffic connection %s/%s" % (j+1, 128))

    CLI(net)

    net.stop()

