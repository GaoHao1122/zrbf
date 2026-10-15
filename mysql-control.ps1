[CmdletBinding()]
param(
    [ValidateSet('start', 'stop', 'restart', 'status')]
    [string]$Action,

    [string]$Password
)

$ErrorActionPreference = 'Stop'

$MysqlHome = 'D:\Software\mysql-8.0.43-winx64'
$Mysqld = Join-Path $MysqlHome 'bin\mysqld.exe'
$Mysqladmin = Join-Path $MysqlHome 'bin\mysqladmin.exe'
$DefaultsFile = Join-Path $MysqlHome 'my.ini'
$Port = 3306
$User = 'root'

function Test-MysqlRunning {
    $client = New-Object System.Net.Sockets.TcpClient
    try {
        $client.Connect('127.0.0.1', $Port)
        return $client.Connected
    } catch {
        return $false
    } finally {
        $client.Close()
    }
}

function Wait-MysqlState {
    param(
        [bool]$Running,
        [int]$TimeoutSeconds = 20
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    do {
        if ((Test-MysqlRunning) -eq $Running) {
            return $true
        }
        Start-Sleep -Milliseconds 500
    } while ((Get-Date) -lt $deadline)

    return $false
}

function Start-Mysql {
    if (Test-MysqlRunning) {
        Write-Host "MySQL is already running on port $Port."
        return
    }

    if (-not (Test-Path -LiteralPath $Mysqld)) {
        throw "mysqld.exe was not found: $Mysqld"
    }
    if (-not (Test-Path -LiteralPath $DefaultsFile)) {
        throw "MySQL config was not found: $DefaultsFile"
    }

    Start-Process `
        -FilePath $Mysqld `
        -ArgumentList "--defaults-file=$DefaultsFile" `
        -WorkingDirectory $MysqlHome `
        -WindowStyle Hidden | Out-Null

    if (-not (Wait-MysqlState -Running $true)) {
        throw "MySQL did not start within 20 seconds."
    }

    Write-Host "MySQL started on port $Port."
}

function Stop-Mysql {
    if (-not (Test-MysqlRunning)) {
        Write-Host "MySQL is already stopped."
        return
    }

    if (-not (Test-Path -LiteralPath $Mysqladmin)) {
        throw "mysqladmin.exe was not found: $Mysqladmin"
    }

    $secret = $null
    $pointer = [IntPtr]::Zero
    try {
        if ($Password) {
            $secret = $Password
        } else {
            $securePassword = Read-Host "MySQL password for $User" -AsSecureString
            $pointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
            $secret = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer)
        }

        $env:MYSQL_PWD = $secret
        & $Mysqladmin `
            --protocol=tcp `
            --host=127.0.0.1 `
            "--port=$Port" `
            "--user=$User" `
            shutdown

        if ($LASTEXITCODE -ne 0) {
            throw "mysqladmin shutdown failed with exit code $LASTEXITCODE."
        }
    } finally {
        Remove-Item Env:MYSQL_PWD -ErrorAction SilentlyContinue
        if ($pointer -ne [IntPtr]::Zero) {
            [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer)
        }
        $secret = $null
    }

    if (-not (Wait-MysqlState -Running $false)) {
        throw "MySQL did not stop within 20 seconds."
    }

    Write-Host "MySQL stopped."
}

function Show-MysqlStatus {
    $process = Get-Process -Name mysqld -ErrorAction SilentlyContinue |
        Where-Object { $_.Path -eq $Mysqld } |
        Select-Object -First 1

    if (Test-MysqlRunning) {
        $pidText = if ($process) { " (PID $($process.Id))" } else { "" }
        Write-Host "MySQL is running on port $Port$pidText."
    } else {
        Write-Host "MySQL is stopped."
    }
}

$Interactive = -not $PSBoundParameters.ContainsKey('Action')

if ($Interactive) {
    Write-Host "MySQL control"
    Write-Host "1. Start"
    Write-Host "2. Stop"
    Write-Host "3. Restart"
    Write-Host "4. Status"

    switch (Read-Host "Select an action") {
        '1' { $Action = 'start' }
        '2' { $Action = 'stop' }
        '3' { $Action = 'restart' }
        '4' { $Action = 'status' }
        default { throw "Invalid action." }
    }
}

switch ($Action) {
    'start' { Start-Mysql }
    'stop' { Stop-Mysql }
    'restart' {
        Stop-Mysql
        Start-Mysql
    }
    'status' { Show-MysqlStatus }
}

if ($Interactive) {
    Read-Host "Press Enter to close"
}
