# Control-Channel-Isolation-in-SDN-Virutalization
Meteor: Network hypervisior providing control channel isolation with control traffic prediction (using Meteor predioctor based on LSTM-Autoencoder)

## Overview

We provide Meteor soruce code, which is a network hypervisor based on Libera for SDN virualization. 

We provide here the scripts that can be used to profile the Linux kernel TCP stack running over terabit ethernet networks. [Section 1](#organisation) describes the organisation of the repository. [Section 2](#setup-servers) contains the steps required to setup the servers to perform profiling. This involves patching and installing an appproriate kernel, installing additional tools like `perf`, and configuring the NIC which one would like to use for the profiling. [Section 3](#running-an-experiment) gives a general overview of how to use our evaluation scripts. And finally, in [Section 4](#sigcomm-2021-artifact-evaluation) you will find the necessary instructions to reproduce the results from our SIGCOMM 2021 paper.

## Repository contents 

The repository contains as follows:

* `PhysicalTopology` contains topology generating script codes; `linear.py`, `fattree.py` based on Mininet.
* `SDN controller` contains executing SDN controller(ONOS) script code, `onos.sh`
* `Meteor` contains Java xx codes for executing Meteor network hypervisor and Meteor  
* `Meteor predictor` contains training/inferencing codes      
* `scripts` contains scripts used to run experiments for our SIGCOMM 2021 paper.
    * `scripts/sender` are the scripts that must be run on the sender-side.
    * `scripts/receiver` are the respective receiver-side scripts.
    * `scripts/parse` are the scripts that can be used to parse and pretty print the results of an experiment after it's finished.
* `run_experiment_sender.py`, `run_experiment_receiver.py` are the scripts that actually run the experiment.
    * `network_setup.py` allows us to configure the NIC to enable/disable various offloads, set parameters and so on.
    * `constants.py` contains the constants used by our scripts.
    * `process_output.py` contains utilily code to parse outputs from the benchmarking programs.
* `symbol_mapping.tsv` is a map from kernel symbols/function names to the classification into one of seven categories depending on their function or their location in the kernel TCP stack.

Below you will find instructions on how to use the tools provided in this repository to either reproduce our findings or profile your own setup to explore it's characteristics.

## Setup Servers
