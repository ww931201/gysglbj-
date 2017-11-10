package com.sinorail.gysglbj.action;

import java.io.File;
import java.io.IOException;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.sinorail.gysglbj.constant.Const;
import com.sinorail.gysglbj.extend.QuiController;
import com.sinorail.gysglbj.model.Project;
import com.sinorail.gysglbj.util.ExcelUtils;

public class ProjectAction extends QuiController {

	public void index() {
		render("view.html");
	}
	public void addView() {
		render("save.html");
	}
	public void updateView() {
		setAttr("project", Project.dao.findById(getPara("id")));
		render("save.html");
	}
	public void detailView() {
		setAttr("project", Project.dao.findById(getPara("id")));
		render("detail.html");
	}
	
	public void list() {
		SqlPara sqp = Db.getSqlPara("project.paginateList", getModel(Project.class));
		renderJson(Db.paginate(pageNumber(), pageSize(), sqp));
	}
	
	/**
	 * 导出模板
	 * @throws IOException 
	 */
	public void export() throws IOException {
		String projectId = getPara("id");
		Project project = Project.dao.findById(projectId);
		
		File file = new File(Const.temp_file_path+project.getName()+project.getNo()+".xls");
		String path = Thread.currentThread().getContextClassLoader().getResource("templates/quote_template.xls").getPath();
		renderFile(ExcelUtils.replaceCell(new File(path), 1, 0, "项目名称："+project.getName()+" 项目编号："+project.getNo(), file));
	}
	
	public void save() {
		boolean status = false;
		Project project = getModel(Project.class);
		if (project.getId() == null) {
			project.remove("ID");
			project.setCreaterId(getSessionUser().getId());
			if(null == Project.dao.findFirst(Db.getSql("project.isDuplicated"), project.getNo())) {				
				status = project.save();
			}else {
				setAttr("message","项目编号重复!");renderJson();return;
			}
		} else {
			status = project.update();
		}
		if(!status) {			
			setAttr("message","保存失败!");
		}
		renderJson();
	} 
	
	public void delete() {
		if(Project.dao.deleteById(getPara("id"))) {
			setAttr("status", 1);
		}
		renderJson();
	}
	
}
