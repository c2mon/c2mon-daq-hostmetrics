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
package cern.c2mon.daq.hostmetrics;

import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import cern.c2mon.daq.common.EquipmentMessageHandler;
import cern.c2mon.daq.common.IEquipmentMessageSender;
import cern.c2mon.daq.common.MetricNames;
import cern.c2mon.daq.tools.equipmentexceptions.EqIOException;
import cern.c2mon.shared.common.datatag.ValueUpdate;

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

    MetricNames metrics = MetricNames.getInstance();

    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
      try {
        sender.update(metrics.getAvailableMemory(), new ValueUpdate(hal.getMemory().getAvailable()));
        sender.update(metrics.getSwapUsed(), new ValueUpdate(hal.getMemory().getSwapUsed()));
        sender.update(metrics.getSystemLoadAverage(), new ValueUpdate(hal.getProcessor().getSystemLoadAverage() * 100));
        sender.update(metrics.getCpuTemperature(), new ValueUpdate(hal.getSensors().getCpuTemperature()));
        sender.update(metrics.getCpuVoltage(), new ValueUpdate(hal.getSensors().getCpuVoltage()));
        sender.update(metrics.getProcessCount(), new ValueUpdate(os.getProcessCount()));
        sender.update(metrics.getThreadCount(), new ValueUpdate(os.getThreadCount()));
        sender.update(metrics.getOpenFileDescriptors(), new ValueUpdate(os.getFileSystem().getOpenFileDescriptors()));

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
