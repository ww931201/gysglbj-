package com.sinorail.gysglbj.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;


import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.sinorail.gysglbj.extend.QuiController;
import com.sinorail.gysglbj.model.Suppliestemplate;
import com.sinorail.gysglbj.util.ExcelUtils;

/**
 * @author wangwei
 *
 */
public class SuppliestemplateAction extends QuiController{
	
	private Logger log = Logger.getLogger(SuppliestemplateAction.class);
	
	public void index(){
		setAttr("CSQYMC", getPara("CSQYMC"));
		render("view.html");
		log.info("跳转到物资库对应的页面");
	}
	
	/**
	 * 
	 * 物资库的展示
	 * 
	 */
	public void list(){ 
		Page<Suppliestemplate> page = Suppliestemplate.dao.findPaginate(pageNumber(), pageSize(),getModel(Suppliestemplate.class));
		renderJson(page);
	} 
	
	/**
	 * 导入excel跳转页面
	 */
	public void importView(){
		render("importView.html");
	}
	
	/**
	 * 
	 * 导出模板:将需要的模板进行上传到resources--templates--supplier_template.xls(supplier_template.xlsx)
	 * 直接下载模板
	 * @throws IOException 
	 */
	public void export() throws IOException {
		
		String path = Thread.currentThread().getContextClassLoader().getResource("templates/suppliertemplate_templates.xlsx").getPath();
		
		File file = new File(path);
		
		renderFile(file);
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
		
		//Suppliestemplate.dao.updateSWTP(getPara("id"));
		
		setAttr("suppliestemplate",Suppliestemplate.dao.queryById(getPara("id")));
		
		render("save.html");
	}
	
