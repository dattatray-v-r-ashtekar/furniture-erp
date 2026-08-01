import os
import glob
import re

controllers = glob.glob('*-service/src/main/java/com/furniture/erp/*/infrastructure/rest/*Controller.java')

for path in controllers:
    with open(path, 'r') as f:
        content = f.read()

    # Find the entity class name from the return type of the get method or create method
    entity_match = re.search(r'public ResponseEntity<([A-Za-z0-9_]+)> (?:get|create)\(', content)
    if not entity_match:
        continue
    
    entity = entity_match.group(1)
    
    if '@GetMapping\n    public ResponseEntity<List<' in content or 'List<' + entity + '>' in content:
        continue # already has it
        
    # We need java.util.List
    if 'import java.util.List;' not in content:
        content = content.replace('import org.springframework.web.bind.annotation.*;', 'import org.springframework.web.bind.annotation.*;\nimport java.util.List;')
        
    # Find the service name
    service_var_match = re.search(r'private final ([A-Za-z0-9_]+Service) ([A-Za-z0-9_]+);', content)
    if not service_var_match:
        continue
    service_var = service_var_match.group(2)

    # Insert the findAll method
    new_method = f"""

    @GetMapping
    public ResponseEntity<List<{entity}>> getAll() {{
        return ResponseEntity.ok({service_var}.getAll());
    }}
"""
    # Using rfind to insert before the last brace
    last_brace = content.rfind('}')
    content = content[:last_brace] + new_method + content[last_brace:]
    
    with open(path, 'w') as f:
        f.write(content)
    
    print(f"Updated controller: {path}")

# Now update the services to have getAll()
services = glob.glob('*-service/src/main/java/com/furniture/erp/*/application/service/*Service.java')
for path in services:
    with open(path, 'r') as f:
        content = f.read()
        
    entity_match = re.search(r'public ([A-Za-z0-9_]+) getById', content)
    if not entity_match:
        continue
    entity = entity_match.group(1)
    
    if 'List<' + entity + '> getAll' in content:
        continue
        
    if 'import java.util.List;' not in content:
        content = content.replace('import org.springframework.stereotype.Service;', 'import org.springframework.stereotype.Service;\nimport java.util.List;')
        
    repo_var_match = re.search(r'private final ([A-Za-z0-9_]+Repository) ([A-Za-z0-9_]+);', content)
    if not repo_var_match:
        continue
    repo_var = repo_var_match.group(2)
    
    new_method = f"""
    public List<{entity}> getAll() {{
        return {repo_var}.findAll();
    }}
"""
    last_brace = content.rfind('}')
    content = content[:last_brace] + new_method + content[last_brace:]
    
    with open(path, 'w') as f:
        f.write(content)
    print(f"Updated service: {path}")
