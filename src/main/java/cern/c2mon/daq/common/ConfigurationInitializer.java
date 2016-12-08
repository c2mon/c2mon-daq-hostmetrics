package cern.c2mon.daq.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.c2mon.client.core.ConfigurationService;
import cern.c2mon.daq.config.DaqProperties;
import cern.c2mon.daq.hostmetrics.HostMetricsMessageHandler;
import cern.c2mon.shared.client.configuration.api.tag.DataTag;
import cern.c2mon.shared.common.datatag.DataTagAddress;
import cern.c2mon.shared.common.metadata.Metadata;

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

    configurationService.createProcess(processName);
    configurationService.createEquipment(processName, equipmentName, HostMetricsMessageHandler.class.getName());

    configurationService.createDataTag(equipmentName, DataTag.create("mem.avail", Long.class, new DataTagAddress())
        .description("Available memory")
        .unit("bytes")
        .metadata(Metadata.builder()
            .addMetadata("location", "104")
            .addMetadata("responsible", "Joe Bloggs").build()).build());

    configurationService.createDataTag(equipmentName, DataTag.create("mem.swap.used", Long.class, new DataTagAddress())
        .description("Amount of swap space used")
        .unit("bytes")
        .metadata(Metadata.builder()
            .addMetadata("location", "104")
            .addMetadata("responsible", "Joe Bloggs").build()).build());

    configurationService.createDataTag(equipmentName, DataTag.create("cpu.loadavg", Double.class, new DataTagAddress())
        .description("CPU load average for the last minute")
        .metadata(Metadata.builder()
            .addMetadata("location", "864")
            .addMetadata("responsible", "John Doe").build()).build());

    configurationService.createDataTag(equipmentName, DataTag.create("cpu.temp", Double.class, new DataTagAddress())
        .description("CPU temperature")
        .unit("°C")
        .metadata(Metadata.builder()
            .addMetadata("location", "864")
            .addMetadata("responsible", "John Doe").build()).build());

    configurationService.createDataTag(equipmentName, DataTag.create("cpu.voltage", Double.class, new DataTagAddress())
        .description("CPU voltage")
        .unit("Volts")
        .metadata(Metadata.builder()
            .addMetadata("location", "864")
            .addMetadata("responsible", "John Doe").build()).build());

    configurationService.createDataTag(equipmentName, DataTag.create("os.numprocs", Integer.class, new DataTagAddress())
        .description("Number of running processes")
        .metadata(Metadata.builder()
            .addMetadata("location", "513")
            .addMetadata("responsible", "Sue West").build()).build());

    configurationService.createDataTag(equipmentName, DataTag.create("os.numthreads", Integer.class, new DataTagAddress())
        .description("Number of running threads")
        .metadata(Metadata.builder()
            .addMetadata("location", "513")
            .addMetadata("responsible", "Sue West").build()).build());

    configurationService.createDataTag(equipmentName, DataTag.create("os.fds", Long.class, new DataTagAddress())
        .description("Number of open file descriptors")
        .metadata(Metadata.builder()
            .addMetadata("location", "513")
            .addMetadata("responsible", "Sue West").build()).build());
  }
}
