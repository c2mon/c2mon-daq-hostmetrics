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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.c2mon.client.core.service.ConfigurationService;
import cern.c2mon.daq.config.DaqProperties;
import cern.c2mon.daq.hostmetrics.HostMetricsMessageHandler;
import cern.c2mon.shared.client.alarm.condition.RangeAlarmCondition;
import cern.c2mon.shared.client.configuration.api.alarm.Alarm;
import cern.c2mon.shared.client.configuration.api.tag.CommandTag;
import cern.c2mon.shared.client.configuration.api.tag.DataTag;
import cern.c2mon.shared.common.datatag.DataTagAddress;
import cern.c2mon.shared.common.datatag.address.impl.SimpleHardwareAddressImpl;

/**
 * Configures a new process in C2MON, if not yet done.
 *
 * @author Justin Lewis Salmon, Matthias Braeger
 */
@Configuration
public class ConfigurationInitializer {

  @Bean
  public InitializingBean autoConfigureTags(ApplicationContext context) {
    return () -> configureTags(context);
  }

  private void configureTags(ApplicationContext context) {
    ConfigurationService configurationService = context.getBean(ConfigurationService.class);
    MetricNames metrics = MetricNames.getInstance();

    String hostName = metrics.getHostName();
    String processName = getProcessName(context);

    if (isProcessConfigured(configurationService, processName)) {
      return;
    }

    configurationService.createProcess(processName);
    configurationService.createEquipment(processName, hostName, HostMetricsMessageHandler.class.getName());

    configurationService.createDataTag(hostName, DataTag.create(metrics.getAvailableMemory(), Long.class, new DataTagAddress())
        .description("Available memory")
        .unit("bytes")
        .addMetadata("responsible", "Joe Bloggs").build());

    configurationService.createDataTag(hostName, DataTag.create(metrics.getSwapUsed(), Long.class, new DataTagAddress())
        .description("Amount of swap space used")
        .unit("bytes")
        .addMetadata("responsible", "Joe Bloggs").build());

    configurationService.createDataTag(hostName, DataTag.create(metrics.getSystemLoadAverage(), Double.class, new DataTagAddress())
        .description("The system load average for the last minute")
        .unit("%")
        .addMetadata("responsible", "John Doe").build());

    configurationService.createDataTag(hostName, DataTag.create(metrics.getCpuTemperature(), Double.class, new DataTagAddress())
        .description("CPU temperature")
        .unit("°C")
        .addMetadata("responsible", "John Doe").build());

    // Causes an exception on the server from 1.9.3-SNAPSHOT onwards (RangeAlarmCondition is the new class)
    configurationService.createAlarm(metrics.getCpuTemperature(),
        Alarm.create("/cpu.temp", "high", 1, new RangeAlarmCondition(0, 90, true))
            .addMetadata("causes", "The CPU temperature is too high")
            .build());

    configurationService.createDataTag(hostName, DataTag.create(metrics.getCpuVoltage(), Double.class, new DataTagAddress())
        .description("CPU voltage")
        .unit("Volts")
        .addMetadata("responsible", "John Doe").build());

    configurationService.createDataTag(hostName, DataTag.create(metrics.getProcessCount(), Integer.class, new DataTagAddress())
        .description("Number of running processes")
        .addMetadata("responsible", "Sue West").build());

    configurationService.createDataTag(hostName, DataTag.create(metrics.getThreadCount(), Integer.class, new DataTagAddress())
        .description("Number of running threads")
        .addMetadata("responsible", "Sue West").build());

    configurationService.createDataTag(hostName, DataTag.create(metrics.getOpenFileDescriptors(), Long.class, new DataTagAddress())
        .description("Number of open file descriptors")
        .addMetadata("responsible", "Sue West").build());

    configurationService.createCommandTag(hostName, CommandTag.create(hostName + "/ping", String.class, new SimpleHardwareAddressImpl("ping"),
        5000, 2000, 100, 0, "test", "test", "test")
        .build());
  }

  /**
   * Checks whether the server has already a configuration for the given Process name
   * @param configurationService The C2MON configuration service
   * @param processName name of the DAQ process
   * @return <code>true</code>, if DAQ process is already configured
   */
  private boolean isProcessConfigured(ConfigurationService configurationService, String processName) {
    return configurationService.getProcessNames().stream().anyMatch(p -> p.getProcessName().equals(processName));
  }

  private String getProcessName(ApplicationContext context) {
    DaqProperties properties = context.getBean(DaqProperties.class);
    return properties.getName();
  }
}
