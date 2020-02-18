package com.aws.s3.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aws.s3.service.GeneratePDFService;


@RestController
public class GeneratePDFController {
	
	@Autowired
	private GeneratePDFService generatePDFService;

	private static final Logger logger = LoggerFactory.getLogger(GeneratePDFController.class);
	
	@RequestMapping( value = "/generatePDFFromDB", method = RequestMethod.GET, headers = "Accept=application/json")
	public ResponseEntity<?> generatePDFFromDB(@RequestParam String name) {
		String res = generatePDFService.generatePDFFromDB(name);		
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
		
}
