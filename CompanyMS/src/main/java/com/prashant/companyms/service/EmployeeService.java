package com.prashant.companyms.service;

import com.prashant.companyms.exception.BusinessRuleException;
import com.prashant.companyms.exception.ResourceNotFoundException;
import com.prashant.companyms.model.Employee;
import com.prashant.companyms.repository.AttendanceRepository;
import com.prashant.companyms.repository.EmployeeRepository;
import com.prashant.companyms.repository.PayrollRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final PayrollRepository payrollRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           AttendanceRepository attendanceRepository,
                           PayrollRepository payrollRepository) {
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
        this.payrollRepository = payrollRepository;
    }

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll(org.springframework.data.domain.Sort.by("name").ascending());
    }

    @Transactional(readOnly = true)
    public List<Employee> searchEmployees(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllEmployees();
        }
        String search = keyword.trim();
        return employeeRepository
                .findByNameContainingIgnoreCaseOrDepartmentContainingIgnoreCaseOrderByNameAsc(search, search);
    }

    @Transactional(readOnly = true)
    public Employee getEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    public Employee saveEmployee(Employee employee) {
        if (employee.getId() != null) {
            getEmployee(employee.getId());
        }
        employee.setName(employee.getName().trim());
        employee.setEmail(employee.getEmail().trim());
        employee.setDepartment(employee.getDepartment().trim());
        if (employee.getId() == null && (employee.getStatus() == null || employee.getStatus().isBlank())) {
            employee.setStatus("ACTIVE");
        }

        boolean duplicateEmail = employee.getId() == null
                ? employeeRepository.existsByEmailIgnoreCase(employee.getEmail())
                : employeeRepository.existsByEmailIgnoreCaseAndIdNot(employee.getEmail(), employee.getId());
        if (duplicateEmail) {
            throw new BusinessRuleException("An employee with this email already exists");
        }
        return employeeRepository.save(employee);
    }

    public Employee transferDepartment(Long id, String newDepartment) {
        if (newDepartment == null || newDepartment.isBlank()) {
            throw new BusinessRuleException("New department is required");
        }
        Employee employee = getEmployee(id);
        employee.setDepartment(newDepartment.trim());
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        Employee employee = getEmployee(id);
        if (attendanceRepository.existsByEmployeeId(id) || payrollRepository.existsByEmployeeId(id)) {
            throw new BusinessRuleException(
                    "Employee has attendance or payroll history and cannot be deleted");
        }
        employeeRepository.delete(employee);
    }
}