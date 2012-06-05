package com.ivyinfo.donkey.ms.msml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ivyinfo.donkey.sip.SIPHelper;

public class MSMLHelper {

	public static final String MSML_CONTENT_TYPE = "application/msml+xml";

	public static boolean isMsmlContentType(String contentType) {
		return MSML_CONTENT_TYPE.equals(contentType);
	}

	public static void sendSipInfoWithMSML(SipSession controlSession,
			String msml) throws IOException {
		SipServletRequest info = controlSession.createRequest(SIPHelper.INFO);
		info.setContent(msml, MSML_CONTENT_TYPE);
		info.send();
	}

	public static String createconference(String deletewhen) {		
		StringBuffer msml = new StringBuffer();
		msml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		msml.append("<msml version=\"1.1\">\n");
		if("nocontrol".equals(deletewhen)){
			msml.append("<createconference deletewhen=\"nocontrol\">\n");
		}
		else{
			msml.append("<createconference deletewhen=\"nomedia\">\n");
		}
		msml.append("<audiomix id=\"audiomix\">\n");
		msml.append("<asn ri=\"10s\"/>\n");
		msml.append("<n-loudest n=\"3\" h=\"3\"/>\n");
		msml.append("</audiomix>\n");
		msml.append("</createconference>\n");
		msml.append("</msml>\n");
		return msml.toString();
	}

	public static String join(String conn, String confid) {
		StringBuffer msml = new StringBuffer();
		msml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		msml.append("<msml version=\"1.1\">\n");
		msml.append("<join id1=\"conn:" + conn + "\" id2=\"" + confid
				+ "\" />\n");
		msml.append("</msml>\n");
		return msml.toString();
	}
	
	public static String join_notice(String conn, String connRandomString, String confid) {
		StringBuffer msml = new StringBuffer();
		msml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		msml.append("<msml version=\"1.1\">\n");
		msml.append("<join id1=\"conn:" + conn + "\" id2=\"" + confid
				+ "\" />\n");
		msml.append("<dialogstart target=\"" + confid
				+ "\" type=\"application/moml+xml\" name=\"" + connRandomString + "\">\n");
		msml.append("<play cvd:barge=\"true\" cvd:cleardb=\"true\">\n");
		msml.append("<audio uri=\"file://provisioned/notice2.wav\"/>\n");
		msml.append("</play>\n");
		msml.append("</dialogstart>\n");
		msml.append("</msml>\n");
		return msml.toString();
	}

	public static String destroyconference(String confid) {
		StringBuffer msml = new StringBuffer();
		msml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		msml.append("<msml version=\"1.1\">\n");
		msml.append("<destroyconference id=\"" + confid + "\">\n");
		msml.append("</destroyconference>\n");
		msml.append("</msml>\n");
		return msml.toString();
	}
	
	public static String muteconnection(String conn, String confid, boolean isMute){
		StringBuffer msml = new StringBuffer();
		msml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		msml.append("<msml version=\"1.1\">\n");
		msml.append("<modifystream id2=\"conn:" + conn + "\" id1=\"" + confid
				+ "\" >\n");
		msml.append("<stream media=\"audio\" dir=\"to-id1\">\n");
		if(isMute){
			msml.append("<gain amt=\"mute\" />\n");
		}
		else{
			msml.append("<gain agc=\"true\" tgtlvl=\"-18\" />\n");
		}
		msml.append("</stream>\n");
		msml.append("</modifystream>\n");
		msml.append("</msml>\n");
		return msml.toString();
	}
	
	public static String announcement(String confid, String announcementname, boolean isPlay){
		StringBuffer msml = new StringBuffer();
		msml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		msml.append("<msml version=\"1.1\">\n");
		if(isPlay){
			msml.append("<dialogstart target=\"" + confid
					+ "\" type=\"application/moml+xml\" name=\"confannounce\">\n");
			msml.append("<play cvd:barge=\"true\" cvd:cleardb=\"true\">\n");
			msml.append("<audio uri=\"file://provisioned/" + announcementname + ".wav\"/>\n");
			msml.append("<playexit>\n");
			msml.append("<exit namelist=\"play.end play.amt\"/>\n");
			msml.append("</playexit>\n");
			msml.append("</play>\n");
			msml.append("</dialogstart>\n");
		}
		else{
			msml.append("<dialogend id=\"" + confid + "/dialog:confannounce\"/>\n");
		}
		msml.append("</msml>\n");
		return msml.toString();
	}
	
	public static String recordconference(String confid, String recordname, long maxtime, boolean isRecord){
		StringBuffer msml = new StringBuffer();
		msml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		msml.append("<msml version=\"1.1\">\n");
		if(isRecord){
			msml.append("<dialogstart target=\"" + confid
					+ "\" type=\"application/moml+xml\" name=\"confrecord\">\n");
			if(maxtime == 0){
				maxtime = 7200;
			}
			msml.append("<record append=\"true\" dest=\"nfs://" + recordname + ".wav\" format=\"audio/vnd.wave;codec=6\" maxtime=\"" + maxtime + "s\">\n");
			msml.append("<recordexit>\n");
			msml.append("<exit namelist=\"record.len record.end record.recordid\"/>\n");
			msml.append("</recordexit>\n");
			msml.append("</record>\n");
			msml.append("</dialogstart>\n");
		}
		else{
			msml.append("<dialogend id=\"" + confid + "/dialog:confrecord\"/>\n");
		}
		msml.append("</msml>\n");
		return msml.toString();
	}
	
	public static Vector<String> getNodeValues(InputStream msml, String parentNodeName, String nodeName)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
		Document dom = domBuilder.parse(msml);
		NodeList nodenameList = dom.getElementsByTagName(parentNodeName);
		Vector<String> vecStr = new Vector<String>();
		if(nodenameList != null){
			Node nodenameNode = nodenameList.item(0);
			for(Node node=nodenameNode.getFirstChild();node!=null;node=node.getNextSibling()){
				if(node.getNodeType() == Node.ELEMENT_NODE){
					if(nodeName.equals(node.getNodeName())){
						vecStr.add(node.getFirstChild().getNodeValue());
					}
				}
			}
		}
		
		return vecStr;
	}
	
	public static String getNodeAttributeValue(InputStream msml, String nodeName, String nodeAttributeName)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
		Document dom = domBuilder.parse(msml);
		NodeList nodenameList = dom.getElementsByTagName(nodeName);
		if(nodenameList != null){
			Node nodenameNode = nodenameList.item(0);
			if (nodenameNode.getNodeType() == Node.ELEMENT_NODE){
				if(nodenameNode.hasAttributes()){
					NamedNodeMap attributesMap = nodenameNode.getAttributes();
					for(int i=0;i!=attributesMap.getLength();i++){
						Attr attribute = (Attr) attributesMap.item(i);
						if(nodeAttributeName.equals(attribute.getName())){
							return attribute.getValue();
						}
					}
				}
			}
		}

		return null;
	}
}
