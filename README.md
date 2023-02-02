# Control-Channel-Isolation-in-SDN-Virutalization
Meteor: Network hypervisior providing control channel isolation with control traffic prediction (using *Meteor* predictor based on LSTM-Autoencoder)

## Overview

We provide *Meteor* soruce code, which is a network hypervisor based on Libera for SDN virualization. 

We provide all of source codes of implementation and scripts that can be used to run whole of SDN virtualization system with our *Meteor* network hypervisor and also provide training/infernce code for *Meteor* predicor, which is a machine learning model for control traffic prediction. 
* [Section 1](#Repository-organization) describes the organization of the repository. 
* [Section 2](#Server-setup-and-environments) contains the steps required to setup the servers to run each components; Physical topology, SDN controller, Meteor. Also, it contains the environments for *Meteor* predicor  
* [Section 3](#Running-guide) gives a general overview of how to use our evaluation scripts. 
* (Optional) [Section 4](#Evaluation) Instructions to reproduce the results from our CCgrid 2023 paper.

## Repository organization 

The repository contains as follows:

* `PhysicalTopology/` contains topology generating script codes; `linear.py`, `fattree.py` based on Mininet.
* `SDN controller/` contains executing SDN controller(ONOS) script code, `onos.sh`
* `Meteor/` contains Java Maven project for Meteor implementation 
   * `Meteor/Meteorstart.sh` for starting Meteor network hypervisor
   * `Meteor/MeteorPredictor/` for inferencing control traffic
* `MeteorPredictor_training/` contains training codes and example dataset for Meteor predictor.    


## Server setup and environments
### 1. Physical network emulation
* Prerequisite
  * install Mininet
    `sudo apt-get install mininet`
  * install iperf3
    `sudo apt-get install iper3`
* Version 
  * OS: Linux ununtu 18.04 or higher 
  * OVS: 2.9.0 or higher (`sudo ovs-ofctl --version`)
  * python: 2.7.17 or higher
  
### 2. SDN controller
* Version
  * OS: Linux ununtu 16.04 or higher
  * docker: 18.09.3 or higher
  
### 3. *Meteor*
* Prerequisite
  * install mvn (Apache maven 3.0.5 or higher)
  * build Meteor mvn project 
  ```
  cd Meteor/
  sudo mvn package
  ```
* Version
  * OS: Linux ununtu 14.04 or higher 
  * python3: 3.6.3 or higher 
  * Java: 1.7.0 or higher
  * Apache maven: 3.0.5 or higher 
  
### 4. Network configuration
* Make communication between Physcial network server <-> Meteor (`ping` test)
* Make communication between SDN controller server <-> Meteor (`ping ` test)
* Ip address of three servers will be used in [later](#Running-guide)

### 5. *Meteor* predictor
* Prerequisite
  * install libraries with `conda`
  ```
  sudo 
  ```
* Version 
  * python
  * pytorch

## Running guide

### 1. Run physical topology
We proivde two network topology, linear topolgoy for `PhysicalTopology/linear.py` and fattree topology for `PhysicalTopology/fattree.py`
These python script uses Mininet API and Mininet utilizes OVS for Openflow switch. We generate traffic using `iperf3` tool. You can check the result and log of iperf3 at `/PhysicalTopology/iperfResult`

#### Linear topology
```
sudo python linear.py
- Linear Topology..total physical node numbers? > e.g., 16 
- Tenant num? > e.g., 1
- Virtual node number per tenant? > e.g., 16
- Meteor`s IP address? > e.g., 20.0.0.2
- Generate traffic? > Wait for the virtual network to be created; When VN created, press any key
```
#### Fat-tree topology
```
sudo python linear.py
- Fattree Topology..Number of ary? > e.g., 4
- Tenant num? > e.g., 1
- connection number per tenant? > e.g., 1
- Meteor`s IP address? > e.g., 20.0.0.2
- Generate traffic? > Wait for the virtual network to be created; When VN created, press any key
```

### 2. Run SDN controller
```
sudo sh onos.sh -t <total tenant number> -i <SDN controller server IP address> 
```

### 3. Run *Meteor* network hypervisor
### run *Meteor*
### generate virtual network per tenant

### 4. Training Meteor predictor

## Evaluation


