package com.sinorail.gysglbj.action;


import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.sinorail.gysglbj.extend.QuiController;
import com.sinorail.gysglbj.model.Suppliergrade;


public class SupplierGradeAction extends QuiController{
private Logger log = Logger.getLogger(CerSortAction.class);
	
	public void index(){
		render("view.html");
		log.info("跳转到供应商等级评价对应的页面");
	}
	
	/**
	 * 
	 * 供应商等级评价的展示
	 */
	public void list(){ 
		
		Page<Suppliergrade> page = Suppliergrade.dao.findPaginate(pageNumber(), pageSize(),getModel(Suppliergrade.class));
		if(page!=null){
			renderJson(page);
		}
	} 
	/**
	 * 
	 */
	public void addView(){
		
	
		render("save.html");
	}
	
	/**
	 * 修改
	 * 
	 */ 
	public void updateView(){
		
		setAttr("suppliergrade",Suppliergrade.dao.queryById(getPara("id")));
		
		render("save.html");
	}
	
	public  void save(){
			
			boolean status = false;
			
			Suppliergrade suppliergrade  = getModel(Suppliergrade.class,"suppliergrade");
			if (suppliergrade.getId() == null) {
				//添加
				suppliergrade.remove("ID");
				if(!Suppliergrade.dao.isExistgradename(suppliergrade.getGradepj())) {	
					status = suppliergrade.save();
					log.info("****保存供应商等级评价,评价为"+ suppliergrade.getGradepj());
					
				}else {
					setAttr("content", "供应商等级评价已存在!");
				}
			} else {
				//修改 
				try {
					if(!Suppliergrade.dao.isExistgradename(suppliergrade.getGradepj(), suppliergrade.getId())) {	
						status = suppliergrade.update();
					}else {
						setAttr("content", "供应商等级评价已存在!");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			setAttr("message", status ? "保存成功!" : "保存失败!");
			
			renderJson();
		}	

	/**
	 * 供应商等级评价的查看
	 * 
	 */
	public void detailView(){
		
		setAttr("suppliergrade", Suppliergrade.dao.queryById(getPara("id")));
		
		render("detail.html");
		
	}
	
	/**
	 * 供应商等级评价的删除
	 * 
	 */
	public void delete(){
		
		if(Suppliergrade.dao.deleteById(getPara("id"))){
			setAttr("status", 1);
			log.info("****删除供应商等级评价ID="+ getPara("id"));
		}
		renderJson();
	}
	
	
	/**
	 * 
	 * 批量删除
	 */ 
	public void deleteBatch(){
		
		if(Suppliergrade.dao.deleteByIds(getPara("ids"))){
			setAttr("status",1);
			log.info("****删除供应商等级评价ID="+ getPara("ids")); 
		}else{
			setAttr("status",2);
		}
		renderJson();
	}
	
	/**
	 * 
	 * 查询
	 */
	
	public void listsup(){
		
		/*String sql = "select GRADEPJ from E_SUPPLIERGRADE ORDER BY GRADEPJ"; 
		
		List<Suppliergrade> find = Suppliergrade.dao.find(sql);
		
		
		Map<String, List<Map<String,String>>> list = new HashMap<String, List<Map<String,String>>>();
		
		
		List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
		
		for (Suppliergrade sup : find) {
			
			Map<String, String> maps = new HashMap<String, String>();
			
			String gradepj = sup.getGradepj();
			maps.put("key", gradepj);
			maps.put("value", gradepj);
			lists.add(maps);
		}
		list.put("list", lists);
		System.out.println(list);*/
		
		String sql = "select GRADEPJ \"key\", GRADEPJ \"value\" from E_SUPPLIERGRADE ORDER BY GRADEPJ"; 
		
		setAttr("list", Db.find(sql));
		
		renderJson();
		
	} 
	
	public void DjpjList(){
		
		String sql = "select GRADEPJ \"key\",GRADEPJ \"value\" from E_SUPPLIERGRADE ORDER BY GRADEPJ ASC";
		
		renderJson(Suppliergrade.dao.find(sql));
		
	}
}
