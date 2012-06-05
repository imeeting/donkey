package com.ivyinfo.donkey.sample;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.sun.enterprise.web.connector.grizzly.comet.CometEvent;
import com.sun.enterprise.web.connector.grizzly.comet.CometHandler;

public class RecordConfCometHandler implements CometHandler<HttpServletResponse> {
	
	private HttpServletResponse response;

	@Override
	public void attach(HttpServletResponse resp) {
		this.response = resp;
	}

	@Override
	public void onEvent(CometEvent event) throws IOException {
		
		if (event.getType() == CometEvent.NOTIFY){
			PrintWriter out = response.getWriter();
			out.print(event.attachment().toString());
			out.close();
			event.getCometContext().resumeCometHandler(this);
		}
	}

	@Override
	public void onInitialize(CometEvent arg0) throws IOException {
		
	}

	@Override
	public void onInterrupt(CometEvent arg0) throws IOException {
		
	}

	@Override
	public void onTerminate(CometEvent arg0) throws IOException {
		
	}

}
