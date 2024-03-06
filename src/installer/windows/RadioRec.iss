#define MyAppName "RadioRec"
#define MyAppVersion "1.0"
#define MyAppPublisher "Marelis Adlatus"
#define MyAppURL "https://radiorec.marelis.cz"
#define MyAppExeName "RadioRec.exe"

[Setup]
AppId={{8D30C4E4-C125-47D2-A48D-8F09E5B12FFC}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
;ArchitecturesInstallIn64BitMode=x64
DefaultDirName={autopf}\Marelis\{#MyAppName}
DisableProgramGroupPage=yes
ChangesAssociations=yes
LicenseFile=addons\License.txt
; Remove the following line to run in administrative install mode (install for all users.)
PrivilegesRequired=lowest
PrivilegesRequiredOverridesAllowed=dialog
OutputDir=.
OutputBaseFilename={#MyAppName}-{#MyAppVersion}-install
SetupIconFile=icons\RadioRec.ico
UninstallDisplayIcon="{app}\{#MyAppExeName}"
Compression=lzma
SolidCompression=yes
WizardStyle=modern

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "czech"; MessagesFile: "compiler:Languages\Czech.isl"
Name: "polish"; MessagesFile: "compiler:Languages\Polish.isl"
Name: "russian"; MessagesFile: "compiler:Languages\Russian.isl"
Name: "slovak"; MessagesFile: "compiler:Languages\Slovak.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked
Name: "startup"; Description: "{cm:AutoStartProgram,{#MyAppName}}"

[Files]
Source: "RadioRec\{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "RadioRec\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{autoprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent

[Registry]
Root: HKA; Subkey: "Software\Classes\.radiorec-station\OpenWithProgids"; ValueType: string; ValueName: "Marelis.RadioRec.Station"; ValueData: ""; Flags: uninsdeletevalue
Root: HKA; Subkey: "Software\Classes\Marelis.RadioRec.Station"; ValueType: string; ValueName: ""; ValueData: "Marelis RadioRec Station"; Flags: uninsdeletekey
Root: HKA; Subkey: "Software\Classes\Marelis.RadioRec.Station\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\Station.ico"
Root: HKA; Subkey: "Software\Classes\Marelis.RadioRec.Station\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\{#MyAppExeName}"" ""%1"""
Root: HKA; Subkey: "Software\Classes\Applications\RadioRec.exe\SupportedTypes"; ValueType: string; ValueName: ".radiorec-station"; ValueData: ""
Root: HKCU; Subkey: "Software\Microsoft\Windows\CurrentVersion\Run"; ValueType: string; ValueName: "{#MyAppName}"; ValueData: """{app}\{#MyAppExeName}"""; Flags: uninsdeletevalue; Tasks: startup
