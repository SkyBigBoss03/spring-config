package com.jt.common.vo;

public class EasyUITreeResult {
	
	private Long id;		//商品分类Id
	private String text;	//商品分类的名称
	private String state;	//节点是否闭合  open/closed
	
	//无参构造必须添加
	public EasyUITreeResult(){
		
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}


	public EasyUITreeResult(Long id, String text, String state) {
		super();
		this.id = id;
		this.text = text;
		this.state = state;
	}
}
