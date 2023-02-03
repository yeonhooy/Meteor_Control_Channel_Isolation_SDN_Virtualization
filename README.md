# Control-Channel-Isolation-in-SDN-Virutalization
This repository contains the implementation of *Meteor*, a network hypervisor for control channel isolation with control traffic prediction.

*Meteor* deploys LSTM-autoencoder for control channel isolation, and please refer to our paper for more details.

## Overview

We provide all source codes, including *Meteor* implementation and experiment scripts. The provided codes run the entire virtualized SDN system consisting of emulated physical network, *Meteor* hypervisor, virtualized networks, and SDN controllers for the virtualized networks.

The code also includes the training and inference process of *Meteor* predictor, a machine learning model for control traffic prediction. With the *Meteor* predictor, *Meteor* achieves control channel isolation. 

* [Part 1](#Repository-organization) describes the source-code organization of this repository.
* [Part 2](#Settings) contains the steps to configure dependencies and compilation to run *Meteor*. We provide setting steps for physical network emulation, SDN controller, *Meteor*, network configurations, and *Meteor* predictor.
* [Part 3](#Execution-guide) gives a general guide on executing the codes. 

## Repository organization 

The repository is organized as follows:

* `PhysicalTopology/`: contains scripts for physical network topology using Mininet; `linear.py`, `fattree.py`.
* `SDNcontroller/`: contains scripts for executing SDN controllers that control virtualized networks; `onos.sh`.
* `Meteor/`: contains the core implementation of *Meteor* hypervisor. *Meteor* is built as Java Maven project.
   * `Meteor/Meteorstart.sh` starts the *Meteor* hypervisor
   * `Meteor/vnCreation/` contains scripts for virtual network creations
   * `Meteor/MeteorPredictor/` contains scripts for control traffic inference by *Meteor* predictor
   * `Meteor/MeteorPredictor/model/meterPredictor.pt` is the pre-trained *Meteor* predictor model used in our study
* `MeteorPredictor_training/`: contains training codes and training dataset for *Meteor* predictor.
  * We implemented *Meteor* predictor as an LSTM autoencoder based on this [implementation](https://github.com/lkulowski/LSTM_encoder_decoder).


## Settings
### 1. Physical network emulation
* Prerequisite
  * Install Mininet for physical network emulation

    `sudo apt-get install mininet`
  * Install iperf3 for data plane traffic generation

    `sudo apt-get install iperf3`
* Software versions 
  * OS: Linux ununtu 18.04 or higher 
  * Open vSwitch of the Mininet: 2.9.0 or higher (`sudo ovs-ofctl --version`)
  * python: 2.7.17 or higher
  
### 2. SDN controller
* Software versions 
  * OS: Linux ununtu 16.04 or higher
  * Docker: 18.09.3 or higher
  
### 3. *Meteor*
* Prerequisite
  * Install mvn (Apache maven 3.0.5 or higher)
  * Build Meteor project
    ```
    cd Meteor/
    sudo mvn package
    ```
  * Install libraries required for *Meteor* predictor
    ```
    pip install numpy
    pip install pandas
    pip install torch
    pip install sympy
    pip install sklearn
    ```
* oftware versions 
  * OS: Linux ununtu 14.04 or higher 
  * python3: 3.6.3 or higher 
  * Java: 1.7.0 or higher
  * Apache maven: 3.0.5 or higher 
  
### 4. Network configurations
* Make communication between Physcial network server <-> Meteor (`ping` test)
* Make communication between SDN controller server <-> Meteor (`ping ` test)
* IP address of three servers will be used in [later](#Execution-guide)

### 5. *Meteor* predictor
* Prerequisite: install required dependencies and libraries with `conda`

* Python packages (install with conda)
  * The dependencies and requirements of our conda setting are given in "MeteorPredictor_training/meteor_training_requirement.txt". You can set a similar conda environment through the following command.
  ```
  conda install -n <env_name> meteor_training_requirement.txt
  ```


## Execution guide

### 1. Run physical network
We provide two physical network topologies, linear topology (`PhysicalTopology/linear.py`) and fat-tree topology (`PhysicalTopology/fattree.py`).

These python scripts use Mininet API, and Mininet utilizes Open vSwitches for SDN switch emulation. We generate traffic using `iperf3`. You can check the result and log of iperf3 at `/PhysicalTopology/iperfResult`

* Linear topology example
  ```
  sudo python linear.py
  - Linear Topology..total physical node numbers? > e.g., 16 
  - Tenant num? > e.g., 1
  - Virtual node number per tenant? > e.g., 16
  - Meteor`s IP address? > e.g., 20.0.0.2
  - Generate traffic? > Wait for the virtual network to be created; When VN created, press any key to generate traffic
  ```
* Fat-tree topology example
  ```
  sudo python linear.py
  - Fattree Topology..Number of ary? > e.g., 4
  - Tenant num? > e.g., 1
  - connection number per tenant? > e.g., 1
  - Meteor`s IP address? > e.g., 20.0.0.2
  - Generate traffic? > Wait for the virtual network to be created; When VN created, press any key to generate traffic
  ```

### 2. Run SDN controller
* Run ONOS controller by a Docker container
  ```
  sudo sh onos.sh -t <total tenant number> -i <SDN controller server IP address> 
  ```
* Check the executed ONOS controller status
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
  * Automatic generation of virtual network topology scripts 
    * Virtual network topology of linear example
      ```
      cd Meteor/vnCreation/linear
      python vncreation_linear.py
      - Total tenant num? > 
      - VN`s node number? >
      - Tenant (SDN controller)`s Ip address? > 
      Output: total_<tenantNum>_<VNnodeNum>.sh

      ```
    * Virtual network topology of tree example
      ```
      cd fattree/
      For fattree, We provide pre-created script for two types of VN topology / image attach
      In this scirpts, we support up to eight tenants. Also, we only create one host pair in one VN. If you need, you can modify code freely.
      2-1) 2-pod topology: t<tenantID>.sh 
      2-2) full-pod topology: fullpod_t<tenantID>.sh
      ```
      * For the tree example, two kinds of topology scripts are produced: 1) 2-pod and 2) full-pod, which are depicted as the left and right subfigures below.

        ![image](https://user-images.githubusercontent.com/17779090/216348754-30960838-c754-4963-9859-9e68fde3cc4f.png)
      ![image](https://user-images.githubusercontent.com/17779090/216348801-8cac45fe-7a36-4759-b072-85e1ee6e1231.png)
      

  * Create virtual networks with the generated scripts 
    ```
    1) linear VN topology: `sudo sh total_<tenantNum>_<VNnodeNum>.sh`
    2) fattree VN topology: `sudo sh t<tenantID>.sh  $IP_address` or `sudo sh fullpod_t<tenantID>.sh  $IP_address`
      *In fattree case, you have to put <IP_address> for argument
    ```
  
* Identify the created virtual network topology 
  * From the SDN controller side, access the SDN controller via web browser.
  * The address of the SDN controller is http://'server IP address':GUI_port.
    * The GUI_port is set as 1000 + virtual network ID.
    * For example, the GUI_port of the first virtual network is 1000 (1000 + 0).

  ![image](https://user-images.githubusercontent.com/17779090/216348067-68309122-8f9e-43cb-829c-b7a762379cbf.png)


* Identify the operation of control channel isolation
  * You can observe that *Meteor* regularly predicts control traffic and calculate γ as the following figure. These operations occur per every window and every control channel of a virtual switch. 
  ![image](https://user-images.githubusercontent.com/17779090/216380056-943f2f95-5669-4816-8922-98458d44cfc7.png)

 
### 4. Training Meteor predictor
* By the following commands, you can train your own *Meteor* predictor. 
  ```
  cd MeteorPredictor_training
  sudo python meteor_train.py --help  
  sudo python meteor_train.py --dataset dataset/control_traffic_dataset.csv --model model/meteorpredictor.pt --io 20 --wo 20
  ```



