import os

html_content = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Furniture ERP Central</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <div class="app-container">
        <!-- Sidebar Navigation -->
        <aside class="sidebar glass">
            <div class="sidebar-header">
                <h2>Furniture ERP</h2>
            </div>
            <nav id="nav-menu">
                <!-- Dynamically generated nav links will go here -->
            </nav>
        </aside>

        <!-- Main Content Area -->
        <main class="main-content">
            <header class="top-bar glass">
                <h1 id="page-title">Dashboard</h1>
                <div class="user-profile">Admin User</div>
            </header>
            
            <div id="content-area" class="content-wrapper">
                <!-- Dynamic content injected here -->
                <div class="welcome-card glass">
                    <h2>Welcome to the Enterprise Command Center</h2>
                    <p>Select a module from the left sidebar to manage its data.</p>
                </div>
            </div>
        </main>
    </div>
    <script src="app.js"></script>
</body>
</html>
"""

css_content = """:root {
    --bg-color: #0f172a;
    --glass-bg: rgba(30, 41, 59, 0.7);
    --glass-border: rgba(255, 255, 255, 0.1);
    --primary-color: #3b82f6;
    --primary-hover: #2563eb;
    --text-main: #f8fafc;
    --text-muted: #94a3b8;
    --accent: #10b981;
}

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Inter', sans-serif;
}

body {
    background: var(--bg-color);
    background-image: radial-gradient(circle at 15% 50%, rgba(59, 130, 246, 0.15), transparent 25%),
                      radial-gradient(circle at 85% 30%, rgba(16, 185, 129, 0.15), transparent 25%);
    color: var(--text-main);
    min-height: 100vh;
    overflow: hidden;
}

.glass {
    background: var(--glass-bg);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    border: 1px solid var(--glass-border);
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.3);
}

.app-container {
    display: flex;
    height: 100vh;
}

/* Sidebar */
.sidebar {
    width: 280px;
    display: flex;
    flex-direction: column;
    border-right: 1px solid var(--glass-border);
    z-index: 10;
}

.sidebar-header {
    padding: 24px;
    border-bottom: 1px solid var(--glass-border);
}

.sidebar-header h2 {
    font-weight: 600;
    font-size: 1.2rem;
    letter-spacing: 1px;
    background: linear-gradient(90deg, #3b82f6, #10b981);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

nav {
    flex: 1;
    overflow-y: auto;
    padding: 16px 0;
}

nav::-webkit-scrollbar { width: 6px; }
nav::-webkit-scrollbar-thumb { background: var(--glass-border); border-radius: 4px; }

.nav-item {
    display: block;
    padding: 12px 24px;
    color: var(--text-muted);
    text-decoration: none;
    transition: all 0.2s ease;
    cursor: pointer;
    font-size: 0.95rem;
}

.nav-item:hover, .nav-item.active {
    background: rgba(59, 130, 246, 0.1);
    color: var(--primary-color);
    border-right: 3px solid var(--primary-color);
}

/* Main Content */
.main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}

.top-bar {
    height: 72px;
    padding: 0 32px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid var(--glass-border);
}

.content-wrapper {
    flex: 1;
    padding: 32px;
    overflow-y: auto;
}

/* Cards & UI Elements */
.welcome-card {
    padding: 40px;
    border-radius: 16px;
    text-align: center;
    margin-top: 20vh;
    animation: fadeIn 0.5s ease-out;
}

.module-container {
    animation: fadeIn 0.3s ease-out;
}

.card {
    padding: 24px;
    border-radius: 12px;
    margin-bottom: 24px;
}

.form-group {
    margin-bottom: 16px;
}

.form-group label {
    display: block;
    margin-bottom: 8px;
    color: var(--text-muted);
    font-size: 0.9rem;
}

.form-group input {
    width: 100%;
    max-width: 400px;
    padding: 12px;
    background: rgba(0,0,0,0.2);
    border: 1px solid var(--glass-border);
    border-radius: 8px;
    color: var(--text-main);
    outline: none;
    transition: border-color 0.2s;
}

.form-group input:focus {
    border-color: var(--primary-color);
}

.btn {
    padding: 12px 24px;
    background: var(--primary-color);
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-weight: 600;
    transition: background 0.2s;
}

.btn:hover {
    background: var(--primary-hover);
}

.data-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 16px;
}

.data-table th, .data-table td {
    padding: 16px;
    text-align: left;
    border-bottom: 1px solid var(--glass-border);
}

.data-table th {
    color: var(--text-muted);
    font-weight: 600;
}

.response-log {
    margin-top: 16px;
    padding: 16px;
    background: rgba(0,0,0,0.3);
    border-radius: 8px;
    font-family: monospace;
    white-space: pre-wrap;
    color: var(--accent);
}

