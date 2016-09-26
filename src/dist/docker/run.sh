#!/usr/bin/env bash
docker run --rm --name daq-hostmetrics -it gitlab-registry.cern.ch/c2mon/c2mon-daq-hostmetrics bin/C2MON-DAQ-STARTUP.jvm -f $@
