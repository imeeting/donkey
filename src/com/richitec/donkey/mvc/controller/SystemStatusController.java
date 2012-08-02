package com.richitec.donkey.mvc.controller;

import java.util.Map;
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
		Map<String, Set<String>> map = ContextLoader.getConfereneManager().getSipUriToConferenceMap();
		ModelAndView mv = new ModelAndView();
		mv.addObject("confIdSet", allConfIdSet);
		mv.addObject("sipUriConfMap", map);
		mv.setViewName("system");
		return mv;
	}
	
	@RequestMapping("value=/conf/{confId}")
	public ModelAndView conf(){
		ModelAndView mv = new ModelAndView();
		return mv;
	}
}
