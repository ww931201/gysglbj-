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
import com.sinorail.gysglbj.model.Certificate;
import com.sinorail.gysglbj.model.CertificateSupcode;
import com.sinorail.gysglbj.model.Quote;
import com.sinorail.gysglbj.model.Supplier;
import com.sinorail.gysglbj.model.Suppliergrade;
import com.sinorail.gysglbj.model.Suppliersort;
import com.sinorail.gysglbj.util.ExcelUtils;


/**
 * 
 * 
 * 供应商Action
 * 
 * @author wangwei
 *
 */
public class GysglAction extends QuiController {
	
	private Logger log = Logger.getLogger(GysglAction.class);

	public void index() {
		render("view.html");
	}
	
	/*添加弹出框进行保存*/
	public void addView(){
		
		render("save.html");
	}
	
	/**
	 * 导入excel跳转页面
	 */
	public void importView(){
		render("importView.html");
	}
	
	/**
	 * 导入供应商excel数据
	 * @throws IOException
	 */
	public void importExcel() throws IOException {
		
			//String msg  = "0"; // renderText 参数不能为汉字
			
			UploadFile file = getFile("excel");
			
			List<List<Object>> list = null;
			
			String[] field = {"GYSBH", "SHXYDM", "YYZZZCH", "QYMC", "FDDBR", "FDDBRDH", "SSS", "SSS1", "ZS", "ZCZB", "CLRQ", "YYQX", "QYLX", "ZZJGDM", "SWDJH", "YXQ", "YWLXR", "LXRSJ", "BGCZ", "BGDH", "LXRYX", "LXRZW", "BGDZ", "BLGYSCFZQ", "HMD", "BLGYSXYPJDJ", "GYSJYFW","GYSFL","GYSQYXZ" };
			try {
				list = ExcelUtils.readExcel(file.getFile(), 1,field.length);
				
			} catch (IOException e1) {
				e1.printStackTrace();
				renderJson("result", "上传失败！"); return;
			}
			//EXCEL文件供应商编码重复
			String temp ="";
			List<Object> lists = new ArrayList<Object>();
			int a = 2;
			if(list!=null){
				for (List<Object> fileGysbh : list) { 
					if(lists!=null){
						for (Object object : lists) {
							if(fileGysbh.get(0).equals(object)){
								 temp += fileGysbh.get(0)+",";
							}
							if(temp.length()>0){
								renderJson("result", "第" + a + "行供应商编码'"+object+"'在excel已经存在，请修改后再导入数据"); return; 
							}
							
						}
					}
					lists.add(fileGysbh.get(0));
					a++;
				}
			}
			
			/*if(temp.length()>0){
				renderText("5"); return;
			}*/
			//数据库已经存在此供应商编码
			String duipler = "";
			List<Supplier> findGysbh = Supplier.dao.findGysbh();
			if(findGysbh!=null){
				for (Supplier supplier : findGysbh) {
					String gysbh = supplier.getGysbh();
					
					if(list!=null){
						for (int l = 0;list.size()>l;l++) { 
							
							if(list.get(l).get(0).equals(gysbh)){
								duipler += gysbh+",";
							}
							if(duipler.length()>0){
								renderJson("result", "第" + (l+2) + "行供应商编码'"+gysbh+"'在数据库已经存在，请修改后再导入数据"); return; 
							}
						}
					}
				}
			}
			
			List<Record> recordList = new LinkedList<Record>();
			
			String[][] fields = {{"GYSBH",".*","供应商编号"}, {"SHXYDM",".*","社会信用代码"}, {"YYZZZCH",".*","营业执照注册号"}, {"QYMC",".*","企业名称"}, {"FDDBR",".*","法定代表人"}, {"FDDBRDH",".*","法定代表人电话"}, {"SSS",".*","所属省"}, {"SSS1",".*","所属市"}, {"ZS",".*","住所"}, {"ZCZB",".*","注册资本"}, {"CLRQ","(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)","成立日期"}, {"YYQX",".*","营业期限"}, {"QYLX",".*","企业类型"}, {"ZZJGDM",".*","组织机构代码"}, {"SWDJH",".*","税务登记号"}, {"YXQ","(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)","有效期"}, {"YWLXR",".*","业务联系人"}, {"LXRSJ",".*","联系人手机"}, {"BGCZ",".*","办公传真"}, {"BGDH",".*","办公电话"}, {"LXRYX",".*","联系人邮箱"}, {"LXRZW",".*","联系人职务"}, {"BGDZ",".*","办公地址"},  {"BLGYSCFZQ",".*","不良供应商处罚周期"}, {"HMD",".*","黑名单"}, {"BLGYSXYPJDJ",".*","供应商信用评价等级"}, {"GYSJYFW",".*","供应商经营范围"}, {"GYSFL",".*","供应商分类"}, {"GYSQYXZ",".*","供应商企业性质"}};
			
			for(int n = 0;n<list.size();n++){
				
				Record r = new Record();

				for(int i=0; i<fields.length; i++) {
					
					if(fields[i][0] == "GYSBH" && (list.get(n).get(i) == null || list.get(n).get(i) == "")){
						renderJson("result","第"+(n+2)+"行"+"第"+(i+1)+"列数据"+list.get(n).get(i)+"格式填写错误！请修改后重新填写！"); return;
					}
					if(list.get(n).get(i) != null) {
						boolean flag = Pattern.matches(fields[i][1], list.get(n).get(i).toString());
						if(!flag){
							renderJson("result","第"+(n+2)+"行"+"第"+(i+1)+"列数据"+fields[i][2]+list.get(n).get(i)+"格式填写错误！请修改后重新填写！"); return;
						}
					}
					//在导入信息的时候进行字符串的trim()切割！
					/*if(fields[i][0] == "QYMC"){
						r.set(fields[i][0], list.get(n).get(i).toString().trim());
					}else{
						r.set(fields[i][0], list.get(n).get(i));
					}*/
					r.set(fields[i][0], list.get(n).get(i));
				}
				
				if(r != null) recordList.add(r);
			}
			
			if(!(Db.batchSave("E_SUPPLIER", recordList, recordList.size()).length > 0) ) {
				renderJson("result", "导入数据失败，请修改后重试！"); return;
			}
			
			/*File file2 = file.getFile();
			System.out.println(file2.getName()); */

			file.getFile().delete();//删除上传的缓存文件
			
			renderJson("result", "0"); 
		}
	
