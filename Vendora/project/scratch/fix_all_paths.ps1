$templatePath = "d:\SLIIT University\Year 2 - Semester 2\IE2091 - IS Project\Assignment\Vendora\project\src\main\resources\templates"
$htmlFiles = Get-ChildItem -Path $templatePath -Filter *.html -Recurse

foreach ($file in $htmlFiles) {
    $content = Get-Content $file.FullName -Raw
    
    # Replace CSS paths
    $content = $content -replace 'href=".*static/css/(.*?\.css)"', 'th:href="@{/css/$1}"'
    
    # Replace JS paths
    $content = $content -replace 'src=".*static/js/(.*?\.js)"', 'th:src="@{/js/$1}"'
    
    # Replace Background Image paths in style tags
    $content = $content -replace "url\('.*static/images/(.*?)'\)", "url('/images/$1')"
    $content = $content -replace 'url\(".*static/images/(.*?)"\)', "url('/images/$1')"
    
    # Replace Image src paths
    $content = $content -replace 'src=".*static/images/(.*?)"', 'th:src="@{/images/$1}"'
    
    # Replace internal links that still point to .html files
    # Only if they start with ../ or ./ and end with .html
    # This is risky but helps with broken nav
    # $content = $content -replace 'href="\.\./(.*?)\.html"', 'th:href="@{/$1}"'
    
    Set-Content -Path $file.FullName -Value $content -NoNewline
    Write-Host "Fixed: $($file.FullName)"
}
