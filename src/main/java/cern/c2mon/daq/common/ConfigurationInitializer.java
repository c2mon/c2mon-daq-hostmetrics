package cern.c2mon.daq.common;

import cern.c2mon.client.core.ConfigurationService;
import cern.c2mon.daq.common.conf.core.ConfigurationController;
import cern.c2mon.daq.hostmetrics.HostMetricsMessageHandler;
import cern.c2mon.shared.client.configuration.api.equipment.Equipment;
import cern.c2mon.shared.client.configuration.api.tag.DataTag;
import cern.c2mon.shared.common.datatag.DataTagAddress;
import cern.c2mon.shared.common.metadata.Metadata;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Justin Lewis Salmon
 */
@Configuration
public class ConfigurationInitializer {

  @Bean
  public InitializingBean autoConfigureTags(ApplicationContext context) {
    return () -> configureTags(context);
  }

  private void configureTags(ApplicationContext context) {
    ConfigurationService configurationService = context.getBean(ConfigurationService.class);

    configurationService.createProcess("P_HOST01");
    configurationService.createEquipment("P_HOST01", "E_HOST01", HostMetricsMessageHandler.class.getName());

    configurationService.updateEquipment(Equipment.update("E_HOST01").handlerClass(HostMetricsMessageHandler.class.getName()).build());

    configurationService.createDataTag("E_HOST01", DataTag.create("mem.avail", Long.class, new DataTagAddress())
        .description("Available memory")
        .unit("bytes")
        .metadata(Metadata.builder()
            .addMetadata("location", "104")
            .addMetadata("responsible", "Joe Bloggs").build()).build());

    configurationService.createDataTag("E_HOST01", DataTag.create("mem.swap.used", Long.class, new DataTagAddress())
        .description("Amount of swap space used")
        .unit("bytes")
        .metadata(Metadata.builder()
            .addMetadata("location", "104")
            .addMetadata("responsible", "Joe Bloggs").build()).build());

    configurationService.createDataTag("E_HOST01", DataTag.create("cpu.loadavg", Double.class, new DataTagAddress())
        .description("CPU load average for the last minute")
        .metadata(Metadata.builder()
            .addMetadata("location", "864")
            .addMetadata("responsible", "John Doe").build()).build());

    configurationService.createDataTag("E_HOST01", DataTag.create("cpu.temp", Double.class, new DataTagAddress())
        .description("CPU temperature")
        .unit("°C")
        .metadata(Metadata.builder()
            .addMetadata("location", "864")
            .addMetadata("responsible", "John Doe").build()).build());

    configurationService.createDataTag("E_HOST01", DataTag.create("cpu.voltage", Double.class, new DataTagAddress())
        .description("CPU voltage")
        .unit("Volts")
        .metadata(Metadata.builder()
            .addMetadata("location", "864")
            .addMetadata("responsible", "John Doe").build()).build());

    configurationService.createDataTag("E_HOST01", DataTag.create("os.numprocs", Integer.class, new DataTagAddress())
        .description("Number of running processes")
        .metadata(Metadata.builder()
            .addMetadata("location", "513")
            .addMetadata("responsible", "Sue West").build()).build());

    configurationService.createDataTag("E_HOST01", DataTag.create("os.numthreads", Integer.class, new DataTagAddress())
        .description("Number of running threads")
        .metadata(Metadata.builder()
            .addMetadata("location", "513")
            .addMetadata("responsible", "Sue West").build()).build());

    configurationService.createDataTag("E_HOST01", DataTag.create("os.fds", Long.class, new DataTagAddress())
        .description("Number of open file descriptors")
        .metadata(Metadata.builder()
            .addMetadata("location", "513")
            .addMetadata("responsible", "Sue West").build()).build());
  }
}
