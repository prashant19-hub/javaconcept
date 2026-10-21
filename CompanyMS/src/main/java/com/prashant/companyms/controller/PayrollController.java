package com.prashant.companyms.controller;

import com.prashant.companyms.exception.BusinessRuleException;
import com.prashant.companyms.model.Employee;
import com.prashant.companyms.model.Payroll;
import com.prashant.companyms.service.EmployeeService;
import com.prashant.companyms.service.PayrollService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;

@Controller
@RequestMapping("/companyms/payroll")
public class PayrollController {

    private final PayrollService payrollService;
    private final EmployeeService employeeService;

    public PayrollController(PayrollService payrollService, EmployeeService employeeService) {
        this.payrollService = payrollService;
        this.employeeService = employeeService;
    }

    @GetMapping
    public String payrollPage(Model model) {
        addPageData(model);
        return "payroll";
    }

    @PostMapping("/save")
    public String savePayroll(@Valid @ModelAttribute("payroll") Payroll payroll,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addPageData(model);
            return "payroll";
        }
        try {
            payrollService.savePayroll(payroll);
            redirectAttributes.addFlashAttribute("successMessage", "Payroll record saved successfully.");
            return "redirect:/companyms/payroll";
        } catch (BusinessRuleException exception) {
            bindingResult.reject("payroll", exception.getMessage());
            addPageData(model);
            return "payroll";
        }
    }

    @PostMapping("/paid/{id}")
    public String markPaid(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        payrollService.markAsPaid(id);
        redirectAttributes.addFlashAttribute("successMessage", "Payroll marked as paid.");
        return "redirect:/companyms/payroll";
    }

    @PostMapping("/delete/{id}")
    public String deletePayroll(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        payrollService.deletePayroll(id);
        redirectAttributes.addFlashAttribute("successMessage", "Payroll record deleted.");
        return "redirect:/companyms/payroll";
    }

    private void addPageData(Model model) {
        model.addAttribute("payrolls", payrollService.getAllPayrolls());
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("months", Arrays.stream(Month.values())
                .map(month -> month.getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                .toList());
        if (!model.containsAttribute("payroll")) {
            Payroll payroll = new Payroll();
            payroll.setYear(Year.now().getValue());
            payroll.setPaymentStatus("PENDING");
            model.addAttribute("payroll", payroll);
        }
    }
}