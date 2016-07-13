package com.arifacar.reports.rest;

import com.arifacar.reports.constant.DocumentType;
import com.arifacar.reports.constant.ReportEntity;
import com.arifacar.reports.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by arifacar on 7/12/16.
 */
@Controller
@RequestMapping("/reports")
public class ReportRestController {
    @Autowired
    ReportService reportService;

    @RequestMapping(value = "/crosstabReport/{reportEntity}/{entityId}/{documentType}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> vBonitetSinifineAyrilma(@PathVariable ReportEntity reportBaseEntity,
                                                                       @PathVariable Long entityId,
                                                                       @PathVariable DocumentType documentType) {
        return reportService.prepareResponse(reportService.crosstabReport(
                reportBaseEntity, entityId, documentType), documentType);
    }


    @RequestMapping(value = "/flatReport/{reportEntity}/{entityId}/{documentType}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> vSoraketlesmeLegenda(@PathVariable ReportEntity reportEntity,
                                                                    @PathVariable Long entityId,
                                                                    @PathVariable DocumentType documentType) {
        return reportService.prepareResponse(reportService.flatReport(
                reportEntity, entityId, documentType), documentType);
    }

    //TODO: Do image report with dynamic URL


}
