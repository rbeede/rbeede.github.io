#!/bin/bash

# For Ubuntu/Debian based that use apt will install all updates, autoremove, cleanup
# Reboots only if necessary

LOGFILEPATH=/var/log/UPDATE-JOB.log


# Redirect STDOUT as $LOG_FILE
exec 1>$LOGFILEPATH

# Redirect STDERR to STDOUT
exec 2>&1


# Don't assume this is set
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin


/usr/bin/apt-get --quiet --assume-yes update;

/usr/bin/apt-get --quiet --assume-yes dist-upgrade;

/usr/bin/apt-get --quiet --assume-yes autoremove;

/usr/bin/apt-get --quiet --assume-yes autoclean;

/usr/bin/apt-get --quiet --assume-yes clean;


if [ -f /var/run/reboot-required ]; then
        /sbin/reboot;
fi;
