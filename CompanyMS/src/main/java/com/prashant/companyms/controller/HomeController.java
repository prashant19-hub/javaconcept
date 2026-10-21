package com.prashant.companyms.controller;

import com.prashant.companyms.model.Employee;
import com.prashant.companyms.model.Payroll;
import com.prashant.companyms.service.AttendanceService;
import com.prashant.companyms.service.EmployeeService;
import com.prashant.companyms.service.PayrollService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/companyms")
public class HomeController {

    private final EmployeeService employeeService;
    private final PayrollService payrollService;
    private final AttendanceService attendanceService;

    public HomeController(EmployeeService employeeService,
                          PayrollService payrollService,
                          AttendanceService attendanceService) {
        this.employeeService = employeeService;
        this.payrollService = payrollService;
        this.attendanceService = attendanceService;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        List<Employee> employees = employeeService.getAllEmployees();
        List<Payroll> payrolls = payrollService.getAllPayrolls();
        long activeEmployees = employees.stream()
                .filter(employee -> "ACTIVE".equalsIgnoreCase(employee.getStatus()))
                .count();
        model.addAttribute("totalEmployees", employees.size());
        model.addAttribute("activeEmployees", activeEmployees);
        model.addAttribute("inactiveEmployees", employees.size() - activeEmployees);
        model.addAttribute("totalPayrolls", payrolls.size());
        model.addAttribute("totalAttendance", attendanceService.getAllAttendance().size());

        Map<String, Long> employeesByDepartment = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment, TreeMap::new, Collectors.counting()));
        model.addAttribute("departmentLabels", new ArrayList<>(employeesByDepartment.keySet()));
        model.addAttribute("employeeCounts", new ArrayList<>(employeesByDepartment.values()));

        Map<String, BigDecimal> payrollByMonth = new TreeMap<>();
        payrolls.forEach(payroll -> {
            String key = payroll.getYear() + "-" + String.format("%02d", monthNumber(payroll.getMonth()));
            payrollByMonth.merge(key, payroll.getNetSalary(), BigDecimal::add);
        });
        List<String> payrollLabels = new ArrayList<>(payrollByMonth.keySet());
        List<Double> payrollTotals = payrollByMonth.values().stream()
                .map(BigDecimal::doubleValue)
                .toList();
        model.addAttribute("payrollLabels", payrollLabels);
        model.addAttribute("payrollTotals", payrollTotals);
        return "dashboard";
    }

    private int monthNumber(String month) {
        try {
            return java.time.Month.valueOf(month.toUpperCase()).getValue();
        } catch (IllegalArgumentException exception) {
            return 13;
        }
    }
}