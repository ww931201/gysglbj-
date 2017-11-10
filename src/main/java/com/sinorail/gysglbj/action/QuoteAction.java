package com.sinorail.gysglbj.action;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.aop.Duang;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;
import com.sinorail.gysglbj.action.service.QuoteService;
import com.sinorail.gysglbj.extend.QuiController;
import com.sinorail.gysglbj.model.Project;
import com.sinorail.gysglbj.model.Quote;
import com.sinorail.gysglbj.model.Supplier;
import com.sinorail.gysglbj.util.ExcelUtils;
/**
 * 
 * @author wangwei
 *
 */
public class QuoteAction extends QuiController {
	
	QuoteService quoteService = Duang.duang(QuoteService.class);
	
	public void index() {
		render("view.html");
	}
	public void addView() {
		render("save.html");
	}
	public void updateView() {
		setAttr("quote", Quote.dao.findById(getPara("id")));
		render("save.html");
	}
	public void detailView() {
		setAttr("quote", Quote.dao.findById(getPara("id")));
		render("detail.html");
	}
	public void importView() {
		setAttr("projectId", getPara("projectId"));
		render("importView.html");
	}
	public void filterView() {
		
		setAttr("projectId", getPara("projectId"));
		Project project = Project.dao.findById(getPara("projectId"));
		setAttr("projectName", project.getName());
		//类型 1 竞买(整包)  2 竞价
		if(project.getType().equals(new BigDecimal("1"))) {
			render("filterJJView.html");
		} else {
			render("filterView.html");
		}
	}
	
	public void list() {
		Quote quote = getModel(Quote.class);
		Supplier supplier = getModel(Supplier.class);
		Kv kv = Kv.by("WZBM", quote.getWzbm()).set("GYSBH", supplier.getGysbh()).set("WZMC",quote.getWzmc()).set("PROJECT_ID",quote.getProjectId()).set("GGXH", quote.getGgxh()).set("SFYXBJ", quote.getSfyxbj()).set("QYMC", supplier.getQymc());
		SqlPara sqp = Db.getSqlPara("quote.paginateList", kv);
	/*	List<Record> list = paginate.getList();
		kv.set("PROJECT_ID",list.get(0).get("PROJECT_ID"));*/
		renderJson(Db.paginate(pageNumber(), pageSize(), sqp));
	}
	
	public void filterList() {
		
		String xh = getPara("XH");
		Quote quote = getModel(Quote.class);
		Supplier supplier = getModel(Supplier.class);
		Kv kv = Kv.by("WZBM", quote.getWzbm()).set("WZMC",quote.getWzmc()).set("XH", xh).set("GGXH", quote.getGgxh()).set("PROJECT_ID",quote.getProjectId()).set("SFYXBJ", quote.getSfyxbj()).set("QYMC", supplier.getQymc()).set("GYSBH", supplier.getGysbh()).set("BJH", quote.getBjh());
		SqlPara sqp;
		//类型 1 竞买(整包)  2 竞价
		Project project = Project.dao.findById(quote.getProjectId());
		if(project.getType().equals(new BigDecimal("1"))) {
			sqp = Db.getSqlPara("quote.filterJJList", kv);
		}else{
			sqp = Db.getSqlPara("quote.filterList", kv);
		}
		renderJson("rows", Db.find(sqp));
	}
	
	public void save() {
		boolean status = false;
		Quote project = getModel(Quote.class);
		if (project.getId() == null) {
			project.remove("ID");
				status = project.save();
		} else {
				status = project.update();
		}
		if(!status) {			
			setAttr("message","保存失败!");
		}
		renderJson();
	} 
	
	public void delete() {
		if(Quote.dao.deleteById(getPara("id"))) {
			setAttr("status", 1);
		}
		renderJson();
	}
	
	@Before(Tx.class)
	public void deleteBatch() {
		String[] idArray = getPara("ids").split(",");
		for (String id : idArray) {
			if(Quote.dao.deleteById(id)) {
				setAttr("status", 1);
			}
		}
		renderJson();
	}
	
