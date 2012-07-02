package com.richitec.donkey.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/sample")
public class SampleController {

	@RequestMapping
	public String index(){
		return "sample";
	}
}
