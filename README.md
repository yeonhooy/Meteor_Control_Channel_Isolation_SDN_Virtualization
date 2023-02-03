# Control-Channel-Isolation-in-SDN-Virutalization
Meteor: Network hypervisior providing control channel isolation with control traffic prediction (using *Meteor* predictor based on LSTM-Autoencoder)

## Overview

We provide *Meteor* soruce code, which is a network hypervisor based on Libera for SDN virualization. 

We provide all of source codes of implementation and scripts that can be used to run whole of SDN virtualization system with our *Meteor* network hypervisor and also provide training/infernce code for *Meteor* predicor, which is a machine learning model for control traffic prediction. 
* [Section 1](#Repository-organization) describes the organization of the repository. 
* [Section 2](#Server-setup-and-environments) contains the steps required to setup the servers to run each components; Physical topology, SDN controller, Meteor. Also, it contains the environments for *Meteor* predicor  
* [Section 3](#Running-guide) gives a general overview of how to use our evaluation scripts. 

## Repository organization 

The repository contains as follows:

* `PhysicalTopology/` contains topology generating script codes; `linear.py`, `fattree.py` based on Mininet.
* `SDNcontroller/` contains executing SDN controller(ONOS) script code, `onos.sh`
* `Meteor/` contains Java Maven project for Meteor implementation 
   * `Meteor/Meteorstart.sh` for starting Meteor network hypervisor
   * `Meteor/vnCreation/` scripts for creating virtual network topology
   * `Meteor/MeteorPredictor/` for inferencing control traffic
   * `Meteor/MeteorPredictor/model/meterPredictor.pt` for our pre-trained model
* `MeteorPredictor_training/` contains training codes and example dataset for Meteor predictor.
  * Meteor predictor is implemented by [LSTM-Autoencoder](https://github.com/lkulowski/LSTM_encoder_decoder)


## Server setup and environments
### 1. Physical network emulation
* Prerequisite
  * install Mininet
    `sudo apt-get install mininet`
  * install iperf3
    `sudo apt-get install iperf3`
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
  * install libraries for *Meteor* predictor
  ```
  pip install numpy
  pip install pandas
  pip install torch
  pip install sympy
  pip install sklearn
  ```
* Version
  * OS: Linux ununtu 14.04 or higher 
  * python3: 3.6.3 or higher 
  * Java: 1.7.0 or higher
  * Apache maven: 3.0.5 or higher 
  
### 4. Network configuration
* Make communication between Physcial network server <-> Meteor (`ping` test)
* Make communication between SDN controller server <-> Meteor (`ping ` test)
* IP address of three servers will be used in [later](#Running-guide)

### 5. *Meteor* predictor
* Prerequisite
  * install libraries with `conda`
  ```
  sudo 
  ```
* Version 
  * python
  * pytorch
* Python packages (install with conda)
The dependencies and requirements of our conda setting are given in "MeteorPredictor_training/meteor_training_requirement.txt". You can set a similar conda environment through the following command.
```
conda install -n <env_name> meteor_training_requirement.txt
```


## Running guide

### 1. Run physical topology
We proivde two network topology, linear topolgoy for `PhysicalTopology/linear.py` and fattree topology for `PhysicalTopology/fattree.py`
These python scripts use Mininet API and Mininet utilizes OVS for Openflow switch. We generate traffic using `iperf3` tool. You can check the result and log of iperf3 at `/PhysicalTopology/iperfResult`

* Linear topology
```
sudo python linear.py
- Linear Topology..total physical node numbers? > e.g., 16 
- Tenant num? > e.g., 1
- Virtual node number per tenant? > e.g., 16
- Meteor`s IP address? > e.g., 20.0.0.2
- Generate traffic? > Wait for the virtual network to be created; When VN created, press any key to generate traffic
```
* Fat-tree topology
```
sudo python linear.py
- Fattree Topology..Number of ary? > e.g., 4
- Tenant num? > e.g., 1
- connection number per tenant? > e.g., 1
- Meteor`s IP address? > e.g., 20.0.0.2
- Generate traffic? > Wait for the virtual network to be created; When VN created, press any key to generate traffic
```

### 2. Run SDN controller
* Run ONOS controller (run docker container)
```
sudo sh onos.sh -t <total tenant number> -i <SDN controller server IP address> 
```
* check ONOS controller status
```
sudo docker ps 
```

### 3. Run *Meteor* network hypervisor
* Run *Meteor*
```
cd Meteor/
echo `tenants number` > config.txt 
sudo sh run_meteor.sh

if success, you can find *Success to start Meteor!!*
```
![image](https://user-images.githubusercontent.com/17779090/216503110-fd1f1c34-ce73-4e3b-8ff8-e07e93411505.png)



* Generate virtual network per tenant
  * Generate vncreation script    
  ```
  1) linear VN topology
  cd Meteor/vnCreation/linear
  python vncreation_linear.py
   - Total tenant num? > 
   - VN`s node number? >
   - Tenant (SDN controller)`s Ip address? > 
 
   *Output: total_<tenantNum>_<VNnodeNum>.sh
   
   2) tree VN topology
   cd fattree/
   For fattree, We provide pre-created script for two types of VN topology / image attach
   In this scirpts, we support up to eight tenants. Also, we only create one host pair in one VN. If you need, you can modify code freely.
   2-1) 2-pod topology: t<tenantID>.sh 
   2-2) full-pod topology: fullpod_t<tenantID>.sh
  ```
  ![image](https://user-images.githubusercontent.com/17779090/216348754-30960838-c754-4963-9859-9e68fde3cc4f.png)
  ![image](https://user-images.githubusercontent.com/17779090/216348801-8cac45fe-7a36-4759-b072-85e1ee6e1231.png)
  
  * Create vn    
  ```
  1) linear VN topology: `sudo sh total_<tenantNum>_<VNnodeNum>.sh`
  2) fattree VN topology: `sudo sh t<tenantID>.sh  $IP_address` or `sudo sh fullpod_t<tenantID>.sh  $IP_address`
    *In fattree case, you have to put <IP_address> for argument
  ```
  
* Check the created VN topology 
  * Meteor side
    You can see the Meteor log, and find "StartOVXNetwork - Booted virtual network "
  * SDN controller (Tenant) side
    Access `SDN controller server IP adress`:GUI port
    GUIport is set to 1000+tenantID. For example, Tenant1's GUI port is 1000.
    ![image](https://user-images.githubusercontent.com/17779090/216348067-68309122-8f9e-43cb-829c-b7a762379cbf.png)


* Meteor log
  * You can find Meteor predicts Control traffic and calcualte γ, every window and per control channel (virtual switch)
  * Meteor executes `Meteor/MeteorPredictor/inference.sh` every window
  ![image](https://user-images.githubusercontent.com/17779090/216380056-943f2f95-5669-4816-8922-98458d44cfc7.png)

 
### 4. Training Meteor predictor

```
cd MeteorPredictor_training
sudo python meteor_train.py --help  
sudo python meteor_train.py --dataset dataset/control_traffic_dataset.csv --model model/meteorpredictor.pt --io 20 --wo 20
```