	public void importExcel() throws IOException {
		
		UploadFile file = getFile("excel");
		
		List<List<Object>> list = null;
		//String[][] field = {{"BJH","^[0-9]+(.[0]{1,2})?$","包件号"}, {"WZBM",".*","物资编码"}, {"WZMC",".*","物资名称"}, {"GGXH",".*","规格型号"}, {"JSYQ",".*","技术要求"}, {"JLDW",".*","计量单位"}, {"YCSL","^[0-9]+(.[0-9]{1,2})?$","预测数量"}, {"DJXJ_BHS","^[0-9]+(.[0-9]{1,2})?$","单价限价(不含税）"}, {"ZXJ_BHS","^[0-9]+(.[0-9]{1,2})?$","总限价（不含税）"}, {"SYDWJDQ",".*","使用单位及地区"}, {"CSBDJ_BHS","^[0-9]+(.[0-9]{1,2})?$","厂商报单价(不含税）"}, {"CSBZXJ_BHS","^[0-9]+(.[0-9]{1,2})?$","厂商报总限价（不含税）"}};
		String[][] field = {{"BJH","^[0-9]+(.[0]{1,2})?$","包件号"}, {"WZBM",".*","物资编码"}, {"WZMC",".*","物资名称"}, {"GGXH",".*","规格型号"}, {"JSYQ",".*","技术要求"}, {"JLDW",".*","计量单位"}, {"YCSL","^[0-9]+(.[0-9]{1,2})?$","预测数量"}, {"DJXJ_BHS","^[0-9]+(.[0-9]{1,2})?$","单价限价(不含税）"}, {"ZXJ_BHS","^[0-9]+(.[0-9]{1,2})?$","总限价（不含税）"}, {"SYDWJDQ",".*","使用单位及地区"}, {"CSBDJ_BHS",".*","厂商报单价(不含税）"}, {"CSBZXJ_BHS",".*","厂商报总限价（不含税）"}};
		try {
			
			list = ExcelUtils.readExcel(file.getFile(), 4);
			
		} catch (IOException e1) {
			
			e1.printStackTrace();
			renderJson("msg", "上传失败!"); return;
			
		}
		
		/**判断excel的list中物资库中是不是存在物资编码*/
		
		/*int j = 5;
		if(list!=null){
			for (List<Object> fileWzbm : list) { 
				String  gyswzbm = (String) fileWzbm.get(1);
				String sql = "select * from E_SUPPLIESTEMPLATE where WZBM = ?";
				List<Suppliestemplate> suppliestemplateList = Suppliestemplate.dao.find(sql,gyswzbm);
				if(suppliestemplateList.size() < 1){
					renderJson("msg", "第"+j+"行,物资编码 "+gyswzbm+"不在模板物资库中,无法导入！"); return; 
				}
				j++;
			}
		}*/
		
		String projectId = getPara("projectId");
		Object supplierNo = null;
		try {
			 supplierNo = ExcelUtils.getCellValueFromExcel(file.getFile(), 0, 1);
		} catch (Exception e) {
			e.printStackTrace();
			renderJson("msg", "上传失败！"); return;
		}
		
		//System.out.println("*********"+supplierId+"************");
		Supplier supplier = Supplier.dao.findFirst("select * from e_supplier where upper(GYSBH) = upper(?)", supplierNo);
		
			if(supplier == null) {renderJson("msg", "检查文件中供应商编码是否填写,或 系统中是否录入该供应商!"); return;}
		
			Quote quote = Quote.dao.findFirst("select * from e_quote where project_id = ? and supplier_id = ?", projectId, supplier.getId());
		
			if(quote != null) {renderJson("msg", "已导入该供应商信息!"); return;}
		
			/*List<List<Object>> list = null;
		
		//String[] field = {"BJH", "WZBM", "WZMC", "GGXH", "JSYQ", "JLDW", "YCSL", "DJXJ_BHS", "ZXJ_BHS", "SYDWJDQ", "CSBDJ_BHS", "CSBZXJ_BHS"};
		String[][] field = {{"BJH","^[0-9]+(.[0]{1,2})?$","包件号"}, {"WZBM",".*","物资编码"}, {"WZMC",".*","物资名称"}, {"GGXH",".*","规格型号"}, {"JSYQ",".*","技术要求"}, {"JLDW",".*","计量单位"}, {"YCSL","^[0-9]+(.[0-9]{1,2})?$","预测数量"}, {"DJXJ_BHS","^[0-9]+(.[0-9]{1,2})?$","单价限价(不含税）"}, {"ZXJ_BHS","^[0-9]+(.[0-9]{1,2})?$","总限价（不含税）"}, {"SYDWJDQ",".*","使用单位及地区"}, {"CSBDJ_BHS","^[0-9]+(.[0-9]{1,2})?$","厂商报单价(不含税）"}, {"CSBZXJ_BHS","^[0-9]+(.[0-9]{1,2})?$","厂商报总限价（不含税）"}};
		
		try {
			
			list = ExcelUtils.readExcel(file.getFile(), 4);
			
		} catch (IOException e1) {
			
			e1.printStackTrace();
			renderJson("msg", "上传失败!"); return;
			
		}
		
		//判断excel的list中物资库中是不是存在物资编码
		int jj = 5;
		if(list!=null){
			for (List<Object> fileWzbm : list) { 
				String  gyswzbm = (String) fileWzbm.get(1);
				String sql = "select * from E_SUPPLIESTEMPLATE where WZBM = ?";
				List<Suppliestemplate> suppliestemplateList = Suppliestemplate.dao.find(sql,gyswzbm);
				if(suppliestemplateList.size() < 1){
					renderJson("result", "第"+jj+"行,物资编码 "+gyswzbm+"不在模板物资库中,无法导入！"); return; 
				}
				jj++;
			}
		}
		*/
		List<Record> recordList = new LinkedList<Record>();
		
		int rows = 1;
		boolean temp_is_stop = false;
		
		for (List<Object> listm : list) {
			
			Record r = new Record().set("PROJECT_ID", projectId).set("SUPPLIER_ID", supplier.getId());
			
			for(int i=0; i<field.length; i++) {
				
				if(field[i][0] == "WZBM") {
					if(listm.get(i) == null || listm.get(i).toString().trim().equals("")){						
						temp_is_stop = true;
						break;
					}
				}
				if(listm.get(i) != null && !listm.get(i).toString().matches(field[i][1])) {
					if(listm.get(i).toString().startsWith("合")) {
						temp_is_stop = true;
						break;
					}else {						
						renderJson("msg", "第"+(rows+4)+"行，"+field[i][2]+"："+listm.get(i)+" 规则不匹配！"); return;
					}
				}
				r.set(field[i][0], listm.get(i));
				 if(listm!=null){
					 if(listm.get(7)!=null && listm.get(8)!=null&&listm.get(10)!=null&&listm.get(11)!=null){
							if(Double.parseDouble((listm.get(10).toString())) > Double.parseDouble((listm.get(7).toString())) || Double.parseDouble((listm.get(11).toString())) > Double.parseDouble((listm.get(8).toString())) || Double.parseDouble((listm.get(10).toString()))==0 || Double.parseDouble((listm.get(11).toString())) == 0){
								r.set("SFYXBJ", "否");
							}else{
								r.set("SFYXBJ", "是");
							}
						}
				 }
				System.out.println(field[i][0]+": "+listm.get(i));
			}
			if(temp_is_stop) break;
			if(r != null) recordList.add(r);
			
			rows++;
		}
		
		if(!(Db.batchSave("E_QUOTE", recordList, recordList.size()).length > 0) ) {
			
			renderJson("msg", "导入失败!"); return;
		}
		
		file.getFile().delete(); //删除上传的缓存文件
		
		renderJson("msg", "导入成功！");
	}

