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
        #default = 2 / now, just create only two hosts. / if need more, implement input value(host number)

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
        #self.addLink(self.SwitchList[end-1], self.HostList[1])


class FatTreeTraffic( Topo ):
  CoreSwitchList = []
  AggSwitchList = []
  EdgeSwitchList = []
  HostList = []

  def __init__( self, k, t, cnum):
    " Create Fat Tree topo."
    self.pod = k
    self.connectionNum = cnum
    self.iCoreLayerSwitch = (k/2)**2
    self.iAggLayerSwitch = k*k/2
    self.iEdgeLayerSwitch = k*k/2
    self.density = 2 #k/2
    self.iHost = self.iEdgeLayerSwitch * self.density
    self.mHost = self.iEdgeLayerSwitch*self.density*cnum

    # Init Topo
    Topo.__init__(self)

    self.createTopo()
    logger.debug("Finished topology creation!")

    self.createLink()
    logger.debug("Finished adding links!")

    



  def createTopo(self):
    self.createCoreLayerSwitch(self.iCoreLayerSwitch)
    self.createAggLayerSwitch(self.iAggLayerSwitch)
    self.createEdgeLayerSwitch(self.iEdgeLayerSwitch)
    self.createHost(self.mHost)


  """
  Create Switch and Host
  """

  def _addSwitch(self, number, level, switch_list):
    temp_sw_dpid=str(level)+"000000000000"
    
    for x in xrange(1, number+1):
      POSTFIX="00"
      PREFIX = str(level)+"00"
      if x>=int(16):
        POSTFIX="0"
      if x>=int(256):
        POSTFIX=""

      if x>=int(10):
        PREFIX = str(level) + "0"

      sw_dpid=temp_sw_dpid+POSTFIX+(str(hex(x)).replace("0x",""))
      print(sw_dpid)
      switch_list.append(self.addSwitch('s' + PREFIX + str(x), protocols='OpenFlow13', dpid=sw_dpid))

  def createCoreLayerSwitch(self, NUMBER):
    logger.debug("Create Core Layer")
    self._addSwitch(NUMBER, 1, self.CoreSwitchList)

  def createAggLayerSwitch(self, NUMBER):
    logger.debug("Create Agg Layer")
    self._addSwitch(NUMBER, 2, self.AggSwitchList)

  def createEdgeLayerSwitch(self, NUMBER):
    logger.debug("Create Edge Layer")
    self._addSwitch(NUMBER, 3, self.EdgeSwitchList)

  def createHost(self, NUMBER):
    logger.debug("Create Host")
    for x in xrange(1, NUMBER+1):
      PREFIX = "h00"
      if x >= int(10):
        PREFIX = "h0"
      elif x>= int(100):
        PREFIX = "h"
      IPaddr= "192.0.0.%d" % (x);
      self.HostList.append(self.addHost(PREFIX+str(x), ip=IPaddr))

  """
  Add Link
  """
  def createLink(self):
    logger.debug("Add link Core to Agg.")
    end = self.pod/2
    for x in xrange(0, self.iAggLayerSwitch, end):
      for i in xrange(0, end):
        for j in xrange(0, end):
          self.addLink(self.CoreSwitchList[i*end+j],
            self.AggSwitchList[x+i], bw=bw_core_aggr)

    logger.debug("Add link Agg to Edge.")
    for x in xrange(0, self.iAggLayerSwitch, end):
      for i in xrange(0, end):
        for j in xrange(0, end):
          self.addLink(
            self.AggSwitchList[x+i], self.EdgeSwitchList[x+j], bw=bw_aggr_edge)

    logger.debug("Add link Edge to Host.")  

    for cn in xrange(0, self.connectionNum):
        for x in xrange(0, self.iEdgeLayerSwitch):
          for i in xrange(0, self.density):
            self.addLink(
              self.EdgeSwitchList[x],
              #self.HostList[self.density *x +i], bw=bw_edge_host)
              self.HostList[self.density *x +i+(cn*16)]) # Inf.
            print("switch-host",self.EdgeSwitchList[x],self.HostList[self.density *x +i+(cn*16)])

def chunks(l, n):
  "Divide list l into chunks of size n"
  return [ l[i:i+n] for i in range(0, len(l),n)]

def startpings( host, targetips ):

  targetips=' '.join( targetips )

  cmd = ( 'while true: do '
      ' for ip in %s: do ' % targetips +
      '  echo -n %s "->" $ip ' % host.IP() +
      '   `ping -c1 -w 1 $ip | grep packets` ;'
      '   sleep 1;'
      ' done; '
      'done &' )
  info( '*** Host %s (%s) will be pinging ips: %s\n' %
    ( host.name, host.IP(), targetips))
  host.cmd(cmd)



if __name__ == '__main__':
    setLogLevel('info')
    prompt = "Fattree Topology.. Number of ary? (ex. 4)"
    k = raw_input(prompt)
    k = int(k)

    prompt = "tenant num? "
    t = input(prompt)
    t = int(t)

    cnum = raw_input("Connection Number per tenant? ")
    cnum = int(cnum)
    
    ip = raw_input("Meteor`s IP address? ")
    port = 6633
    
    capa = 100

    bw_core_aggr = capa  # Mbps
    bw_aggr_edge = capa

    fat_topo = FatTreeTraffic(k,t,cnum)
    
    c = RemoteController('c', ip=ip, port=port)
    net = Mininet(topo=fat_topo, autoSetMacs=True, controller=None, link=TCLink)
    net.addController(c)
    net.start()

    info("[Link capacitiy] core-aggr: %d Mbps, aggr-edge: %d Mbps, edge-host: %d Mbps \n" % (
    bw_core_aggr, bw_aggr_edge, bw_edge_host))

    hosts = [0]
    hosts.extend(net.hosts)

    current_time = time.strftime("%m%d_%H%M%S", time.gmtime())
    current_time = (str(current_time))

    print(hosts)

    raw_input("Generate traffic? ")
    c.cmd('date +"%Y-%m-%d %H:%M:%S.%N" > date.txt')


    pport = 3000
    for cnm in range(0,cnum):
        plus = 16*cnm
        clients = [hosts[plus+1],hosts[plus+3],hosts[plus+2],hosts[plus+4],hosts[plus+5],hosts[plus+7],hosts[plus+10],hosts[plus+12]]
        servers= [hosts[plus+13],hosts[plus+15],hosts[plus+6],hosts[plus+8],hosts[plus+9],hosts[plus+11],hosts[plus+14],hosts[plus+16]]
        for te in range(0,t):
            s1 = servers[te]
            c1 = clients[te]
            print("src: " + s1.name, "/ dst: " + c1.name)

            pport = pport + 1
            print("Generate Traffic bewteen %s and %s using port(%s)" % (c1.params['ip'], s1.params['ip'], pport))
            result1 = c1.cmd("iperf3 -s -1 -p %s > iperfResult/host_%s_1.txt &" % (pport, te))
            result2 = s1.cmd("iperf3 -c %s -p %s -n 1 -l 100 > iperfResult/host_%s_2.txt &" % (c1.params['ip'], pport, te))


    for i in range(1,120):
         time.sleep(1)
         print(i)

    CLI(net)

    net.stop()

