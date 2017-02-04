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
import cern.c2mon.shared.client.metadata.Metadata;
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

    String processName = properties.getName();
    String equipmentName = InetAddress.getLocalHost().getHostName();

    configurationService.removeProcess(processName);

    ConfigurationReport report = configurationService.createProcess(processName);
    report = configurationService.createEquipment(processName, equipmentName, HostMetricsMessageHandler.class.getName());

    configurationService.createDataTag(equipmentName, DataTag.create("mem.avail", Long.class, new DataTagAddress())
        .description("Available memory")
        .unit("bytes")
        .addMetadata("location", 104)
        .addMetadata("responsible", "Joe Bloggs").build());

    configurationService.createDataTag(equipmentName, DataTag.create("mem.swap.used", Long.class, new DataTagAddress())
        .description("Amount of swap space used")
        .unit("bytes")
        .addMetadata("location", 104)
        .addMetadata("responsible", "Joe Bloggs").build());

    configurationService.createDataTag(equipmentName, DataTag.create("cpu.loadavg", Double.class, new DataTagAddress())
        .description("CPU load average for the last minute")
        .addMetadata("location", "864")
        .addMetadata("responsible", "John Doe").build());

    configurationService.createAlarm("cpu.loadavg", Alarm.create("cpu.loadavg", "high", 1, new RangeCondition(Double.class, 5, 100))
        .addMetadata("causes", "The CPU load is too high").build());

    configurationService.createDataTag(equipmentName, DataTag.create("cpu.temp", Double.class, new DataTagAddress())
        .description("CPU temperature")
        .unit("°C")
        .addMetadata("location", "864")
        .addMetadata("responsible", "John Doe").build());

    configurationService.createDataTag(equipmentName, DataTag.create("cpu.voltage", Double.class, new DataTagAddress())
        .description("CPU voltage")
        .unit("Volts")
        .addMetadata("location", "864")
        .addMetadata("responsible", "John Doe").build());

    configurationService.createDataTag(equipmentName, DataTag.create("os.numprocs", Integer.class, new DataTagAddress())
        .description("Number of running processes")
        .addMetadata("location", "513")
        .addMetadata("responsible", "Sue West").build());

    configurationService.createDataTag(equipmentName, DataTag.create("os.numthreads", Integer.class, new DataTagAddress())
        .description("Number of running threads")
        .addMetadata("location", "513")
        .addMetadata("responsible", "Sue West").build());

    configurationService.createDataTag(equipmentName, DataTag.create("os.fds", Long.class, new DataTagAddress())
        .description("Number of open file descriptors")
        .addMetadata("location", "513")
        .addMetadata("responsible", "Sue West").build());

    configurationService.createCommandTag(equipmentName, CommandTag.create("ping", String.class, new SimpleHardwareAddressImpl("ping"),
        5000, 2000, 100, 0, "test", "test", "test")
        .build());
  }
}
