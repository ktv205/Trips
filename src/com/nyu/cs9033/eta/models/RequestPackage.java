package com.nyu.cs9033.eta.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestPackage {
	private String URI;
	private String method;
	private Map<String,List> params=new HashMap<String, List>();
	public String getURI() {
		return URI;
	}
	public void setURI(String uRI) {
		URI = uRI;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Map<String, List> getParams() {
		return params;
	}
	public void setParams(Map<String, List> params) {
		this.params = params;
	}
	public void setParam(String string,List list){
		params.put(string, list);
		
	}

}
