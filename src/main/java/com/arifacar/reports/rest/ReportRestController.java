package com.arifacar.reports.rest;

import com.arifacar.reports.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by arifacar on 7/12/16.
 */
@Controller
@RequestMapping("/reports")
public class ReportRestController {
    @Autowired
    ReportService reportService;

}
