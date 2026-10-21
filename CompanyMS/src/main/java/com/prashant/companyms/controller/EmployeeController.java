package com.prashant.companyms.controller;

import com.prashant.companyms.exception.BusinessRuleException;
import com.prashant.companyms.model.Employee;
import com.prashant.companyms.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/companyms/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String employeesPage(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("employees", employeeService.searchEmployees(search));
        model.addAttribute("search", search);
        return "employees";
    }

    @GetMapping("/add")
    public String addEmployeePage(Model model) {
        model.addAttribute("employee", new Employee());
        return "add-employee";
    }

    @GetMapping("/edit/{id}")
    public String editEmployeePage(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployee(id));
        return "add-employee";
    }

    @PostMapping("/save")
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "add-employee";
        }
        try {
            employeeService.saveEmployee(employee);
            redirectAttributes.addFlashAttribute("successMessage", "Employee saved successfully.");
            return "redirect:/companyms/employees";
        } catch (BusinessRuleException exception) {
            bindingResult.rejectValue("email", "duplicate", exception.getMessage());
            return "add-employee";
        }
    }

    @PostMapping("/transfer/{id}")
    public String transferEmployee(@PathVariable Long id,
                                   @RequestParam String department,
                                   RedirectAttributes redirectAttributes) {
        try {
            employeeService.transferDepartment(id, department);
            redirectAttributes.addFlashAttribute("successMessage", "Department updated successfully.");
        } catch (BusinessRuleException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/companyms/employees";
    }

    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully.");
        } catch (BusinessRuleException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/companyms/employees";
    }
}