	/**
	 * 
	 * 展示供应商管理信息
	 */
	public void list(){ 
		
		String sql = "select * from E_SUPPLIER where BLGYSCFZQ < to_char(SYSDATE, 'yyyy-mm-dd')";
		List<Supplier> list = Supplier.dao.find(sql); 
		if(list!=null){
			for (Supplier supplier2 : list) {
				
				String sql2 = "update E_SUPPLIER set HMD = '否' where ID = ?";
				
				Db.update(sql2,supplier2.getId());
			}
		}
		
		Page<Supplier> page = Supplier.dao.findPaginate(pageNumber(), pageSize(),getModel(Supplier.class),getModel(CertificateSupcode.class,"certificatesupcode"),getModel(Certificate.class,"certificate"),getModel(Suppliersort.class,"suppliersort"),getModel(Suppliergrade.class,"suppliergrade"));
		 
		renderJson(page);
	}  
	
	/** 
	 * 保存信息的方法
	 * 
	 */
	public void save(){
		
		boolean status = false;
		Supplier supplier = null;
		
		try {
			 supplier = getModel(Supplier.class,"supplier");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (supplier.getId() == null) {
			supplier.remove("ID");
			if(!Supplier.dao.isExistGysbh(supplier.getGysbh())) {
				status = supplier.save();
				log.info("****保存供应商,编号为"+ supplier.getGysbh());
			}else {
				setAttr("content", "供应商编号已存在!");
			}
		} else {
			//修改
			try {
				if(!Supplier.dao.isExistGysbh(supplier.getGysbh(), supplier.getId())) {	
					status = supplier.update();
				}else {
					setAttr("content", "供应商编号已存在!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		setAttr("message", status ? "保存成功!" : "保存失败!");
		
		renderJson();
	}
	/**
	 * detailView
	 * 查看每一行的信息
	 * 
	 */
	public void detailView() {
		setAttr("supplier", Supplier.dao.queryById(getPara("id")));
		render("detail.html");
	}
	
	/**
	 * 
	 * 删除对应的一行
	 */
	
	public void delete(){
		
		String sql = "select YYZZZP from E_SUPPLIER where ID =?";
		Record record = Db.findFirst(sql,getPara("id"));
		
		if(Quote.dao.queryByCerId(getPara("id")).size()>0){
			setAttr("status",2);
			log.info("****删除供应商记录ID在报价表存在了="+ getPara("id"));
		}else{
			if(record.get("YYZZZP")!=null){
				File file = new File(this.getSession().getServletContext().getRealPath("") + record.get("YYZZZP"));
				file.delete();
			}
			boolean deleteResult = Supplier.dao.deleteById(getPara("id"));
			if(deleteResult == true){
				setAttr("status", 1);
				log.info("****删除供应商记录ID="+ getPara("id"));
			}
		}
		renderJson(); 
	}
	
	/**
	 * 
	 * 批量删除
	 */ 
	public void deleteBatch(){
		
		if(Supplier.dao.deleteByIds(getPara("ids"))){
			setAttr("status",1);
			log.info("****删除供应商记录ID="+ getPara("ids")); 
		}else{
			setAttr("status",2);
		}
		renderJson();
		
	}
	
	/**
	 * 修改供应商记录在save.html页面展示
	 * 
	 */ 
	public void updateView(){
		
		setAttr("supplier",Supplier.dao.queryById(getPara("id")));
		
		render("save.html");
	}
	
	/**
	 * 子窗口信息展示
	 * 
	 */
	
	public void showCer(){
		
		Page<Certificate> page = Certificate.dao.queryBySupplierId(pageNumber(), pageSize(), getPara("supplierId"));
		
		
		renderJson(page);
	}
	
	/**
	 * 
	 * 导出模板:将需要的模板进行上传到resources--templates--supplier_template.xls(supplier_template.xlsx)
	 * 直接下载模板
	 * @throws IOException 
	 */
	public void export() throws IOException {
		
		String path = Thread.currentThread().getContextClassLoader().getResource("templates/supplier_template.xlsx").getPath();
		
		File file = new File(path);
		
		renderFile(file);
	}
	
	/**
	 * 查找资质过期的供应商
	 */
	public void findOverDate() {
		
		List<Record> supListRes = Db.find(Db.getSql("supplier.findOverDate"));
		
		List<Record> find = Db.find("select NO,NAME,SUPPLIER_ID from E_CERTIFICATE where END_TIME < to_char(SYSDATE, 'yyyy-mm-dd')");
		
		setAttr("gys", supListRes);
		setAttr("zzzs", find);
		renderJson();
	}
	
	
	/**
	 * 项目中供应商信息的编号查询供应商信息
	 */
	public void findProjectOverDate() {
		
		List<Supplier> findProjectOverDate = Supplier.dao.findProjectOverDate(getPara("PROJECT_ID"));
		
		renderJson(findProjectOverDate);
		}
	

	public void exportGys() throws IOException{
		/*new 一个file文件*/
		File file = new File("GysExport.xls");
		List<Record> exportList = Supplier.dao.findExportGys(getModel(Supplier.class),getModel(CertificateSupcode.class,"certificatesupcode"),getModel(Certificate.class,"certificate"),getModel(Suppliersort.class,"suppliersort"),getModel(Suppliergrade.class,"suppliergrade"));
		//String[] title = {"供应商编号", "社会信用代码", "营业执照注册号", "企业名称", "法定代表人", "法定代表人电话", "所属省", "所属市", "住所", "注册资本", "成立日期", "营业期限", "企业类型", "组织机构代码", "税务登记号", "有效期", "业务联系人", "联系人手机", "办公传真", "办公电话", "联系人邮箱", "联系人职务", "办公地址", "不良供应商处罚周期", "黑名单", "不良供应商信用评价等级", "供应商经营范围", "录入时间", "供应商分类", "供应商企业性质", "营业执照照片"};
		//String[] field = {"GYSBH", "SHXYDM", "YYZZZCH", "QYMC", "FDDBR", "FDDBRDH", "SSS", "SSS1", "ZS", "ZCZB", "CLRQ", "YYQX", "QYLX", "ZZJGDM", "SWDJH", "YXQ", "YWLXR", "LXRSJ", "BGCZ", "BGDH", "LXRYX", "LXRZW", "BGDZ", "BLGYSCFZQ", "HMD", "BLGYSXYPJDJ", "GYSJYFW", "ENTRY_TIME", "GYSFL", "GYSQYXZ", "YYZZZP"};
		
		String[] title = {"供应商编号", "社会信用代码", "营业执照注册号", "企业名称", "法定代表人", "法定代表人电话", "所属省", "所属市", "住所", "注册资本", "成立日期", "营业期限", "企业类型", "组织机构代码", "税务登记号", "有效期", "业务联系人", "联系人手机", "办公传真", "办公电话", "联系人邮箱", "联系人职务", "办公地址", "不良供应商处罚周期", "黑名单", "不良供应商信用评价等级", "供应商经营范围", "供应商分类", "供应商企业性质"};
		String[] field = {"GYSBH", "SHXYDM", "YYZZZCH", "QYMC", "FDDBR", "FDDBRDH", "SSS", "SSS1", "ZS", "ZCZB", "CLRQ", "YYQX", "QYLX", "ZZJGDM", "SWDJH", "YXQ", "YWLXR", "LXRSJ", "BGCZ", "BGDH", "LXRYX", "LXRZW", "BGDZ", "BLGYSCFZQ", "HMD", "BLGYSXYPJDJ", "GYSJYFW", "GYSFL", "GYSQYXZ"};
		
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
		    //区分大小写的格式类型
		    /*if (".png".equals(extension) || ".jpg".equals(extension)
		            || ".gif".equals(extension) || "jpeg".equals(extension)
		            || "bmp".equals(extension) || "JPG".equals(extension)) {*/
		    //不区分大小写的格式类型
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
		            json.put("error", 0);
		            json.put("src", "/upload/pic/" + fileName); // 相对地址，显示图片用
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
		    lists.add(json.toJSONString());
		}
	}
	
	
	/*UploadFile filenames = getFile("images");
	
	String fileName = filenames.getOriginalFileName();
	File source = new File(filenames.getUploadPath() + "\\" + fileName); // 获取临时文件对象
    String extension = fileName.substring(fileName.lastIndexOf("."));
    String savePath = PathKit.getWebRootPath() + "/upload/pic/";
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
            json.put("src", "/upload/pic/" + fileName); // 相对地址，显示图片用
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
    	renderJson(json.toJSONString());*/
		renderJson(lists);
	}


}
