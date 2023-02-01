# Control-Channel-Isolation-in-SDN-Virutalization
Meteor: Network hypervisior providing control channel isolation with control traffic prediction (using Meteor predioctor based on LSTM-Autoencoder)

### Overview

We provide here the scripts that can be used to profile the Linux kernel TCP stack running over terabit ethernet networks. [Section 1](#organisation) describes the organisation of the repository. [Section 2](#setup-servers) contains the steps required to setup the servers to perform profiling. This involves patching and installing an appproriate kernel, installing additional tools like `perf`, and configuring the NIC which one would like to use for the profiling. [Section 3](#running-an-experiment) gives a general overview of how to use our evaluation scripts. And finally, in [Section 4](#sigcomm-2021-artifact-evaluation) you will find the necessary instructions to reproduce the results from our SIGCOMM 2021 paper.
