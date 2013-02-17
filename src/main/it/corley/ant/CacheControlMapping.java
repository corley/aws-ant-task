package it.corley.ant;

public class CacheControlMapping {
	private String extension;
	private String cacheControl;
	
	public CacheControlMapping() {}
	public CacheControlMapping(String extension, String cacheControl) {
		setExtension(extension);
		setCacheControl(cacheControl);
	}
	
	public void setExtension(String extension) {
		this.extension=extension;
	}
	
	public String getExtension()
	{
		return this.extension;
	}
	
	/**
	 * 
	 * @param cacheControl The cache-control string eg. max-age=3600
	 */
	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}
	
	public String getMaxAge()
	{
		return this.cacheControl;
	}
}
