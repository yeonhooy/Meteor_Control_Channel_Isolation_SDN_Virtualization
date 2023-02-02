# Control-Channel-Isolation-in-SDN-Virutalization
Meteor: Network hypervisior providing control channel isolation with control traffic prediction (using *Meteor* predioctor based on LSTM-Autoencoder)

## Overview

We provide *Meteor* soruce code, which is a network hypervisor based on Libera for SDN virualization. 

We provide all of source codes of implementation and scripts that can be used to run whole of SDN virtualization system with our *Meteor* network hypervisor and also provide training/infernce code for *Meteor* predicor, which is a machine learning model for control traffic prediction. 
* [Section 1](#Repository contents) describes the organisation of the repository. 
* [Section 2](#Server setup and environments) contains the steps required to setup the servers to perform profiling. This involves patching and installing an appproriate kernel, installing additional tools like `perf`, and configuring the NIC which one would like to use for the profiling. 
* [Section 3](#Running guide) gives a general overview of how to use our evaluation scripts. 
* [Section 4](#sigcomm-2021-artifact-evaluation) you will find the necessary instructions to reproduce the results from our SIGCOMM 2021 paper.

## Repository contents 

The repository contains as follows:

* `PhysicalTopology` contains topology generating script codes; `linear.py`, `fattree.py` based on Mininet.
* `SDN controller` contains executing SDN controller(ONOS) script code, `onos.sh`
* `Meteor` contains Java Maven project for Meteor implementation 
   * `Meteor/Meteorstart.sh` for starting Meteor network hypervisor
   * `Meteor/MeteorPredictor/` for inferencing control traffic
* `MeteorPredictor_training` contains training codes and example dataset for Meteor predictor    

Below you will find instructions on how to use the tools provided in this repository to either reproduce our findings or profile your own setup to explore it's characteristics.

## Server setup and environments

## Running guide

### Run physical topology

### Run SDN controller

### RUN *Meteor* network hypervisor

### Training Meteor predictor


