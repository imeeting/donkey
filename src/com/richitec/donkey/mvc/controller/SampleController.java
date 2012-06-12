package com.richitec.donkey.mvc.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/sample")
public class SampleController {

	@RequestMapping
	public String index(HttpSession session){
		return "sample";
	}
}
