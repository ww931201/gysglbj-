package com.sinorail.gysglbj.action;

import java.io.File;
import java.io.IOException;
import java.util.List;


import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.sinorail.gysglbj.extend.QuiController;
import com.sinorail.gysglbj.model.Certificate;
import com.sinorail.gysglbj.model.CertificateSupcode;
import com.sinorail.gysglbj.model.Supplier;
import com.sinorail.gysglbj.util.ExcelUtils;

/**
 * @author wangwei
 *
 */
public class JoinlistAction extends QuiController{
	
	private Logger log = Logger.getLogger(JoinlistAction.class);
	
	public void index(){
		
		render("view.html");
		log.info("跳转到综合查询对应的页面");
		
	}
	
	/**
	 * 
	 * 综合查询的展示
	 */
	public void list(){ 
		
		Page<Supplier> page = Supplier.dao.findPaginateByZhcx(pageNumber(), pageSize(),getModel(Supplier.class,"supplier"),getModel(Certificate.class,"certificate"),getModel(CertificateSupcode.class,"certificatesupcode"));

		renderJson(page);
		 
	} 
	
	public void exportGysAndCer() throws IOException{
		/*new 一个file文件*/
		File file = new File("supplierAndCerAndCERCODE.xls");
		
		List<Record> exportList = Supplier.dao.findExportSupAndCerAndCode(getModel(Supplier.class,"supplier"),getModel(Certificate.class,"certificate"),getModel(CertificateSupcode.class,"certificatesupcode"));
		
		String[] title = {"供应商编码", "企业名称", "供应商分类", "证书编号","证书名称","证书类别","物资编码","物资名称"};
		
		String[] field = {"GYSBH", "QYMC", "GYSFL", "NO", "NAME", "ZSLB","CODE","WZMC"};
		
		ExcelUtils.export(exportList, title, field, file); 
		 
		renderFile(file);
	}
	
}