	/**
	 * 导出数据过滤结果
	 * @throws IOException 
	 */
	public void filterDataExport() throws IOException {
		
		Quote quote = getModel(Quote.class);
		Supplier supplier = getModel(Supplier.class);
		Kv kv = Kv.by("WZBM", quote.getWzbm()).set("WZMC",quote.getWzmc()).set("GGXH", quote.getGgxh()).set("PROJECT_ID",quote.getProjectId()).set("SFYXBJ", quote.getSfyxbj()).set("QYMC", supplier.getQymc()).set("GYSBH", supplier.getGysbh()).set("BJH", quote.getBjh()).set("XH",getPara("XH"));
		File file = new File("filter.xls");
		
		//类型 1 竞买(整包)  2 竞价
		Project project = Project.dao.findById(quote.getProjectId());
		if(project.getType().equals(new BigDecimal("1"))) {
			
			SqlPara sqp = Db.getSqlPara("quote.filterJJList", kv);
			List<Record> list = Db.find(sqp);
			
			String[] title = {"供应商编号", "企业名称", "总限价（不含税）", "厂商报总限价（不含税）","有效报价"};
			String[] field = {"GYSBH", "QYMC", "ZXJ_BHS", "CSBZXJ_BHS","SFYXBJ"};
			
			ExcelUtils.export(list, title, field, file);
		} else { 
			
			SqlPara sqp = Db.getSqlPara("quote.filterList", kv);
			List<Record> list = Db.find(sqp);
			
			String[] title = {"包件号", "供应商编号", "企业名称", "物资编码", "物资名称", "规格型号", "技术要求", "计量单位", "预测数量", "单价限价(不含税）", "总限价（不含税）", "使用单位及地区", "厂商报单价(不含税）", "厂商报总限价（不含税）","有效报价"};
			String[] field = {"BJH", "GYSBH", "QYMC", "WZBM", "WZMC", "GGXH", "JSYQ", "JLDW", "YCSL", "DJXJ_BHS", "ZXJ_BHS", "SYDWJDQ", "CSBDJ_BHS", "CSBZXJ_BHS","SFYXBJ"};
			
			ExcelUtils.export(list, title, field, file);
		}
		
		renderFile(file);
	}
	
	
	/*
	 * 
	 * 导出在竞买模式下导出详细信息
	 */
	public void filterDataExport2() throws IOException {
		
		Quote quote = getModel(Quote.class,"quote");
		Supplier supplier = getModel(Supplier.class);
		Kv kv = Kv.by("WZBM", quote.getWzbm()).set("WZMC",quote.getWzmc()).set("QYMC",supplier.getQymc()).set("GGXH", quote.getGgxh()).set("PROJECT_ID",quote.getProjectId()).set("SFYXBJ", quote.getSfyxbj()).set("QYMC", supplier.getQymc()).set("GYSBH", supplier.getGysbh()).set("BJH", quote.getBjh());
		File file = new File("filter.xls");
		
		//类型 1 竞买(整包)  2 竞价
		//Project project = Project.dao.findById(quote.getProjectId());
		/*if(project.getType().equals(new BigDecimal("1"))) {
			
			SqlPara sqp = Db.getSqlPara("quote.filterJJList", kv);
			List<Record> list = Db.find(sqp);
			
			String[] title = {"供应商编号", "企业名称", "总限价（不含税）", "厂商报总限价（不含税）"};
			String[] field = {"GYSBH", "QYMC", "ZXJ_BHS", "CSBZXJ_BHS"};
			
			ExcelUtils.export(list, title, field, file);
		} else {*/
			
			SqlPara sqp = Db.getSqlPara("quote.filterList2", kv);
			List<Record> list = Db.find(sqp);
			
			String[] title = {"包件号", "供应商编号", "企业名称", "物资编码", "物资名称", "规格型号", "技术要求", "计量单位", "预测数量", "单价限价(不含税）", "总限价（不含税）", "使用单位及地区", "厂商报单价(不含税）", "厂商报总限价（不含税）", "有效报价"};
			String[] field = {"BJH", "GYSBH", "QYMC", "WZBM", "WZMC", "GGXH", "JSYQ", "JLDW", "YCSL", "DJXJ_BHS", "ZXJ_BHS", "SYDWJDQ", "CSBDJ_BHS", "CSBZXJ_BHS","SFYXBJ"};
			
			ExcelUtils.export(list, title, field, file);
		//}
		renderFile(file);
	}
}
