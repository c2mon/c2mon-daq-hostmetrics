package cern.c2mon.daq.hostmetrics;

import cern.c2mon.daq.common.EquipmentMessageHandler;
import cern.c2mon.daq.common.IEquipmentMessageSender;
import cern.c2mon.daq.tools.equipmentexceptions.EqIOException;
import cern.c2mon.shared.common.datatag.ValueUpdate;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.util.concurrent.Executors;

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
      } catch (Exception e) {
        log.error("Error sending tag update", e);
      }
    }, 0, 1, SECONDS);

    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
      sender.sendSupervisionAlive();
    }, 0, 30, SECONDS);
  }

  @Override
  public void disconnectFromDataSource() throws EqIOException {}

  @Override
  public void refreshAllDataTags() {}

  @Override
  public void refreshDataTag(long dataTagId) {}
}
