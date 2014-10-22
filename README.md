[![Build Status](https://travis-ci.org/PaulMB/media-adapter.svg?branch=master)](https://travis-ci.org/PaulMB/media-adapter)

Media Adapter
==============

Purpose
--------------

Tool to interact with media containers, typically MKV files. In particular, it may be used add or remove tracks to an existing container. Includes Synology package with a dedicated UI to modify files stored on the NAS.


Requirements
--------------

* A Java 7 installation is mandatory (on Synology, you can get it from [MissileHugger](http://packages.missilehugger.com) or [PCLoadLetter](http://packages.pcloadletter.co.uk/). Make sure `JAVA_HOME` is defined in the environment, for example in the `/etc/profile`)

* Two connectors are available 
    * `mkvmerge` (on Synology, you can install the `mkvmerge` binary with `ipkg install mkvtoolnix`)
    * `ffmpeg` which is used by default (on Synology, you can get it from [Cytec](http://cytec.us/spk/))

* The version 5 of DiskStation Manager is required if installed on a Synology .


Download
--------------

SPK files can be downloaded from [Bintray](https://bintray.com/paulmb/maven/media-adapter/view/files/org/media/MediaAdapter)   


License
--------------

Apache license 2.0
