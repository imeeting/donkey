package com.ivyinfo.donkey.sip;

import java.util.Random;

public class SDPHelper {
	private static final Random random = new Random();

	public static final String SDP_CONTENT_TYPE = "application/sdp";

	public static String getNoMediaSDP(String offer) {
		StringBuilder sb = new StringBuilder();
		String c = "IN IP4 0.0.0.0";
		sb.append("v=0\r\n"); // protocol version
		sb.append("o=-" + " " + randomUUID() + " " + randomUUID() + " " + c
				+ "\r\n"); // owner/creator and session identifier (rfc 2327)
		sb.append("s=futuo session\r\n"); // session name
		// sb.append("c=" + c + "\r\n"); // connection information - not
		// required if included in all media
		sb.append("t=0 0\r\n"); // time the session is active
		return sb.toString();
	}

	/*
	v=0
	o=- 36139 36139 IN IP4 192.168.1.2
	s=media server session
	c=IN IP4 122.96.24.171
	t=0 0
	m=audio 34138 RTP/AVP 0 8 101 100 99 98 18 4 102 103 9 96
	a=rtpmap:0 PCMU/8000
	a=rtpmap:8 PCMA/8000
	a=rtpmap:101 G726-40/8000
	a=rtpmap:100 G726-32/8000
	a=rtpmap:99 G726-24/8000
	a=rtpmap:98 G726-16/8000
	a=rtpmap:18 G729/8000
	a=rtpmap:4 G723/8000
	a=rtpmap:102 AMR/8000
	a=fmtp:102 octet-align=1
	a=rtpmap:103 AMR/8000
	a=rtpmap:9 G722/8000
	a=ptime:20
	a=rtpmap:96 telephone-event/8000
	a=fmtp:96 0-15,32,36
	a=sendrecv
	*/
	public static String getMsSDP(boolean hasM) {
		StringBuilder sb = new StringBuilder();
		String c = "IN IP4 122.96.24.171";
		sb.append("v=0\r\n"); // protocol version
		sb.append("o=-" + " " + randomUUID() + " " + randomUUID() + " " + c
				+ "\r\n"); // owner/creator and session identifier (rfc 2327)
		sb.append("s=futuo session\r\n"); // session name
		sb.append("c=" + c + "\r\n"); // connection information - not required
										// if included in all media
		sb.append("t=0 0\r\n"); // time the session is active
		if (hasM) {
			sb.append("m=audio 34138 RTP/AVP 0 8 101 100 99 98 18 4 102 103 9 96\r\n");
		}
		sb.append("a=rtpmap:0 PCMU/8000\r\n");
		sb.append("a=rtpmap:0 PCMA/8000\r\n");
		sb.append("a=rtpmap:101 G726-40/8000\r\n");
		sb.append("a=rtpmap:100 G726-32/8000\r\n");
		sb.append("a=rtpmap:99 G726-24/8000\r\n");
		sb.append("a=rtpmap:98 G726-16/8000\r\n");
		sb.append("a=rtpmap:18 G729/8000\r\n");
		sb.append("a=rtpmap:4 G723/8000/8000\r\n");
		sb.append("a=rtpmap:102 AMR/8000\r\n");
		sb.append("a=fmtp:102 octet-align=1\r\n");
		sb.append("a=rtpmap:103 AMR/8000\r\n");
		sb.append("a=rtpmap:9 G722/8000\r\n");
		sb.append("a=ptime:20\r\n");
		sb.append("a=rtpmap:96 telephone-event/8000\r\n");
		sb.append("a=fmtp:96 0-15,32,36\r\n");
		sb.append("a=sendrecv\r\n");
		return sb.toString();
	}

	public static String randomUUID() {
		byte[] randomBytes = new byte[16];
		random.nextBytes(randomBytes);
		randomBytes[6] &= 0x0f; /* clear version */
		randomBytes[6] |= 0x40; /* set to version 4 */
		randomBytes[8] &= 0x3f; /* clear variant */
		randomBytes[8] |= 0x80; /* set to IETF variant */

		long msb = 0;
		long lsb = 0;
		for (int i = 0; i < 8; i++)
			msb = (msb << 8) | (randomBytes[i] & 0xff);
		for (int i = 8; i < 16; i++)
			lsb = (lsb << 8) | (randomBytes[i] & 0xff);

		return (digits(msb >> 32, 2) + digits(msb >> 16, 2) + digits(msb, 2)
				+ digits(lsb >> 48, 2) + digits(lsb, 2));
	}

	private static String digits(long val, int digits) {
		long hi = 1L << (digits * 4);
		return Long.toOctalString(hi | (val & (hi - 1))).substring(1);
	}
}
