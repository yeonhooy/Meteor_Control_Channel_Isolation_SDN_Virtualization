#!/bin/bash
# Written by Gyeongsik Yang and Yeonho Yoo
help() {
	echo "USAGE:"
	echo "\tsudo sh onos_multiple.sh -[opt] [value]\n"
	echo "OPTIONS (0 is default):"
	echo "\t-t [num] : The number of tenants (# of ONOS Docker) - Required"
  echo "\t-i [address] : The address of SDN controller server - Required"
    exit 0
}
num_tenants=0
ip_address="20.1.0.3"
net_mode=0 # default container networking mode
onos_ver=0 # orginal (default)
match_port=0 # Match TCP/UDP port 
if [ "$#" -lt 3 ]; then # One parameter (t) is needed at least
	help
fi
while getopts "t:i:" opt
do
    case $opt in
        t) num_tenants=$OPTARG
          ;;
	i) ip_address=$OPTARG
	  ;;
        ?) help ;;
    esac
done
if [ $num_tenants -eq 0 ]; then # "t" is needed 
	help
fi
default_openflow=10000
default_gui=20000
default_cli=30000
default_cluster=40000


echo "Now deleting all running dockers\n"
sudo docker stop $(sudo docker ps -a -q);
sudo docker rm -f $(sudo docker ps -a -q);

echo "Now running [$num_tenants] onos dockers\n"
onos_version="onos_original"
network_mode=""
match_port_="True"
if [ $onos_ver -eq 1 ]; then
	onos_version="onos_hsjin"
fi
if [ $net_mode -eq 1 ]; then
	network_mode="--network host"
fi
if [ $match_port -eq 1 ]; then
	match_port_="False"
fi
echo "ONOS Version: [$onos_version]"
echo "Network mode: [$network_mode] (Null is default)"
echo "Match TCP/UDP: [$match_port_]"

for num in $(seq 0 $(($num_tenants-1)))
do
	# Calculate port numbers for each docker
	openflow_port=$(($default_openflow+$num))
	gui_port=$(($default_gui+$num))
	cli_port=$(($default_cli+$num))
	cluster_port=$(($default_cluster+$num))
	
	network_port_mapping="-p $openflow_port:6653 -p $gui_port:8181 -p $cli_port:8101 -p $cluster_port:9876" 
	if [ $net_mode -eq 1 ]; then
		network_port_mapping=""
	fi

	# Run ONOS docker and get its IP
	command_params="-td $network_mode $network_port_mapping --name onos_$num $onos_version";
	echo "Command: sudo docker run $command_params" 
	sudo docker run $command_params 
	
done

echo "\n\nNow waiting for REST api to be up...."
for i in $(seq 0 60)
do
	sleep 1
	printf "\r       %02d s" $i
done
printf "\r"

for num in $(seq 0 $(($num_tenants-1)))
do
	# Run ONOS docker and get its IP
	onos_ip=$(sudo docker inspect --format '{{ .NetworkSettings.IPAddress }}' "onos_$num")
	
	if [ x$onos_ip = x ]; then
		onos_ip=ip_address
	fi

	# Activate fwd, openflow
	curl -sS --user karaf:karaf -X POST http://$onos_ip:8181/onos/v1/applications/org.onosproject.fwd/active
	curl -sS --user karaf:karaf -X POST http://$onos_ip:8181/onos/v1/applications/org.onosproject.openflow/active

	# Set options of apps
	curl -sS --user karaf:karaf -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' http://$onos_ip:8181/onos/v1/configuration/org.onosproject.net.flow.impl.FlowRuleManager -d '{ "fallbackFlowPollFrequency": "3000",  "allowExtraneousRules": "true"}'

	if [ $match_port -eq 0 ]; then
		curl -sS --user karaf:karaf -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' http://$onos_ip:8181/onos/v1/configuration/org.onosproject.fwd.ReactiveForwarding -d '{ "matchTcpUdpPorts": "true", "matchIpv4Address": "true", "flowTimeout": "500000"}'
	else
		curl -sS --user karaf:karaf -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' http://$onos_ip:8181/onos/v1/configuration/org.onosproject.fwd.ReactiveForwarding -d '{ "matchTcpUdpPorts": "false", "matchIpv4Address": "true", "flowTimeout": "500000"}'
	fi
		curl -sS --user karaf:karaf -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' http://$onos_ip:8181/onos/v1/configuration/org.onosproject.provider.lldp.impl.LldpLinkProvider -d '{ "probeRate": "6000", "staleLinkAge": "1000000"}'
		curl -sS --user karaf:karaf -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' http://$onos_ip:8181/onos/v1/configuration/org.onosproject.provider.of.device.impl.OpenFlowDeviceProvider -d '{ "portStatsPollFrequency": "1000"}'
done

