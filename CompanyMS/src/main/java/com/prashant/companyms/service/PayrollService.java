package com.prashant.companyms.service;

import com.prashant.companyms.exception.BusinessRuleException;
import com.prashant.companyms.exception.ResourceNotFoundException;
import com.prashant.companyms.model.Employee;
import com.prashant.companyms.model.Payroll;
import com.prashant.companyms.repository.EmployeeRepository;
import com.prashant.companyms.repository.PayrollRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class PayrollService {

    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    public PayrollService(PayrollRepository payrollRepository, EmployeeRepository employeeRepository) {
        this.payrollRepository = payrollRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<Payroll> getAllPayrolls() {
        return payrollRepository.findAll().stream()
                .sorted((left, right) -> {
                    int yearComparison = right.getYear().compareTo(left.getYear());
                    return yearComparison != 0 ? yearComparison
                            : Integer.compare(monthNumber(right.getMonth()), monthNumber(left.getMonth()));
                })
                .toList();
    }

    public Payroll savePayroll(Payroll payroll) {
        Employee employee = employeeRepository.findById(payroll.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + payroll.getEmployeeId()));
        payroll.setEmployeeName(employee.getName());
        payroll.setDepartment(employee.getDepartment());
        payroll.setMonth(Month.valueOf(payroll.getMonth().trim().toUpperCase(Locale.ENGLISH))
                .getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        payroll.setBonus(zeroIfNull(payroll.getBonus()));
        payroll.setDeduction(zeroIfNull(payroll.getDeduction()));
        payroll.setNetSalary(payroll.getBasicSalary().add(payroll.getBonus()).subtract(payroll.getDeduction()));
        if (payroll.getNetSalary().signum() < 0) {
            throw new BusinessRuleException("Deduction cannot exceed basic salary plus bonus");
        }

        if (payrollRepository.existsByEmployeeIdAndMonthIgnoreCaseAndYear(
                payroll.getEmployeeId(), payroll.getMonth(), payroll.getYear())) {
            throw new BusinessRuleException("A payroll record already exists for this employee and month");
        }

        if (payroll.getPaymentStatus() == null || payroll.getPaymentStatus().isBlank()) {
            payroll.setPaymentStatus("PENDING");
        } else {
            payroll.setPaymentStatus(payroll.getPaymentStatus().toUpperCase());
        }
        payroll.setPaymentDate("PAID".equals(payroll.getPaymentStatus()) ? LocalDate.now() : null);
        return payrollRepository.save(payroll);
    }

    public Payroll markAsPaid(Long id) {
        Payroll payroll = getPayroll(id);
        payroll.setPaymentStatus("PAID");
        payroll.setPaymentDate(LocalDate.now());
        return payrollRepository.save(payroll);
    }

    public void deletePayroll(Long id) {
        payrollRepository.delete(getPayroll(id));
    }

    private Payroll getPayroll(Long id) {
        return payrollRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll record not found with id: " + id));
    }

    private BigDecimal zeroIfNull(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private int monthNumber(String month) {
        return Month.valueOf(month.toUpperCase(Locale.ENGLISH)).getValue();
    }
}