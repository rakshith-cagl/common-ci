# Brian Wilhite
# bcwilhite@live.com
# TechEd 2013 NA June 5th, 2013

$FolderPath = "C:\Program Files (x86)\Microsoft SDKs\UAP\v0.8.0.0\ExtensionSDKs\SQLite.UAP.2015"
$FilePath   = ".\SQLiteWinRT.vcxproj"
$VersionNumber = Get-ChildItem -Path $FolderPath | Where-Object {$_.Extension -ne ".deleteme"} | Select-Object -ExpandProperty Name
$FileContents = Get-Content -Path $FilePath
$FileContents = $FileContents | ForEach-Object {$_ -replace '<SDKReference Include="SQLite.UAP.2015, Version=(\d.?)+"', "<SDKReference Include=`"SQLite.UAP.2015, Version=$VersionNumber`""}
$FileContents | Out-File $FilePath -Encoding ascii -Force