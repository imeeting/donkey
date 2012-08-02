package com.richitec.donkey.mvc.controller;

import java.util.Set;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.richitec.donkey.ContextLoader;

@Controller
@RequestMapping(value="/sys")
public class SystemStatusController {

	@RequestMapping
	public ModelAndView index(){
		Set<String> allConfIdSet = ContextLoader.getConfereneManager().getAllConferenceID();
		ModelAndView mv = new ModelAndView();
		mv.addObject("confIdSet", allConfIdSet);
		mv.setViewName("system");
		return mv;
	}
}
