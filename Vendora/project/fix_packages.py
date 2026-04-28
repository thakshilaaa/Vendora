import os
import re

base_dir = r"d:\SLIIT University\Year 2 - Semester 2\IE2091 - IS Project\Assignment\Vendora\project\src\main\java"

class_locations = {}

# Pass 1: find all classes and their correct packages
for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".java"):
            # e.g., d:\...\src\main\java\com\vendora\epic6\controller\SupplierDashboardController.java
            rel_path = os.path.relpath(root, base_dir)
            correct_pkg = rel_path.replace(os.sep, '.')
            class_name = file[:-5]
            if class_name not in class_locations:
                class_locations[class_name] = f"{correct_pkg}.{class_name}"
            else:
                # duplicate class names, e.g., AdminController might be in epic4 and epic5
                # We will handle duplicates carefully
                if not isinstance(class_locations[class_name], list):
                    class_locations[class_name] = [class_locations[class_name]]
                class_locations[class_name].append(f"{correct_pkg}.{class_name}")

# Let's print duplicates to see
duplicates = {k: v for k, v in class_locations.items() if isinstance(v, list)}
print("Duplicates found:", duplicates)

def replace_package(content, correct_pkg):
    # Match any package declaration
    new_content, count = re.subn(r'^package\s+[\w\.]+;', f'package {correct_pkg};', content, flags=re.MULTILINE)
    return new_content

for root, dirs, files in os.walk(base_dir):
    for file in files:
        if file.endswith(".java"):
            filepath = os.path.join(root, file)
            # Only process epic2 to epic6
            if not any(f"epic{i}" in filepath for i in range(2, 7)):
                continue
                
            rel_path = os.path.relpath(root, base_dir)
            correct_pkg = rel_path.replace(os.sep, '.')
            
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
                
            new_content = replace_package(content, correct_pkg)
            
            if new_content != content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Fixed package in {filepath}")
