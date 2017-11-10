package com.sinorail.gysglbj.action;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Page;
import com.sinorail.gysglbj.extend.QuiController;
import com.sinorail.gysglbj.model.Cersort;

/**
 * 
 * 证书类别的Action
 * 
 * @author wangwei
 *
 */
public class CerSortAction extends QuiController{
	
	private Logger log = Logger.getLogger(CerSortAction.class);
	
	public void index(){
		
		render("view.html");
		log.info("跳转到证书类别对应的页面");
	}
	
	/**
	 * 
	 * 证书类别的展示
	 */
	public void list(){ 
		
		Page<Cersort> page = Cersort.dao.findPaginate(pageNumber(), pageSize(),getModel(Cersort.class));
		
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
		
		setAttr("cersort",Cersort.dao.queryById(getPara("id")));
		
		render("save.html");
	}
	
	public  void save(){
			
			boolean status = false;
			Cersort cersort  = getModel(Cersort.class,"cersort");
			if (cersort.getId() == null) {
				//添加
				cersort.remove("ID");
				if(!Cersort.dao.isExistSortname(cersort.getCersortname())) {	
					status = cersort.save();
					log.info("****保存证书类别,类别为"+ cersort.getCersortname());
					
				}else {
					setAttr("content", "资质证书类别已存在!");
				}
			} else {
				//修改 
				try {
					if(!Cersort.dao.isExistSortname(cersort.getCersortname(), cersort.getId())) {	
						status = cersort.update();
					}else {
						setAttr("content", "资质证书类别已存在!");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			setAttr("message", status ? "保存成功!" : "保存失败!");
			
			renderJson();
		}	

	/**
	 * 证书类别的查看
	 * 
	 */
	public void detailView(){
		
		setAttr("cersort", Cersort.dao.queryById(getPara("id")));
		
		render("detail.html");
		
	}
	
	/**
	 * 证书类别的删除
	 * 
	 */
	public void delete(){
		
		if(Cersort.dao.deleteById(getPara("id"))){
			setAttr("status", 1);
			log.info("****删除证书类别ID="+ getPara("id"));
		}
		
		renderJson();
	}
	
	
	/**
	 * 
	 * 批量删除
	 */ 
	public void deleteBatch(){
		
		if(Cersort.dao.deleteByIds(getPara("ids"))){
			setAttr("status",1);
			log.info("****删除证书类别ID="+ getPara("ids")); 
		}else{
			setAttr("status",2);
		}
		renderJson();
		
	}
	
	public void ZslblList(){
		
		String sql = "select CERSORTNAME \"key\",CERSORTNAME \"value\" from E_CERSORT";
		
		renderJson(Cersort.dao.find(sql));
		
	}
}
