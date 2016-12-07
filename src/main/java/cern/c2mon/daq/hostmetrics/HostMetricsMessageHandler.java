package cern.c2mon.daq.hostmetrics;

import cern.c2mon.client.core.ConfigurationService;
import cern.c2mon.client.core.TagService;
import cern.c2mon.daq.common.EquipmentMessageHandler;
import cern.c2mon.daq.common.IEquipmentMessageSender;
import cern.c2mon.daq.tools.equipmentexceptions.EqIOException;
import cern.c2mon.shared.client.configuration.api.tag.DataTag;
import cern.c2mon.shared.client.tag.TagMode;
import cern.c2mon.shared.common.datatag.DataTagAddress;
import cern.c2mon.shared.common.datatag.ValueUpdate;
import cern.c2mon.shared.common.metadata.Metadata;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Simple {@link EquipmentMessageHandler} implementation that uses the OSHI
 * library to publish metrics about the current host.
 *
 * @author Justin Lewis Salmon
 */
@Slf4j
public class HostMetricsMessageHandler extends EquipmentMessageHandler {

  @Override
  public void connectToDataSource() throws EqIOException {
    IEquipmentMessageSender sender = getEquipmentMessageSender();
    sender.confirmEquipmentStateOK();

    configureTags();

    SystemInfo si = new SystemInfo();
    HardwareAbstractionLayer hal = si.getHardware();
    OperatingSystem os = si.getOperatingSystem();

    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
      try {
        sender.update("mem.avail", new ValueUpdate(hal.getMemory().getAvailable()));
        sender.update("mem.swap.used", new ValueUpdate(hal.getMemory().getSwapUsed()));
        sender.update("cpu.loadavg", new ValueUpdate(hal.getProcessor().getSystemLoadAverage()));
        sender.update("cpu.temp", new ValueUpdate(hal.getSensors().getCpuTemperature()));
        sender.update("cpu.voltage", new ValueUpdate(hal.getSensors().getCpuVoltage()));
        sender.update("os.numprocs", new ValueUpdate(os.getProcessCount()));
        sender.update("os.numthreads", new ValueUpdate(os.getThreadCount()));
        sender.update("os.fds", new ValueUpdate(os.getFileSystem().getOpenFileDescriptors()));
        sender.update("os.fds.max", new ValueUpdate(os.getFileSystem().getMaxFileDescriptors()));
      } catch (Exception e) {
        log.error("Error sending tag update", e);
      }
    }, 0, 1, SECONDS);

    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(sender::sendSupervisionAlive, 0, 30, SECONDS);
  }

  private void configureTags() {
    ConfigurationService configurationService = this.getContext().getBean(ConfigurationService.class);

    configurationService.createProcess("P_HOST01");
    configurationService.createEquipment("P_HOST01", "E_HOST01", this.getClass().getName());

    configurationService.createDataTag("E_HOST01", DataTag.create("mem.avail", Long.class, new DataTagAddress())
        .description("Available memory")
        .unit("bytes")
        .addMetadata("location", "104")
        .addMetadata("responsible", "Joe Bloggs").build());

    configurationService.createDataTag("E_HOST01", DataTag.create("mem.swap.used", Long.class, new DataTagAddress())
        .description("Amount of swap space used")
        .unit("bytes")
        .addMetadata("location", "104")
        .addMetadata("responsible", "Joe Bloggs").build());

    configurationService.createDataTag("E_HOST01", DataTag.create("cpu.loadavg", Double.class, new DataTagAddress())
        .description("CPU load average for the last minute")
        .addMetadata("location", "864")
        .addMetadata("responsible", "John Doe").build());

    configurationService.createDataTag("E_HOST01", DataTag.create("cpu.temp", Double.class, new DataTagAddress())
        .description("CPU temperature")
        .unit("°C")
        .addMetadata("location", "864")
        .addMetadata("responsible", "John Doe").build());

    configurationService.createDataTag("E_HOST01", DataTag.create("cpu.voltage", Double.class, new DataTagAddress())
        .description("CPU voltage")
        .unit("Volts")
        .addMetadata("location", "864")
        .addMetadata("responsible", "John Doe").build());

    configurationService.createDataTag("E_HOST01", DataTag.create("os.numprocs", Integer.class, new DataTagAddress())
        .description("Number of running processes")
        .addMetadata("location", "513")
        .addMetadata("responsible", "Sue West").build());

    configurationService.createDataTag("E_HOST01", DataTag.create("os.numthreads", Integer.class, new DataTagAddress())
        .description("Number of running threads")
        .addMetadata("location", "513")
        .addMetadata("responsible", "Sue West").build());

    configurationService.createDataTag("E_HOST01", DataTag.create("os.fds", Long.class, new DataTagAddress())
        .description("Number of open file descriptors")
        .addMetadata("location", "513")
        .addMetadata("responsible", "Sue West").build());
  }

  @Override
  public void disconnectFromDataSource() throws EqIOException {}

  @Override
  public void refreshAllDataTags() {}

  @Override
  public void refreshDataTag(long dataTagId) {}
}
