package it.corley.ant;

public class ContentEncodingMapping {
	private String extension;
	private String encoding;
	
	public ContentEncodingMapping() {}
	public ContentEncodingMapping(String extension, String encoding) {
		setExtension(extension);
		setEncoding(encoding);
	}
	
	public void setExtension(String extension) {
		this.extension=extension;
	}
	
	public String getExtension()
	{
		return this.extension;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public String getEncoding()
	{
		return this.encoding;
	}
}
