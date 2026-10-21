package com.prashant.companyms.controller;

import com.prashant.companyms.exception.BusinessRuleException;
import com.prashant.companyms.model.Attendance;
import com.prashant.companyms.service.AttendanceService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/companyms/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;

    public AttendanceController(AttendanceService attendanceService, EmployeeService employeeService) {
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
    }

    @GetMapping
    public String attendancePage(Model model) {
        addPageData(model);
        return "attendance";
    }

    @PostMapping("/save")
    public String saveAttendance(@Valid @ModelAttribute("attendance") Attendance attendance,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            addPageData(model);
            return "attendance";
        }
        try {
            attendanceService.saveAttendance(attendance);
            redirectAttributes.addFlashAttribute("successMessage", "Attendance record saved.");
            return "redirect:/companyms/attendance";
        } catch (BusinessRuleException exception) {
            bindingResult.reject("attendance", exception.getMessage());
            addPageData(model);
            return "attendance";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteAttendance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        attendanceService.deleteAttendance(id);
        redirectAttributes.addFlashAttribute("successMessage", "Attendance record deleted.");
        return "redirect:/companyms/attendance";
    }

    private void addPageData(Model model) {
        model.addAttribute("attendanceRecords", attendanceService.getAllAttendance());
        model.addAttribute("employees", employeeService.getAllEmployees());
        if (!model.containsAttribute("attendance")) {
            Attendance attendance = new Attendance();
            attendance.setAttendanceDate(LocalDate.now());
            attendance.setStatus("PRESENT");
            model.addAttribute("attendance", attendance);
        }
    }
}
