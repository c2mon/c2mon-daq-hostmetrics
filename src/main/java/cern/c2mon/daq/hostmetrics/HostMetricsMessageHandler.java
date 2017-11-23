package cern.c2mon.daq.hostmetrics;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import cern.c2mon.daq.common.EquipmentMessageHandler;
import cern.c2mon.daq.common.IEquipmentMessageSender;
import cern.c2mon.daq.tools.equipmentexceptions.EqIOException;
import cern.c2mon.shared.common.datatag.ValueUpdate;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Simple {@link EquipmentMessageHandler} implementation that uses the OSHI
 * library to publish metrics about the current host.
 *
 * @author Justin Lewis Salmon
 */
@Slf4j
public class HostMetricsMessageHandler extends EquipmentMessageHandler {

  float minX = 50.0f;
  float maxX = 100.0f;

  Random rand = new Random();

  @Override
  public void connectToDataSource() throws EqIOException {
    IEquipmentMessageSender sender = getEquipmentMessageSender();
    sender.confirmEquipmentStateOK();

    SystemInfo si = new SystemInfo();
    HardwareAbstractionLayer hal = si.getHardware();
    OperatingSystem os = si.getOperatingSystem();

    String hostName = System.getProperty("c2mon.daq.hostname");

    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
      try {
//        sender.update(hostName + "/mem.avail", new ValueUpdate(hal.getMemory().getAvailable()));
//        sender.update(hostName + "/mem.swap.used", new ValueUpdate(hal.getMemory().getSwapUsed()));
//        sender.update(hostName + "/cpu.loadavg", new ValueUpdate(hal.getProcessor().getSystemLoadAverage()));
        sender.update(hostName + "/cpu.temp", new ValueUpdate(hal.getSensors().getCpuTemperature()));
        IntStream.range(0, 10).forEach(i -> sender.update("tagName_" + i, new ValueUpdate(hal.getSensors().getCpuTemperature() - ThreadLocalRandom.current().nextInt(2, 20))));
        IntStream.range(10, 20).forEach(i -> sender.update("tagName_" + i, new ValueUpdate(hal.getSensors().getCpuTemperature() + ThreadLocalRandom.current().nextInt(3, 10))));
        IntStream.range(20, 30).forEach(i -> sender.update("tagName_" + i, new ValueUpdate(hal.getSensors().getCpuTemperature() + ThreadLocalRandom.current().nextInt(1, 3))));
        // sender.update(hostName + "/cpu.voltage", new ValueUpdate(hal.getSensors().getCpuVoltage()));
//        sender.update(hostName + "/os.numprocs", new ValueUpdate(os.getProcessCount()));
//        sender.update(hostName + "/os.numthreads", new ValueUpdate(os.getThreadCount()));
//        sender.update(hostName + "/os.fds", new ValueUpdate(os.getFileSystem().getOpenFileDescriptors()));
      } catch (Exception e) {
        log.error("Error sending tag update", e);
      }
    }, 0, 2, SECONDS);

//    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
//      IntStream.range(0, 10).forEach(i -> sender.update("tagName_" + i, new ValueUpdate(hal.getSensors().getCpuTemperature() - ThreadLocalRandom.current().nextInt(2, 20))));
//      IntStream.range(10, 20).forEach(i -> sender.update("tagName_" + i, new ValueUpdate(hal.getSensors().getCpuTemperature() + ThreadLocalRandom.current().nextInt(2, 10))));
//      IntStream.range(20, 30).forEach(i -> sender.update("tagName_" + i, new ValueUpdate(hal.getSensors().getCpuTemperature() + ThreadLocalRandom.current().nextInt(2, 5))));
//    }, 0, 2, SECONDS);

    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(sender::sendSupervisionAlive, 0, 30, SECONDS);
  }

  @Override
  public void disconnectFromDataSource() throws EqIOException {
  }

  @Override
  public void refreshAllDataTags() {
  }

  @Override
  public void refreshDataTag(long dataTagId) {
  }
}
