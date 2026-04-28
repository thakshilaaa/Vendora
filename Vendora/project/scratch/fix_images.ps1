$templatePath = "d:\SLIIT University\Year 2 - Semester 2\IE2091 - IS Project\Assignment\Vendora\project\src\main\resources\templates"
$htmlFiles = Get-ChildItem -Path $templatePath -Filter *.html -Recurse

foreach ($file in $htmlFiles) {
    $content = Get-Content $file.FullName -Raw
    
    if ($content -match "url\('/images/'\)") {
        # Default to b2.png for most (forms/auth)
        $newImg = "b2.png"
        
        # Dashboard specific
        if ($file.Name -match "dashboard") { $newImg = "b1.jpg" }
        # Admin specific
        if ($file.FullName -match "admin") { $newImg = "b3.webp" }
        
        $content = $content -replace "url\('/images/'\)", "url('/images/$newImg')"
        Set-Content -Path $file.FullName -Value $content -NoNewline
        Write-Host "Fixed Image: $($file.FullName) with $newImg"
    }
}
