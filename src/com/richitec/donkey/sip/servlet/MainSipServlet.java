
package com.richitec.donkey.sip.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * SipServlet implementation class MainSipServlet
 */
@javax.servlet.sip.annotation.SipServlet(applicationName="donkey", name="mainServlet")
public class MainSipServlet extends SipServlet {

    private static final long serialVersionUID = 3978425801979081269L;
    private static final Log log = LogFactory.getLog(MainSipServlet.class);

    //Reference to context - The ctx Map is used as a central storage for this app
    ServletContext ctx = null;

    /*
     * Demonstrates extension with a new "REPUBLISH" method
     */
    @Override
    protected void doRequest(SipServletRequest req) throws ServletException, IOException {
    	log.info("\n REQUEST Method : " + req.getMethod() + 
    			"\n REQUEST From : " + req.getFrom().getValue() + 
    			"\n REQUEST To : " + req.getTo().getValue());
//    	SipSession session = req.getSession(false);
//    	session.setHandler(CallInSipServlet.class.getSimpleName());
    	RequestDispatcher dispatcher = req.getRequestDispatcher(CallInSipServlet.class.getSimpleName());
    	dispatcher.forward(req, null);
//        if( req.getMethod().equals("REPUBLISH") ) {
//            doRepublish(req);
//        } else {
//            super.doRequest(req);
//        }
    }
    
    /*
     * Implement the REPUBLISH extension here
     */    
    protected void doRepublish(SipServletRequest req) throws ServletException, IOException {
		// TODO Auto-generated method stub
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ctx = config.getServletContext();
    }
}
