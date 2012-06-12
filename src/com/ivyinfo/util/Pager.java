package com.ivyinfo.util;

public class Pager {

	private String PageUrl; // url
	private boolean hasNext; // 是否有下一页
	private boolean hasPrevious; // 是否有上一页
	private String nextPage; // 下一页
	private String previousPage; // 上一页
	private int offset; // 当前页
	private int size; // 总数据量
	private int length; // 每页显示多少条数据
	private int pagenumber; // 共有几页

	public Pager(int offset, int length, int size, String url) {
		this.offset = offset;
		this.length = length;
		this.size = size;
		int index = url.indexOf("&pager.offset");
		if (index > -1) {
			this.PageUrl = url.substring(0, index);
		} else {
			this.PageUrl = url;
		}

	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setPagerUrl(String PagerUrl) {
		this.PageUrl = PagerUrl;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getOffset() {
		return this.offset;
	}

	public String getPageUrl() {
		return this.PageUrl;
	}

	public boolean getHasNext() {
		if (offset * length + 1 > size) {
			hasNext = false;
		} else {
			hasNext = true;
		}
		return hasNext;
	}

	public boolean getHasPrevious() {
		if (offset > 1) {
			this.hasPrevious = true;
		} else {
			this.hasPrevious = false;
		}
		return hasPrevious;
	}

	public String getPreviousPage() {
		this.previousPage = "";
		if (this.getHasPrevious()) {
			this.previousPage = this.PageUrl + "?offset=" + (offset - 1);
		}
		return previousPage;
	}

	public String getNextPage() {
		this.nextPage = "";
		if (this.getHasNext()) {
			this.nextPage = this.PageUrl + "?offset=" + (offset + 1);
		}
		return this.nextPage;
	}

	public int getPageNumber() {
		float temppn = (float) size / (float) length;
		pagenumber = new Float(temppn).intValue();
		if (temppn > pagenumber) {
			this.pagenumber++;
		}
		return this.pagenumber;
	}
}
