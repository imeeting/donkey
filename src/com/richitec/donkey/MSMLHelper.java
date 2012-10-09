package com.richitec.donkey;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.richitec.donkey.msml.BooleanType;
import com.richitec.donkey.msml.DialogLanguageDatatype;
import com.richitec.donkey.msml.Dtmf;
import com.richitec.donkey.msml.ExitType;
import com.richitec.donkey.msml.Msml;
import com.richitec.donkey.msml.ObjectFactory;
import com.richitec.donkey.msml.Play;
import com.richitec.donkey.msml.Send;

public class MSMLHelper {
	
	private JAXBContext jc;
	private Unmarshaller ju;
	private Marshaller jm;
	
	private ObjectFactory msmlObjFactory;
	
	public MSMLHelper(){
        msmlObjFactory = new ObjectFactory(); 
        
		try {
			this.jc = JAXBContext.newInstance(Msml.class);
			this.ju = jc.createUnmarshaller();
			this.jm = jc.createMarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

    public Play createPlay(String audioUri, String eventName){
    	Play play = new Play();
    	play.setBarge(BooleanType.TRUE);
    	play.setCleardb(BooleanType.TRUE);
    	
    	Play.Audio audio = new Play.Audio();
    	audio.setUri(audioUri);
    	play.getAudioOrMedia().add(msmlObjFactory.createPlayAudio(audio));
    	
    	if (null!=eventName && eventName.length()>0){
    		Play.Playexit playExit = new Play.Playexit();
    		
    		Send send = new Send();
    		send.setTarget("source");
    		send.setEvent(eventName);
    		playExit.getContent().add(send);
    		
	    	ExitType exit = new ExitType();
	    	exit.setNamelist("play.end play.amt");
	    	playExit.getContent().add(msmlObjFactory.createPlayPlayexitExit(exit));
    		
    		play.setPlayexit(playExit);
    	}
    	
    	return play;
    }
    
    public Msml.Dialogstart createDialogStart(String target){
    	Msml.Dialogstart dialogstart = new Msml.Dialogstart();
    	dialogstart.setTarget(target);
    	dialogstart.setType(DialogLanguageDatatype.APPLICATION_MOML_XML);
    	return dialogstart;
    }
    
    public Dtmf createDtmf(){
    	Dtmf dtmf = new Dtmf();
    	
    	dtmf.setFdt("10s");
    	dtmf.setIdt("5s");
    	dtmf.setEdt("2s");
    	
    	Dtmf.Pattern pattern = new Dtmf.Pattern();
    	pattern.setDigits("min=1;max=9;rtk=#");
    	pattern.setFormat("moml+digits");
    	
    	dtmf.getPattern().add(pattern);
    	
    	Dtmf.Dtmfexit dtmfexit = new Dtmf.Dtmfexit();
    	
    	ExitType exit = new ExitType();
    	exit.setNamelist("dtmf.digits dtmf.end");
    	dtmfexit.getContent().add(msmlObjFactory.createDtmfDtmfexitExit(exit));
    	
//		Send send = new Send();
//		send.setTarget("source");
//		send.setEvent(Collect_DTMF_Completed);
//		send.setNamelist("dtmf.digits dtmf.end");
//		dtmfexit.getContent().add(send);
    	
    	dtmf.setDtmfexit(dtmfexit);
    	return dtmf;
    }
    
    public String createMsml(Object msmlRequest){
    	Msml msml = new Msml();
    	msml.getMsmlRequest().add(msmlRequest);
    	
		OutputStream os = new ByteArrayOutputStream();
		try {
			XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(os);
			writer.setPrefix("cvd", "http://convedia.com/ext");
			jm.marshal(msml, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
		
		return os.toString();
    }
    
    public String createMsml(Object [] msmlRequest){
    	Msml msml = new Msml();
    	for (Object obj : msmlRequest){
    		msml.getMsmlRequest().add(obj);
    	}
    	
		OutputStream os = new ByteArrayOutputStream();
		try {
			XMLStreamWriter writer = XMLOutputFactory.newInstance().createXMLStreamWriter(os);
			writer.setPrefix("cvd", "http://convedia.com/ext");
			jm.marshal(msml, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
		
		return os.toString();
    }
    
}
