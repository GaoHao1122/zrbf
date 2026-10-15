@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0mysql-control.ps1" %*
