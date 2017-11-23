
package cern.c2mon.daq.common;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.stream.IntStream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.c2mon.client.core.service.ConfigurationService;
import cern.c2mon.daq.config.DaqProperties;
import cern.c2mon.daq.hostmetrics.HostMetricsMessageHandler;
import cern.c2mon.shared.client.configuration.ConfigurationReport;
import cern.c2mon.shared.client.configuration.api.alarm.Alarm;
import cern.c2mon.shared.client.configuration.api.alarm.RangeCondition;
import cern.c2mon.shared.client.configuration.api.tag.CommandTag;
import cern.c2mon.shared.client.configuration.api.tag.DataTag;
import cern.c2mon.shared.client.configuration.api.tag.RuleTag;
import cern.c2mon.shared.client.expression.DslExpression;
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

//    configurationService.removeProcess(processName);

//    ConfigurationReport report = configurationService.createProcess(processName);
//    if (report.getStatusDescription().contains("already exists")) {
//      return;
//    }
//
//    configurationService.createEquipment(processName, hostName, HostMetricsMessageHandler.class.getName());
//
//    configurationService.createDataTag(hostName, DataTag.create(hostName + "/mem.avail", Long.class, new DataTagAddress())
//        .description("Available memory")
//        .unit("bytes")
//        .addMetadata("location", "104/1")
//        .addMetadata("responsible", "Joe Bloggs").build());
//
//    configurationService.createDataTag(hostName, DataTag.create(hostName + "/mem.swap.used", Long.class, new DataTagAddress())
//        .description("Amount of swap space used")
//        .unit("bytes")
//        .addMetadata("location", "104/1")
//        .addMetadata("responsible", "Joe Bloggs").build());
//
//    configurationService.createDataTag(hostName, DataTag.create(hostName + "/cpu.loadavg", Double.class, new DataTagAddress())
//        .description("CPU load average for the last minute")
//        .addMetadata("location", "864/1")
//        .addMetadata("responsible", "John Doe").build());
//
//    configurationService.createAlarm(hostName + "/cpu.loadavg", Alarm.create(hostName + "/cpu.loadavg", "high", 1, new RangeCondition(Double.class, 5, 100))
//            .addMetadata("causes", "The CPU load is too high").build());
//
//
//    configurationService.createDataTag(hostName, DataTag.create(hostName + "/os.numprocs", Integer.class, new DataTagAddress())
//        .description("Number of running processes")
//        .addMetadata("location", "513/1")
//        .addMetadata("responsible", "John Doe").build());
//
//    configurationService.createDataTag(hostName, DataTag.create(hostName + "/os.numthreads", Integer.class, new DataTagAddress())
//        .description("Number of running threads")
//        .addMetadata("location", "213/1")
//        .addMetadata("responsible", "Sue West").build());
//
//    configurationService.createDataTag(hostName, DataTag.create(hostName + "/os.fds", Long.class, new DataTagAddress())
//        .description("Number of open file descriptors")
//        .addMetadata("location", "213/1")
//        .addMetadata("responsible", "Sue West").build());
//
//    configurationService.createCommandTag(hostName, CommandTag.create(hostName + "/ping", String.class, new SimpleHardwareAddressImpl("ping"),
//        5000, 2000, 100, 0, "test", "test", "test")
//        .build());
//
//    configurationService.createDataTag(hostName, DataTag.create(hostName + "/cpu.temp", Double.class, new DataTagAddress())
//        .description("CPU temperature")
//        .unit("°C")
//        .addMetadata("location", "864/1")
//        .addMetadata("responsible", "John Doe").build());
////
//
//    IntStream.range(0, 10).forEach(i -> configurationService.createDataTag(hostName, DataTag.create("tagName_" + i, Float.class, new DataTagAddress())
//        .description("System temperature")
//        .unit("bytes")
//        .addMetadata("location", "513/1")
//        .addMetadata("rackrow", "10")
//        .addMetadata("responsible", "Tiffany Pieters").build()))
//
//    IntStream.range(10, 20).forEach(i -> configurationService.createDataTag(hostName, DataTag.create("tagName_" + i, Float.class, new DataTagAddress())
//        .description("System temperature")
//        .unit("bytes")
//        .addMetadata("location", "513/1")
//        .addMetadata("rackrow", "20")
//        .addMetadata("responsible", "Tiffany Pieters").build()));
//
//    IntStream.range(20, 30).forEach(i -> configurationService.createDataTag(hostName, DataTag.create("tagName_" + i, Float.class, new DataTagAddress())
//        .description("System temperature")
//        .unit("bytes")
//        .addMetadata("location", "513/1")
//        .addMetadata("rackrow", "30")
//        .addMetadata("responsible", "Jeff Bridges").build()));
//
//
//    configurationService.createExpression(DslExpression.create("Responsible_Tiffany_Pieters", "Average of all sensors from Tiffany Pieters.",
//        Float.class, "avg(q(name:'*', responsible:'Tiffany Pieters', '10m'))").build());
//
//    configurationService.createExpression(DslExpression.create("All_rack_rows", "Average of all rack rows. ",
//        Float.class,"avg(q(name:'*', expressionType:'rackrow', '10m'))").build());
//
//    configurationService.createExpression(DslExpression.create("Rack_row_10", "Average of all sensors in rack row 10 in the last 5m.",
//        Float.class, "avg(q(name:'*', rackrow:'10', '5m'))")
//        .addMetadata("expressionType", "rackrow").build());
//
//    configurationService.createExpression(DslExpression.create("Rack_row_20", "Average of all sensors in rack row 20 in the last 5m.",
//        Float.class, "avg(q(name:'*', rackrow:'20', '5m'))")
//        .addMetadata("expressionType", "rackrow").build());
//
//    configurationService.createExpression(DslExpression.create("Rack_row_30", "Average of all sensors in rack row 30 in the last 5m.",
//        Float.class, "avg(q(name:'*', rackrow:'30', '5m'))")
//        .addMetadata("expressionType", "rackrow").build());
//
//    configurationService.createExpression(DslExpression.create("Location_513/1", "Average of all sensors at location:513/1.",
//        Float.class, "avg(q(name:'*', location:'513/1', '1h'))").build());
//
//    configurationService.createRule(RuleTag.create("this is the name of the rule 1", Boolean.class, "(#1000038 > 35)[true],true[false])").build());

  }
}