	public  void save(){
			boolean status = false;
			Suppliestemplate suppliestemplate  = getModel(Suppliestemplate.class,"suppliestemplate");
			if (suppliestemplate.getId() == null) {
				//添加
				suppliestemplate.remove("ID");
				/*if(!Suppliestemplate.dao.isExistSupplierCode(suppliestemplate.getWzbm())) {	
					status = suppliestemplate.save();
					log.info("****保存物资库,"+ suppliestemplate.getWzbm());
					
				}else {
					setAttr("content", "物资库编码已存在!");
				}*/
				status = suppliestemplate.save();
				log.info("****保存物资库,"+ suppliestemplate.getWzbm());
				//setAttr("content", "物资库编码已存在!");
				
			} else {
				//修改  
				try {
					/*if(!Suppliestemplate.dao.findByWzbmAndId(suppliestemplate.getWzbm(), suppliestemplate.getId())) {
						
						status = suppliestemplate.update();
					}else {
						setAttr("content", "物资库编码已存在!");
					}*/
						status = suppliestemplate.update();
						//setAttr("content", "物资库编码已存在!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			setAttr("message", status ? "保存成功!" : "保存失败!");
			
			renderJson();
		}	

	/**
	 * 物资库的查看
	 * 
	 */
	public void detailView(){
		
		setAttr("suppliestemplate", Suppliestemplate.dao.queryById(getPara("id")));
		
		render("detail.html");
		
	}
	
	/**
	 * 物资库的删除
	 * 
	 */
	public void delete(){
		
		String sql = "select SWTP from E_SUPPLIESTEMPLATE where ID =?";
		Record record = Db.findFirst(sql,getPara("id"));
		if(record.get("SWTP")!=null){
			File file = new File(this.getSession().getServletContext().getRealPath("") + record.get("SWTP"));
			file.delete();
		}
		boolean deleteResult = Suppliestemplate.dao.deleteById(getPara("id"));
		if(deleteResult == true){
			setAttr("status", 1);
			log.info("****删除物资库ID="+ getPara("id"));
		}else{
			log.info("****删除失败*****");
		}
		renderJson();
	}
	
	/**
	 * 
	 * 批量删除
	 */ 
	public void deleteBatch(){
		
		if(Suppliestemplate.dao.deleteByIds(getPara("ids"))){
			setAttr("status",1);
			log.info("****删除物资库ID="+ getPara("ids")); 
		}else{
			setAttr("status",2);
		}
		renderJson();
		
	}
	
	
	/**
	 * 导入物资库excel数据
	 * @throws IOException
	 */
	public void importExcel() throws IOException {
		
			//String msg  = "0"; // renderText 参数不能为汉字
			
			UploadFile file = getFile("excel");
			
			List<List<Object>> list = null;
			
			String[] field = {"LB","WZBM", "WZMC", "GGXH", "JLDW", "BZ", "QYMC2"};
			try {
				list = ExcelUtils.readExcel(file.getFile(), 1,field.length);
				
			} catch (IOException e1) {
				e1.printStackTrace();
				renderJson("result", "上传失败！"); return;
			}
			
		/*	//EXCEL文件供应商编码重复
			String temp ="";
			List<Object> lists = new ArrayList<Object>();
			int a = 2;
			if(list!=null){
				for (List<Object> fileWzbm : list) { 
					if(lists!=null){
						for (Object object : lists) {
							if (fileWzbm.get(1)!=null) {
								if(fileWzbm.get(1).equals(object)){
									 temp += fileWzbm.get(1)+",";
								}
							}else{
								renderJson("result", "第" + a + "行物资编码为空，请查看修改"); return; 
							}
							if(temp.length()>0){
								renderJson("result", "第" + a + "行物资编码'"+object+"'在excel已经存在，请修改后再导入数据"); return; 
							}
							
						}
					}
					lists.add(fileWzbm.get(1));
					a++;
				}
			}
			
			//数据库已经存在此供应商编码
			String duipler = "";
			
			List<Suppliestemplate> findWzbm = Suppliestemplate.dao.findWzbm();
			
			if(findWzbm!=null){
				for (Suppliestemplate suppliestemplate : findWzbm) {
					String wzbm = suppliestemplate.getWzbm();
					if(list!=null){
						for (int l = 0;list.size()>l;l++) { 
							if(list.get(l).get(1).equals(wzbm)){
								duipler += wzbm+",";
							}
							if(duipler.length()>0){
								renderJson("result", "第" + (l+2) + "行物资编码'"+wzbm+"'在数据库已经存在，请修改后再导入数据"); return; 
							}
						}
					}
				}
			}*/
			
			List<Record> recordList = new LinkedList<Record>();
			
			String[][] fields = {{"LB",".*","类别"},{"WZBM",".*","物资编码"}, {"WZMC",".*","物资名称"}, {"GGXH",".*","规格型号"}, {"JLDW",".*","计量单位"}, {"BZ",".*","备注"}, {"QYMC2",".*","企业名称"}};			
			for(int n = 0;n<list.size();n++){
				
				Record r = new Record();

				for(int i=0; i<fields.length; i++) {
					
					if(fields[i][1] == "WZBM" && (list.get(n).get(i) == null || list.get(n).get(i) == "")){
						renderJson("result","第"+(n+2)+"行"+"第"+(i+1)+"列数据"+list.get(n).get(i)+"格式填写错误！请修改后重新填写！"); return;
					}
					if(list.get(n).get(i) != null) {
						boolean flag = Pattern.matches(fields[i][1], list.get(n).get(i).toString());
						if(!flag){
							renderJson("result","第"+(n+2)+"行"+"第"+(i+1)+"列数据"+fields[i][2]+list.get(n).get(i)+"格式填写错误！请修改后重新填写！"); return;
						}
					}
					r.set(fields[i][0], list.get(n).get(i));
				}
				
				if(r != null) recordList.add(r);
			}
			
			if(!(Db.batchSave("E_SUPPLIESTEMPLATE", recordList, recordList.size()).length > 0) ) {
				renderJson("result", "导入数据失败，请修改后重试！"); return;
			}

			file.getFile().delete();//删除上传的缓存文件
			
			renderJson("result", "0"); 
		}
	
	
	public void exportWzk() throws IOException{
		/*new 一个file文件*/
		File file = new File("suppliertemplate_template.xls");
		
		List<Record> exportList = Suppliestemplate.dao.findExportWzk(getModel(Suppliestemplate.class));
		
		String[] title = {"类别", "物资编码", "物资名称", "规格型号", "计量单位","备注","企业名称"};
		
		String[] field = {"LB", "WZBM", "WZMC", "GGXH", "JLDW", "BZ","QYMC2"};
		
		ExcelUtils.export(exportList, title, field, file); 
		 
		renderFile(file);
	}
	
	/**
	 * 上传图片
	 */
	public void uploadPic(){
	
		List<UploadFile> files = getFiles();
		
		List<String> lists = new ArrayList<String>();
	 	
		if(files!=null){
			for (UploadFile filenames : files) {
				String fileName = filenames.getOriginalFileName();
				File source = new File(filenames.getUploadPath() + "\\" + fileName); // 获取临时文件对象
			    String extension = fileName.substring(fileName.lastIndexOf("."));
			    String savePath = PathKit.getWebRootPath() + "/upload/pic/";
			    JSONObject json = new JSONObject();
			    if (".png".equalsIgnoreCase(extension) || ".jpg".equalsIgnoreCase(extension)
			            || ".gif".equalsIgnoreCase(extension) || "jpeg".equalsIgnoreCase(extension)
			            || "bmp".equalsIgnoreCase(extension)) {
			        fileName = UUID.randomUUID().toString().replaceAll("-", "") + extension;
			        try {
			            FileInputStream fis = new FileInputStream(source);
			            File targetDir = new File(savePath);
			            if (!targetDir.exists()) {
			                targetDir.mkdirs();
			            }
			            File target = new File(targetDir, fileName);
			            if (!target.exists()) {
			                target.createNewFile();
			            }
			            FileOutputStream fos = new FileOutputStream(target);
			            byte[] bts = new byte[1024 * 20];
			            while (fis.read(bts, 0, 1024 * 20) != -1) {
			                fos.write(bts, 0, 1024 * 20);
			            }
			            fos.close();
			            fis.close();
			            json.put("error", "0");
			            json.put("src", "/upload/pic/" + fileName); // 相对地址，显示图片用
			            source.delete();
			        } catch (FileNotFoundException e) {
			            json.put("error", "1");
			            json.put("message", "上传出现错误，请稍后再上传");
			        } catch (IOException e) {
			            json.put("error", "1");
			            json.put("message", "文件写入服务器出现错误，请稍后再上传");
			        }
			    } else {
			        source.delete();
			        json.put("error", "1");
			        json.put("message", "只允许上传png,jpg,jpeg,gif,bmp类型的图片文件");
			    }
			    lists.add(json.toJSONString());
			}
		}
		renderJson(lists.toString());
	}
	
	/*	UploadFile filenames = getFile("images");
		String fileName = filenames.getOriginalFileName();
		File source = new File(filenames.getUploadPath() + "\\" + fileName); // 获取临时文件对象
	    String extension = fileName.substring(fileName.lastIndexOf("."));
	    String savePath = PathKit.getWebRootPath() + "/upload/wzpic/";
	    JSONObject json = new JSONObject();
	    if (".png".equals(extension) || ".jpg".equals(extension)
	            || ".gif".equals(extension) || "jpeg".equals(extension)
	            || "bmp".equals(extension)) {
	        fileName = UUID.randomUUID().toString().replaceAll("-", "") + extension;
	        try {
	            FileInputStream fis = new FileInputStream(source);
	            File targetDir = new File(savePath);
	            if (!targetDir.exists()) {
	                targetDir.mkdirs();
	            }
	            File target = new File(targetDir, fileName);
	            if (!target.exists()) {
	                target.createNewFile();
	            }
	            FileOutputStream fos = new FileOutputStream(target);
	            byte[] bts = new byte[1024 * 20];
	            while (fis.read(bts, 0, 1024 * 20) != -1) {
	                fos.write(bts, 0, 1024 * 20);
	            }
	            fos.close();
	            fis.close();
	            json.put("error", 0);
	            json.put("src", "/upload/wzpic/" + fileName); // 相对地址，显示图片用
	            source.delete();
	        } catch (FileNotFoundException e) {
	            json.put("error", 1);
	            json.put("message", "上传出现错误，请稍后再上传");
	        } catch (IOException e) {
	            json.put("error", 1);
	            json.put("message", "文件写入服务器出现错误，请稍后再上传");
	        }
	    } else {
	        source.delete();
	        json.put("error", 1);
	        json.put("message", "只允许上传png,jpg,jpeg,gif,bmp类型的图片文件");
	    }
	    renderJson(json.toJSONString());
		}*/
}
