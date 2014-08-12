[![Build Status](https://travis-ci.org/PaulMB/media-adapter.svg?branch=master)](https://travis-ci.org/PaulMB/media-adapter)

Media Adapter
==============

Purpose
--------------

Tool to interact with media containers, typically MKV files. In particular, it may be used add tracks like subtitles to an existing container. Includes a package for Synology with a dedicated UI to modify files stored on the NAS.


Requirements
--------------

* The version 5 of DiskStation Manager must be installed for the Synology part.

* A Java 7 installation is mandatory. For Synology, you can get it from [MissileHugger](http://packages.missilehugger.com) or [PCLoadLetter](http://packages.pcloadletter.co.uk/). Make sure `JAVA_HOME` is defined in the environment, for example in the `/etc/profile`.

* The `mkvmerge` binary must be available. For Synology, you can install it with `ipkg install mkvtoolnix`.


License
--------------

Apache license 2.0
