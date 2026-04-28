$templatePath = "d:\SLIIT University\Year 2 - Semester 2\IE2091 - IS Project\Assignment\Vendora\project\src\main\resources\templates"
$htmlFiles = Get-ChildItem -Path $templatePath -Filter *.html -Recurse

foreach ($file in $htmlFiles) {
    $content = Get-Content $file.FullName -Raw
    
    # 1. CSS paths
    $content = $content -replace 'href=".*static/css/(.*?\.css)"', 'th:href="@{/css/$1}"'
    
    # 2. JS paths
    $content = $content -replace 'src=".*static/js/(.*?\.js)"', 'th:src="@{/js/$1}"'
    
    # 3. Background Image paths (fixed regex to capture filename)
    $content = $content -replace "url\(['\"].*static/images/(.*?)['\"]\)", "url('/images/$1')"
    
    # 4. Image src paths
    $content = $content -replace 'src=".*static/images/(.*?)"', 'th:src="@{/images/$1}"'
    
    # 5. Internal Navigation Links (Manual common ones)
    $content = $content -replace 'href=".*admin-dashboard\.html"', 'th:href="@{/admin-dashboard}"'
    $content = $content -replace 'href=".*customer-dashboard\.html"', 'th:href="@{/customer-dashboard}"'
    $content = $content -replace 'href=".*supplier-dashboard\.html"', 'th:href="@{/supplier-dashboard}"'
    $content = $content -replace 'href=".*delivery-dashboard\.html"', 'th:href="@{/delivery-dashboard}"'
    $content = $content -replace 'href=".*admin-login\.html"', 'th:href="@{/admin-login}"'
    $content = $content -replace 'href=".*login\.html"', 'th:href="@{/login}"'
    $content = $content -replace 'href=".*admin-signup\.html"', 'th:href="@{/admin-signup}"'
    $content = $content -replace 'href=".*customer-signup\.html"', 'th:href="@{/customer-signup}"'
    $content = $content -replace 'href=".*pending-registrations\.html"', 'th:href="@{/pending-registrations}"'
    $content = $content -replace 'href=".*users\.html"', 'th:href="@{/users}"'
    $content = $content -replace 'href=".*profile\.html"', 'th:href="@{/profile}"'
    $content = $content -replace 'href=".*verify-email\.html"', 'th:href="@{/verify-email}"'
    
    # 6. Static HTML links
    $content = $content -replace 'href="/html/(.*?)\.html"', 'th:href="@{/$1}"'
    
    Set-Content -Path $file.FullName -Value $content -NoNewline
    Write-Host "Re-fixed: $($file.FullName)"
}
