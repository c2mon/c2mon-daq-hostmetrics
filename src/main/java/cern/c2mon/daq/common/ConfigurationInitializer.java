package cern.c2mon.daq.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import cern.c2mon.client.core.service.ConfigurationService;
import cern.c2mon.shared.client.configuration.ConfigurationReport;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.c2mon.daq.config.DaqProperties;
import cern.c2mon.daq.hostmetrics.HostMetricsMessageHandler;
import cern.c2mon.shared.client.configuration.api.alarm.Alarm;
import cern.c2mon.shared.client.configuration.api.alarm.RangeCondition;
import cern.c2mon.shared.client.configuration.api.tag.CommandTag;
import cern.c2mon.shared.client.configuration.api.tag.DataTag;
import cern.c2mon.shared.common.datatag.DataTagAddress;
import cern.c2mon.shared.common.datatag.address.impl.SimpleHardwareAddressImpl;

/**
 * @author Justin Lewis Salmon
 */
@Configuration
public class ConfigurationInitializer {

  @Bean
  public InitializingBean autoConfigureTags(ApplicationContext context) {
    return () -> configureTags(context);
  }

  private void configureTags(ApplicationContext context) throws UnknownHostException {
    ConfigurationService configurationService = context.getBean(ConfigurationService.class);
    DaqProperties properties = context.getBean(DaqProperties.class);

    String hostName = InetAddress.getLocalHost().getHostName();
    String processName = properties.getName();
    System.setProperty("c2mon.daq.hostname", hostName);

    configurationService.removeProcess(processName);

    ConfigurationReport report = configurationService.createProcess(processName);
    if (report.getStatusDescription().contains("already exists")) {
      return;
    }

    configurationService.createEquipment(processName, hostName, HostMetricsMessageHandler.class.getName());

    configurationService.createDataTag(hostName, DataTag.create(hostName + "/mem.avail", Long.class, new DataTagAddress())
        .description("Available memory")
        .unit("bytes")
        .addMetadata("location", 104)
        .addMetadata("responsible", "Joe Bloggs").build());

    configurationService.createDataTag(hostName, DataTag.create(hostName + "/mem.swap.used", Long.class, new DataTagAddress())
        .description("Amount of swap space used")
        .unit("bytes")
        .addMetadata("location", 104)
        .addMetadata("responsible", "Joe Bloggs").build());

    configurationService.createDataTag(hostName, DataTag.create(hostName + "/cpu.loadavg", Double.class, new DataTagAddress())
        .description("CPU load average for the last minute")
        .addMetadata("location", "864")
        .addMetadata("responsible", "John Doe").build());

    configurationService.createDataTag(hostName, DataTag.create(hostName + "/cpu.temp", Double.class, new DataTagAddress())
        .description("CPU temperature")
        .unit("°C")
        .addMetadata("location", "864")
        .addMetadata("responsible", "John Doe").build());

    configurationService.createDataTag(hostName, DataTag.create(hostName + "/cpu.voltage", Double.class, new DataTagAddress())
        .description("CPU voltage")
        .unit("Volts")
        .addMetadata("location", "864")
        .addMetadata("responsible", "John Doe").build());

    configurationService.createDataTag(hostName, DataTag.create(hostName + "/os.numprocs", Integer.class, new DataTagAddress())
        .description("Number of running processes")
        .addMetadata("location", "513")
        .addMetadata("responsible", "Sue West").build());

    configurationService.createAlarm(hostName + "/os.numprocs",
        Alarm.create("/os.nump", "high", 1, new RangeCondition(Integer.class, 1, 1000))
            .addMetadata("causes", "The CPU load is too high")
            .addMetadata("more", "some details").build());

    configurationService.createDataTag(hostName, DataTag.create(hostName + "/os.numthreads", Integer.class, new DataTagAddress())
        .description("Number of running threads")
        .addMetadata("location", "513")
        .addMetadata("responsible", "Sue West").build());

    configurationService.createDataTag(hostName, DataTag.create(hostName + "/os.fds", Long.class, new DataTagAddress())
        .description("Number of open file descriptors")
        .addMetadata("location", "513")
        .addMetadata("1234", "4321")
        .addMetadata("responsible", "Sue West").build());

    configurationService.createCommandTag(hostName, CommandTag.create(hostName + "/ping", String.class, new SimpleHardwareAddressImpl("ping"),
        5000, 2000, 100, 0, "test", "test", "test")
        .build());
  }
}
