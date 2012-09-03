package it.corley.ant;

public class CacheControlMapping {
	private String extension;
	private String maxAge;
	
	public CacheControlMapping() {}
	public CacheControlMapping(String extension, String maxAge) {
		setExtension(extension);
		setMaxAge(maxAge);
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
	 * @param maxAge
	 * 
	 * throw NumberFormatException
	 */
	public void setMaxAge(String maxAge) {
		int intMaxAge = Integer.valueOf(maxAge);
		this.maxAge = String.valueOf(intMaxAge);
	}
	
	public String getMaxAge()
	{
		return this.maxAge;
	}
}
