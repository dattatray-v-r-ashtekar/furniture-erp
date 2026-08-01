// --- CONFIGURATION ---
// Set to true if running 'docker-compose up' (Monolith Mode - low RAM)
// Set to false if running 'docker-compose-microservices.yml' (High RAM)
const USE_MONOLITH_MODE = true; 
const MONOLITH_PORT = 8081;
// ---------------------

const modules = [
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
        const targetPort = USE_MONOLITH_MODE ? MONOLITH_PORT : mod.port;
        const apiUrl = `http://localhost:${targetPort}${mod.path}`;

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
            const targetPort = USE_MONOLITH_MODE ? MONOLITH_PORT : mod.port;
            const response = await fetch(`http://localhost:${targetPort}${mod.path}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(requestBody)
            });

            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const data = await response.json();
            
            logArea.innerHTML = `<span style="color:var(--accent)">Success!</span>\nResponse:\n${JSON.stringify(data, null, 2)}`;
            
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
            const targetPort = USE_MONOLITH_MODE ? MONOLITH_PORT : mod.port;
            logArea.innerHTML = `<span style="color:#ef4444">Connection Error.</span>\nIs the backend running on port ${targetPort}?\nEnsure @CrossOrigin is enabled.`;
            console.error(error);
        }
    };
});
