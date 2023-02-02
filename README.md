# Control-Channel-Isolation-in-SDN-Virutalization
Meteor: Network hypervisior providing control channel isolation with control traffic prediction (using Meteor predioctor based on LSTM-Autoencoder)

## Overview

We provide Meteor soruce code, which is a network hypervisor based on Libera for SDN virualization. 

We provide here the scripts that can be used to profile the Linux kernel TCP stack running over terabit ethernet networks. [Section 1](#organisation) describes the organisation of the repository. [Section 2](#setup-servers) contains the steps required to setup the servers to perform profiling. This involves patching and installing an appproriate kernel, installing additional tools like `perf`, and configuring the NIC which one would like to use for the profiling. [Section 3](#running-an-experiment) gives a general overview of how to use our evaluation scripts. And finally, in [Section 4](#sigcomm-2021-artifact-evaluation) you will find the necessary instructions to reproduce the results from our SIGCOMM 2021 paper.

## Repository contents 

The repository contains as follows:

* `PhysicalTopology` contains topology generating script codes; `linear.py`, `fattree.py` based on Mininet.
* `SDN controller` contains executing SDN controller(ONOS) script code, `onos.sh`
* `METEOR` contains Java Maven project, `Meteor/` for Meteor implementation 
   * `Meteorstart.sh` for starting Meteor network hypervisor
   * `MeteorPredictor/` for training and inferencing control traffic   

Below you will find instructions on how to use the tools provided in this repository to either reproduce our findings or profile your own setup to explore it's characteristics.

## Setup Servers
