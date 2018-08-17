@ECHO OFF
SETLOCAL

REM	Author:  Rodney Beede <contactme -AT- rodneybeede -DOT- com>
REM	First Version:  2008-12-19
REM	License:  Public Domain


REM	You may need to change ISO_EXTRACT_PROGRAM and ISO_EXTRACT_PROGRAM_OPTS to
REM	match any different programs you wish to use
REM	They are set below at the ***** CHANGEABLE OPTIONS ***** section



ECHO This script allows you to extract the contents of the System Rescue CD ISO
ECHO onto a disk drive of your choice.
ECHO.
ECHO You must run this script as an Administrator
ECHO.
ECHO The ISO should be located in the same directory as this script
ECHO.
ECHO The usb drive must already have a FAT or FAT32 filesystem
ECHO.
ECHO Tested to work with Windows XP SP2+ and Windows Vista SP1+ with version
ECHO	1.1.3 of System Rescue CD
ECHO.
ECHO.


SET /P TARGET_DRIVE=Enter in the drive letter of the usb key:  


IF "" EQU "%TARGET_DRIVE%" (
	ECHO You MUST enter a drive letter
	EXIT /B
	)



REM	***** CHANGEABLE OPTIONS *****

REM	What program to run to extract files from the ISO (no "")
SET ISO_EXTRACT_PROGRAM=C:\Program Files\7-Zip\7z.exe

REM	Options to append after the ISO_EXTRACT_PROGRAM command
SET ISO_EXTRACT_PROGRAM_OPTS=x -o%TARGET_DRIVE%:\ -r

REM	***** END CHANGEABLE OPTIONS *****



REM	Determine the ISO to use
FOR %%i IN (%~dp0systemrescuecd-*.iso) DO SET ISO_FILE=%%i

IF "" EQU "%ISO_FILE%" (
	ECHO Could not find ISO in %~dp0
	EXIT /B
) ELSE (
	ECHO Using %ISO_FILE%
	ECHO.
)



"%ISO_EXTRACT_PROGRAM%" %ISO_EXTRACT_PROGRAM_OPTS% "%ISO_FILE%" *



REM	Prepare SysLinux 
FOR %%i IN (%TARGET_DRIVE%:\syslinux\syslinux-*.zip) DO SET SYSLINUX_ZIP=%%i

"%ISO_EXTRACT_PROGRAM%" %ISO_EXTRACT_PROGRAM_OPTS% "%SYSLINUX_ZIP%" win32\syslinux.exe

MOVE %TARGET_DRIVE%:\win32\syslinux.exe %TARGET_DRIVE%:\syslinux.exe

RMDIR /S /Q %TARGET_DRIVE%:\win32



REM	Move the SysLinux items into place

MOVE %TARGET_DRIVE%:\isolinux\isolinux.cfg %TARGET_DRIVE%:\isolinux\syslinux.cfg

RMDIR /S /Q %TARGET_DRIVE%:\syslinux

MOVE %TARGET_DRIVE%:\isolinux %TARGET_DRIVE%:\syslinux



REM	Make usb drive bootable (writes mbr)

%TARGET_DRIVE%:\syslinux.exe -maf %TARGET_DRIVE%:


ECHO ALL DONE