@keyframes fadeIn {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}
"""

js_content = """const modules = [
    { id: 'inventory', name: 'Inventory', port: 8081, path: '/api/v1/inventory/items', payload: { skuCode: "WOOD-01", quantity: 100 }, fieldLabel: 'SKU Code' },
    { id: 'procurement', name: 'Procurement', port: 8082, path: '/api/v1/procurement/orders', payload: { vendorId: "VND-123" }, fieldLabel: 'Vendor ID' },
    { id: 'erp-central', name: 'ERP Central (Sales)', port: 8083, path: '/api/v1/erp/sales-orders', payload: { referenceCode: "SO-100" }, fieldLabel: 'Reference Code' },
    { id: 'mes', name: 'Manufacturing (MES)', port: 8084, path: '/api/v1/mes/orders', payload: { productSku: "CHAIR-01", targetQuantity: 50 }, fieldLabel: 'Product SKU' },
    { id: 'wms', name: 'Warehouse (WMS)', port: 8085, path: '/api/v1/wms/bins', payload: { referenceCode: "BIN-A1" }, fieldLabel: 'Bin Reference' },
    { id: 'tms', name: 'Transportation (TMS)', port: 8086, path: '/api/v1/tms/routes', payload: { referenceCode: "RT-NYC" }, fieldLabel: 'Route Reference' },
    { id: 'crm', name: 'CRM', port: 8087, path: '/api/v1/crm/customers', payload: { referenceCode: "CUST-01" }, fieldLabel: 'Customer Reference' },
    { id: 'dealer', name: 'Dealer Portal', port: 8088, path: '/api/v1/dealer/orders', payload: { referenceCode: "B2B-99" }, fieldLabel: 'Wholesale Reference' },
    { id: 'ecommerce', name: 'E-Commerce', port: 8089, path: '/api/v1/ecommerce/orders', payload: { referenceCode: "WEB-44" }, fieldLabel: 'Web Order Ref' },
    { id: 'hrms', name: 'HRMS', port: 8090, path: '/api/v1/hrms/employees', payload: { referenceCode: "EMP-001" }, fieldLabel: 'Employee ID' },
    { id: 'payroll', name: 'Payroll', port: 8091, path: '/api/v1/payroll/slips', payload: { referenceCode: "PAY-OCT" }, fieldLabel: 'Payroll Reference' },
    { id: 'accounting', name: 'Accounting', port: 8092, path: '/api/v1/accounting/ledgers', payload: { referenceCode: "LEDG-2026" }, fieldLabel: 'Ledger Ref' },
    { id: 'qms', name: 'Quality Management', port: 8093, path: '/api/v1/qms/inspections', payload: { referenceCode: "INSP-01" }, fieldLabel: 'Inspection Ref' },
    { id: 'bi', name: 'Business Intelligence', port: 8094, path: '/api/v1/bi/reports', payload: { referenceCode: "RPT-Q4" }, fieldLabel: 'Report Ref' }
];

document.addEventListener('DOMContentLoaded', () => {
    const navMenu = document.getElementById('nav-menu');
    const contentArea = document.getElementById('content-area');
    const pageTitle = document.getElementById('page-title');

    // Generate Nav Links
    modules.forEach(mod => {
        const a = document.createElement('a');
        a.className = 'nav-item';
        a.textContent = mod.name;
        a.onclick = () => loadModule(mod, a);
        navMenu.appendChild(a);
    });

    function loadModule(mod, activeElement) {
        // Update Active State
        document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
        if(activeElement) activeElement.classList.add('active');
        
        pageTitle.textContent = mod.name;

        // Note: For a real cross-origin request from a file:// URL to localhost, CORS must be enabled on the Spring Boot backend. 
        // For demonstration, we build the UI to make the exact correct REST calls.
        const apiUrl = `http://localhost:${mod.port}${mod.path}`;

        // Build View
        contentArea.innerHTML = `
            <div class="module-container">
                <div class="card glass">
                    <h2>Create New Record</h2>
                    <p style="color:var(--text-muted); margin-bottom: 16px; font-size: 0.85rem;">POST ${apiUrl}</p>
                    
                    <div class="form-group">
                        <label>${mod.fieldLabel}</label>
                        <input type="text" id="inputField" value="${Object.values(mod.payload)[0]}">
                    </div>
                    <button class="btn" onclick="submitData('${mod.id}')">Submit to Backend</button>
                    
                    <div id="logArea-${mod.id}" style="display:none;" class="response-log"></div>
                </div>
                
                <div class="card glass">
                    <h2>Recent Records</h2>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Reference</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody id="tableBody-${mod.id}">
                            <tr><td colspan="3" style="text-align:center; color:var(--text-muted);">No records fetched yet. Ensure backend is running.</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        `;
    }

    window.submitData = async (moduleId) => {
        const mod = modules.find(m => m.id === moduleId);
        const inputValue = document.getElementById('inputField').value;
        const logArea = document.getElementById(`logArea-${mod.id}`);
        
        logArea.style.display = 'block';
        logArea.innerHTML = 'Sending request...';

        // Prepare payload dynamically based on the input
        const payloadKey = Object.keys(mod.payload)[0];
        const requestBody = { ...mod.payload };
        requestBody[payloadKey] = inputValue;

        try {
            const response = await fetch(`http://localhost:${mod.port}${mod.path}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(requestBody)
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const data = await response.json();
            
            logArea.innerHTML = `<span style="color:var(--accent)">Success!</span>\\nResponse:\\n${JSON.stringify(data, null, 2)}`;
            
            // Mock adding to table
            const tbody = document.getElementById(`tableBody-${mod.id}`);
            if (tbody.querySelector('td[colspan]')) tbody.innerHTML = '';
            
            tbody.innerHTML += `
                <tr>
                    <td style="font-family:monospace; font-size:0.8rem">${data.id || 'UUID'}</td>
                    <td>${inputValue}</td>
                    <td><span style="color:var(--accent)">CREATED</span></td>
                </tr>
            `;

        } catch (error) {
            logArea.innerHTML = `<span style="color:#ef4444">Connection Error.</span>\\nIs the Spring Boot service running on port ${mod.port}?\\nEnsure @CrossOrigin is enabled on the Controller.`;
            console.error(error);
        }
    };
});
"""

os.makedirs("furniture-erp-ui", exist_ok=True)
with open("furniture-erp-ui/index.html", "w") as f:
    f.write(html_content)
with open("furniture-erp-ui/styles.css", "w") as f:
    f.write(css_content)
with open("furniture-erp-ui/app.js", "w") as f:
    f.write(js_content)

print("UI successfully generated in furniture-erp-ui/")
