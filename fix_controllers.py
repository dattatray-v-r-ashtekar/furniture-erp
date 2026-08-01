import os
import glob
import re

controllers = glob.glob('*-service/src/main/java/com/furniture/erp/*/infrastructure/rest/*Controller.java')

for path in controllers:
    with open(path, 'r') as f:
        content = f.read()

    # Find if it is broken
    if 'record CreateRequest' in content and '    @GetMapping\n    public ResponseEntity<List<' in content.split('record CreateRequest')[1]:
        # The file is broken
        # We will reset it to before and then do it properly
        
        # We can just run git checkout on the file and then redo the modification correctly.
        os.system(f'git checkout {path}')
        
        with open(path, 'r') as f:
            clean_content = f.read()
            
        entity_match = re.search(r'public ResponseEntity<([A-Za-z0-9_]+)> (?:get|create)\(', clean_content)
        if not entity_match:
            continue
        entity = entity_match.group(1)
        
        if 'import java.util.List;' not in clean_content:
            clean_content = clean_content.replace('import org.springframework.web.bind.annotation.*;', 'import org.springframework.web.bind.annotation.*;\nimport java.util.List;')
            
        service_var_match = re.search(r'private final ([A-Za-z0-9_]+Service) ([A-Za-z0-9_]+);', clean_content)
        if not service_var_match:
            continue
        service_var = service_var_match.group(2)
        
        new_method = f"""

    @GetMapping
    public ResponseEntity<List<{entity}>> getAll() {{
        return ResponseEntity.ok({service_var}.getAll());
    }}
"""
        
        # We need to insert it BEFORE the final brace of the controller class, not the record.
        # Find the position of 'record CreateRequest'
        record_idx = clean_content.find('record CreateRequest')
        if record_idx != -1:
            # The class ends before the record
            # Let's find the closing brace before the record
            brace_idx = clean_content.rfind('}', 0, record_idx)
            if brace_idx != -1:
                clean_content = clean_content[:brace_idx] + new_method + clean_content[brace_idx:]
        else:
            last_brace = clean_content.rfind('}')
            clean_content = clean_content[:last_brace] + new_method + clean_content[last_brace:]
            
        with open(path, 'w') as f:
            f.write(clean_content)
            
        print(f"Fixed {path}")
