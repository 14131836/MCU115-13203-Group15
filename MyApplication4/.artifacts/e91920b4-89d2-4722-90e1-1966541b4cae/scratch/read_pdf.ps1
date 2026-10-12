$path = "C:\Users\ASUS\Downloads\Fast Food Ordering Wizard - Fragment.pdf"
$bytes = [System.IO.File]::ReadAllBytes($path)
$str = [System.Text.Encoding]::ASCII.GetString($bytes)
$matches = [regex]::Matches($str, '\(([^\)]+)\)')
foreach ($m in $matches) {
    $m.Groups[1].Value
}
