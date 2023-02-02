# Control-Channel-Isolation-in-SDN-Virutalization
Meteor: Network hypervisior providing control channel isolation with control traffic prediction (using *Meteor* predictor based on LSTM-Autoencoder)

## Overview

We provide *Meteor* soruce code, which is a network hypervisor based on Libera for SDN virualization. 

We provide all of source codes of implementation and scripts that can be used to run whole of SDN virtualization system with our *Meteor* network hypervisor and also provide training/infernce code for *Meteor* predicor, which is a machine learning model for control traffic prediction. 
* [Section 1](#Repository-organization) describes the organization of the repository. 
* [Section 2](#Server-setup-and-environments) contains the steps required to setup the servers to run each components; Physical topology, SDN controller, Meteor. Also, it contains the environments for *Meteor* predicor  
* [Section 3](#Running-guide) gives a general overview of how to use our evaluation scripts. 
* [Section 4](#Evaluation) you will find the necessary instructions to reproduce the results from our CCgrid 2023 paper.

## Repository organization 

The repository contains as follows:

* `PhysicalTopology/` contains topology generating script codes; `linear.py`, `fattree.py` based on Mininet.
* `SDN controller/` contains executing SDN controller(ONOS) script code, `onos.sh`
* `Meteor/` contains Java Maven project for Meteor implementation 
   * `Meteor/Meteorstart.sh` for starting Meteor network hypervisor
   * `Meteor/MeteorPredictor/` for inferencing control traffic
* `MeteorPredictor_training/` contains training codes and example dataset for Meteor predictor    

Below you will find instructions on how to use the tools provided in this repository to either reproduce our findings or profile your own setup to explore it's characteristics.

## Server setup and environments
### 1. Physical network emulation
### 2. SDN controller
### 3. *Meteor*
### 4. Network configuration
### 5. *Meteor* predictor


## Running guide

### 1. Run physical topology

### 2. Run SDN controller

### 3. RUN *Meteor* network hypervisor

### 4. Training Meteor predictor

## Evaluation


