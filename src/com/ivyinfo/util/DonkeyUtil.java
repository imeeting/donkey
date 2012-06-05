package com.ivyinfo.util;

import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;

import com.ivyinfo.donkey.ConferenceManager;

public class DonkeyUtil {

	private static final String SipUriPrefix = "sip:";
	private static final String SipUriPostfix = "@donkey.com";

	// check caller and callee sipUri
	public static String checkSipUri(String userSipUri) {
		String ret = userSipUri;

		if (!userSipUri.startsWith(SipUriPrefix)) {
			ret = SipUriPrefix + userSipUri + SipUriPostfix;
		}

		return ret;
	}

	public static SipSession checkMatchedSession(SipSession session,
			String matchString) {
		// define return result
		SipSession ret = session;

		SipServletRequest invite = (SipServletRequest) session
				.getAttribute(matchString);

		if (null == invite) {
			ret = (SipSession) session
					.getAttribute(ConferenceManager.LINKED_SESSION);
		}

		return ret;
	}

	public static boolean isDigit(String origString) {
		if (origString == null) {
			return true;
		}

		String reg = "[0-9]*";
		boolean ret = Pattern.matches(reg, origString);
		return ret;
	}

	public static boolean isValidString(String str) {
		if (null == str || str.length() <= 0) {
			return false;
		}

		return true;
	}

	public static boolean isValidString(String[] str) {
		if (null == str || str.length <= 0) {
			return false;
		}

		return true;
	}

	/**
	 * split the text with given split word
	 * 
	 * @param text
	 * @param splitWord
	 * @return
	 */
	public static String[] splitText(String text, String splitWord) {
		Vector sentences = new Vector();
		String[] ret = null;
		text += splitWord;

		if (text != null && !text.equals("")) {
			for (int i = 0, j = 0;;) {
				i = text.indexOf(splitWord, j);

				if (i >= 0 && i > j) {
					String tmp = text.substring(j, i);
					// System.out.println("[TextUtility] splitText - " + tmp);
					// // @test
					sentences.addElement(tmp);
				}
				if (i < 0 || i == (text.length() - splitWord.length())) {
					break;
				}
				j = i + splitWord.length();
			}

		}

		if (sentences.size() > 0) {
			ret = new String[sentences.size()];
			sentences.copyInto(ret);
		}

		return ret;

	}

	/**
	 * split the text between split1 & split2 by improved algorithm it can deal
	 * with the condition that split1 is the same as split2
	 * 
	 * @param text
	 * @param split1
	 * @param split2
	 * @return
	 */
	public static String[] splitText(String text, String split1, String split2) {
		Vector words = new Vector();
		String[] ret = null;

		int i = 0;
		int j = 0;

		if (null == text || null == split1 || null == split2) {
			return ret;
		}

		do {
			// get the first matched word
			i = text.indexOf(split1, j);
			// get the following matched word
			if ((i + split1.length()) < text.length()) {
				j = text.indexOf(split2, i + split1.length());
			}
			// if the i & j are not out of bound length of text
			if (j > i && j < text.length() && i < text.length()) {

				String tmp = text.substring(i + split1.length(), j);

				// System.out.println("[TextUtility] splitTextPro: " + tmp);
				// //@test
				words.addElement(tmp);

			}

			j = j + split2.length();
		} while (text.indexOf(split1, j) > 0 && text.indexOf(split2, j) > 0);

		if (words.size() > 0) {
			ret = new String[words.size()];
			words.copyInto(ret);
		}
		return ret;

	}

	public static String getUserFromSipURI(String sipuri) {
		final String sip = "sip:";
		final String at = "@";
		String uri = sipuri;
		int i = uri.indexOf(sip);
		if (i >= 0) {
			uri = uri.substring(i + sip.length());
		}

		i = uri.indexOf(at);
		if (i >= 0) {
			uri = uri.substring(0, i);
		}

		return uri;
	}

	public static void main(String arg[]) {
		String test = "sip:008613770662051@donkey.com";
		String test1 = "sip:123456789@test.com";
		String test2 = "123456789@test.com";
		String test3 = "sip:123456789";

		System.out.println(getUserFromSipURI(test1));
		System.out.println(getUserFromSipURI(test2));
		System.out.println(getUserFromSipURI(test3));

		String arr1[] = splitText(test, "sip:");
		String arr2[] = splitText(test, "sip:", "@");

		if (null == arr1 || arr1.length <= 0 || null == arr2
				|| arr2.length <= 0) {
			return;
		}

		System.out.println("splitText(String, String) " + arr1[0]);
		System.out.println("splitText(String, String, String)" + arr2[0]);
	}

}
