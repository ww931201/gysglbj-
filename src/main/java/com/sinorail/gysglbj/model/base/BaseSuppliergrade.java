package com.sinorail.gysglbj.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseSuppliergrade<M extends BaseSuppliergrade<M>> extends Model<M> implements IBean {

	public void setGradepj(java.lang.String gradepj) {
		set("GRADEPJ", gradepj);
	}

	public java.lang.String getGradepj() {
		return get("GRADEPJ");
	}

	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}

	public void setDjpjsm(java.lang.String djpjsm) {
		set("DJPJSM", djpjsm);
	}

	public java.lang.String getDjpjsm() {
		return get("DJPJSM");
	}

}
