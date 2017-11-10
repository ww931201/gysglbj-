package com.sinorail.gysglbj.action;


import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Page;
import com.sinorail.gysglbj.extend.QuiController;
import com.sinorail.gysglbj.model.Suppliersort;

/**
 * 
 * 供应商分类Action
 * @author wangwei
 *
 */
public class SupplierSortAction extends QuiController{
private Logger log = Logger.getLogger(CerSortAction.class);
	


	public void index(){
		
		render("view.html");
		
		log.info("跳转到供应商分类对应的页面");
	}
	
	/**
	 * 
	 * 供应商分类的展示
	 */
	public void list(){ 
		
		Page<Suppliersort> page = Suppliersort.dao.findPaginate(pageNumber(), pageSize(),getModel(Suppliersort.class));
		
		renderJson(page);
		
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
		
		setAttr("suppliersort",Suppliersort.dao.queryById(getPara("id")));
		
		render("save.html");
	}
	
	public  void save(){
			
			boolean status = false;
			Suppliersort suppliersort  = getModel(Suppliersort.class,"suppliersort");
			if (suppliersort.getId() == null) {
				//添加
				suppliersort.remove("ID");
				if(!Suppliersort.dao.isExistGysSortname(suppliersort.getGyssortname())) {	
					status = suppliersort.save();
					log.info("****保存供应商分类,类别为"+ suppliersort.getGyssortname());
					
				}else {
					setAttr("content", "资质供应商分类已存在!");
				}
			} else {
				//修改  
				try {
					if(!Suppliersort.dao.isExistGysSortname(suppliersort.getGyssortname(), suppliersort.getId())) {	
						status = suppliersort.update();
					}else {
						setAttr("content", "资质供应商分类已存在!");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			setAttr("message", status ? "保存成功!" : "保存失败!");
			
			renderJson();
		}	

	/**
	 * 供应商分类的查看
	 * 
	 */
	public void detailView(){
		
		setAttr("suppliersort", Suppliersort.dao.queryById(getPara("id")));
		
		render("detail.html");
		
	}
	
	/**
	 * 供应商分类的删除
	 * 
	 */
	public void delete(){
		
		if(Suppliersort.dao.deleteById(getPara("id"))){
			setAttr("status", 1);
			log.info("****删除供应商分类ID="+ getPara("id"));
		}
		
		renderJson();
	}
	
	
	/**
	 * 
	 * 批量删除
	 */ 
	public void deleteBatch(){
		
		if(Suppliersort.dao.deleteByIds(getPara("ids"))){
			setAttr("status",1);
			log.info("****删除供应商分类ID="+ getPara("ids")); 
		}else{
			setAttr("status",2);
		}
		renderJson();
		
	}
	
	/**
	 * 
	 * 供应商分类
	 */
	public void GysflList(){
		
		String sql = "select GYSSORTNAME \"key\",GYSSORTNAME \"value\" from E_SUPPLIERSORT";
		
		setAttr("list", Suppliersort.dao.find(sql));
		
		renderJson();
	}
}

