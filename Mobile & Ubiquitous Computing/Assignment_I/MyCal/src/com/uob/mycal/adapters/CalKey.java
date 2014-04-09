package com.uob.mycal.adapters;


public class CalKey {
	 
	public enum ContentType {NUM,OPT,EXEC,CLR,PNT,ADVOPT,BRCKT,NON};
	
	int contentText;
	ContentType contentType;

	public CalKey(int txt, ContentType type) {
		contentText = txt;
		contentType = type;
	}

	public int getContentText() {
		return contentText;
	}

	public void setContentText(int contentText) {
		this.contentText = contentText;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}
	
}