package com.sinorail.gysglbj.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseCertificateSupcode<M extends BaseCertificateSupcode<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("ID", id);
	}

	public java.lang.String getId() {
		return get("ID");
	}

	public void setCerid(java.lang.String cerid) {
		set("CERID", cerid);
	}

	public java.lang.String getCerid() {
		return get("CERID");
	}

	public void setCode(java.lang.String code) {
		set("CODE", code);
	}

	public java.lang.String getCode() {
		return get("CODE");
	}

	public void setCodename(java.lang.String codename) {
		set("CODENAME", codename);
	}

	public java.lang.String getCodename() {
		return get("CODENAME");
	}

}
