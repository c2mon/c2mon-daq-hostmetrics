# c2mon-daq-hostmetrics
[![build status](https://gitlab.cern.ch/c2mon/c2mon-daq-hostmetrics/badges/master/build.svg)](https://gitlab.cern.ch/c2mon/c2mon-daq-hostmetrics/commits/master)

A simple, exemplary C2MON DAQ module for publishing metrics about the current
host using the OSHI library.

## Documentation
See the current [reference docs][].

## Issue tracking
Please report issues on GitLab via the [issue tracker][].

## Release notes
Please have a look into the [CHANGELOG.md][] file.

## Milestone planning
[Find here][] the complete list of all existing version milestones.

## Building from Source
C2MON uses a [Maven][]-based build system.

### Prerequisites

[Git][] and [JDK 8 update 20 or later][JDK8 build]

Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8.0` folder
extracted from the JDK download.

### Check out sources
`git clone git@github.com:c2mon/c2mon-daq-hostmetrics.git`

### Compile and test; build jar and distribution tarball
`mvn package -DskipDockerBuild -DskipDockerTag`

## Startup
Make sure that your C2MON server is running before launching the Hostmetrics DAQ. 
In the example commands below we assume that C2MON is running on `localhost`.

### Running from tarball (Linux/Unix only)
Once you have built your distribution tarball, extract it and run the following command:
```shell
${HOSTMETRICS_HOME}/bin/daqprocess.sh start P_HOST01
```

### Running from Docker image
```shell
docker run --rm --name daq-hostmetrics -it --net=host -e "C2MON_PORT_61616_TCP=tcp://localhost:61616" \
  gitlab-registry.cern.ch/c2mon/c2mon-daq-hostmetrics bin/C2MON-DAQ-STARTUP.jvm -f P_HOST01
```

## License
C2MON is released under the [GNU LGPLv3 License][].


[reference docs]: http://c2mon.web.cern.ch/c2mon/docs/latest/getting-started/#running-the-hostmetrics-daq-tarball-distribution
[issue tracker]: https://gitlab.cern.ch/c2mon/c2mon-daq-hostmetrics/issues
[CHANGELOG.md]: /CHANGELOG.md
[Find here]: https://gitlab.cern.ch/c2mon/c2mon-daq-hostmetrics/milestones?state=all
[Maven]: http://maven.apache.org
[Git]: http://help.github.com/set-up-git-redirect
[JDK8 build]: http://www.oracle.com/technetwork/java/javase/downloads
[Pull requests]: http://help.github.com/send-pull-requests
[contributor guidelines]: /CONTRIBUTING.md
[GNU LGPLv3 License]: /LICENSE
