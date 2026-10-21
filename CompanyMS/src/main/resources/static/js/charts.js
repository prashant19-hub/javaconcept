(() => {
    const data = window.companyMsDashboardData;
    if (!data || typeof Chart === "undefined") {
        return;
    }

    const departmentCanvas = document.getElementById("departmentChart");
    if (departmentCanvas) {
        new Chart(departmentCanvas, {
            type: "bar",
            data: {
                labels: data.departmentLabels,
                datasets: [{
                    label: "Employees",
                    data: data.employeeCounts,
                    backgroundColor: "#5875e8",
                    borderRadius: 6,
                    maxBarThickness: 42
                }]
            },
            options: {
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
            }
        });
    }

    const payrollCanvas = document.getElementById("payrollChart");
    if (payrollCanvas) {
        new Chart(payrollCanvas, {
            type: "line",
            data: {
                labels: data.payrollLabels,
                datasets: [{
                    label: "Net payroll (INR)",
                    data: data.payrollTotals,
                    borderColor: "#168a61",
                    backgroundColor: "rgba(22, 138, 97, 0.12)",
                    fill: true,
                    tension: 0.3
                }]
            },
            options: {
                maintainAspectRatio: false,
                scales: { y: { beginAtZero: true } }
            }
        });
    }

    const employeeStatusCanvas = document.getElementById("employeeStatusChart");
    if (employeeStatusCanvas) {
        new Chart(employeeStatusCanvas, {
            type: "doughnut",
            data: {
                labels: ["Active", "Inactive"],
                datasets: [{
                    data: [data.activeEmployees, data.inactiveEmployees],
                    backgroundColor: ["#168a61", "#d8deea"],
                    borderWidth: 0
                }]
            },
            options: {
                maintainAspectRatio: false,
                cutout: "68%",
                plugins: { legend: { position: "bottom" } }
            }
        });
    }
})();