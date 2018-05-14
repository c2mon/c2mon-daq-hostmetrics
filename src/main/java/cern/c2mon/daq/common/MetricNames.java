/******************************************************************************
 * Copyright (C) 2010-2018 CERN. All rights not expressly granted are reserved.
 *
 * This file is part of the CERN Control and Monitoring Platform 'C2MON'.
 * C2MON is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the license.
 *
 * C2MON is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with C2MON. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/
package cern.c2mon.daq.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.Getter;

/**
 * Class to retrieve all available metric constants
 *
 * @author Matthias Braeger
 *
 */
@Getter
public final class MetricNames {

  private final String hostName;

  private final String availableMemory;

  private final String swapUsed;

  private final String systemLoadAverage;

  private final String cpuTemperature;

  private final String cpuVoltage;

  private final String processCount;

  private final String threadCount;

  private final String openFileDescriptors;

  /** Singleton instance */
  private static final MetricNames instance = new MetricNames();

  /**
   * Default Constructor that generates the metric names based on the
   * local host name.
   */
  private MetricNames() {
    try {
      hostName = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      throw new RuntimeException(e);
    }

    availableMemory     = buildName("mem.avail");
    swapUsed            = buildName("mem.swap.used");
    systemLoadAverage   = buildName("cpu.loadavg");
    cpuTemperature      = buildName("cpu.temp");
    cpuVoltage          = buildName("cpu.voltage");
    processCount        = buildName("os.numprocs");
    threadCount         = buildName("os.numthreads");
    openFileDescriptors = buildName("os.fds");
  }


  private String buildName(String metric) {
    return hostName + "/" + metric;
  }

  /**
   * @return The singleton instance of that class
   */
  public static MetricNames getInstance() {
    return instance;
  }
}
