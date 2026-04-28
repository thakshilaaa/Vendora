import os
import re

template_dir = r'd:\SLIIT University\Year 2 - Semester 2\IE2091 - IS Project\Assignment\Vendora\project\src\main\resources\templates'

# Regex patterns
css_pattern = re.compile(r'href=".*static/css/(.*?\.css)"')
js_pattern = re.compile(r'src=".*static/js/(.*?\.js)"')
img_url_pattern = re.compile(r"url\('.*?static/images/(.*?)'\)")
# Handle image src too
img_src_pattern = re.compile(r'src=".*?static/images/(.*?)"')

def fix_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    new_content = css_pattern.sub(r'th:href="@{/css/\1}"', content)
    new_content = js_pattern.sub(r'th:src="@{/js/\1}"', new_content)
    new_content = img_url_pattern.sub(r"url('/images/\1')", new_content)
    new_content = img_src_pattern.sub(r'th:src="@{/images/\1}"', new_content)
    
    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Fixed: {filepath}")

for root, dirs, files in os.walk(template_dir):
    for file in files:
        if file.endswith('.html'):
            fix_file(os.path.join(root, file))
