package com.sinorail.gysglbj.action;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.sinorail.gysglbj.extend.QuiController;
import com.sinorail.gysglbj.model.Certificate;
import com.sinorail.gysglbj.model.CertificateSupcode;
import com.sinorail.gysglbj.util.ExcelUtils;

/**
 * 
 *  证书的Action
 * @author wangwei
 *
 */
public class CertificateAction extends QuiController{
	
	private Logger log  = Logger.getLogger(CertificateAction.class);
	
	public void index(){
		render("view.html");
		log.info("***跳转到证书的展示页面");
	}
	
    
	public void detailViewSon(){
		
		setAttr("certificate", Certificate.dao.queryById(getPara("id")));
		
		setAttr("certificatesupcode", CertificateSupcode.dao.queryById(getPara("id")));
		
		render("detail.html");
	}
	
	public void addViewSon(){
		String fatherId = getPara("fatherId");
		setAttr("fatherId", fatherId);
		render("save.html");
	}
	
	/**
	 * 证书保存
	 */
	public void save(){
		
		boolean status = false;
		Certificate certificate = getModel(Certificate.class,"certificate");
		
		String codefirst = getPara("certificatesupcode.CODE");
		
		/*CertificateSupcode certificatesupcode = getModel(CertificateSupcode.class,"certificatesupcode");
		
		String codefirst = certificatesupcode.getCode();*/
		
		/*certificate.setStartTime(new Timestamp(getParaToDate("START_TIME").getTime()));
		certificate.setEndTime(new Timestamp(getParaToDate("END_TIME").getTime()));*/
		
		String[] rescode ={};
		
		if(codefirst != null){
			if((!codefirst.trim().equals(""))){
				String code = codefirst.replaceAll("，", ",");
				rescode = code.split(",");
			}
		}
		
		if (certificate.getId() == null) {
			
			certificate.remove("ID");
			
			if(!Certificate.dao.isExistZsbh(certificate.getNo())) {		
				
				status = certificate.save();
				
				String cerId = certificate.getId();
				
				if(rescode.length!=0){
					for (int i = 0; i<rescode.length;i++) {
						CertificateSupcode supcode = new CertificateSupcode();
						
						supcode.setCerid(cerId); 
						supcode.setCode(rescode[i]);
						supcode.setCodename(codefirst);
						boolean save = supcode.save();
						log.info(save);
					}
				}
				log.info("****保存证书,编号为"+ certificate.getNo());
				
			}else {
				setAttr("content", "证书编号已存在!");
			}
		} else {
			//修改
			try {
				if(!Certificate.dao.isExistZsbh(certificate.getNo(), certificate.getId())) {
					//取消资质证书的父ID
					
					certificate.remove("SUPPLIER_ID");
					status = certificate.update();
					//删除物资编码再进行保存
					String cerID = certificate.getId();
					String sql = "delete from E_CERTIFICATE_SUPCODE where CERID = ?";
					Db.update(sql, cerID);
					
					if(rescode.length!=0){
						for (int i = 0; i<rescode.length;i++) {
							CertificateSupcode supcode = new CertificateSupcode();
							supcode.setCerid(cerID); 
							supcode.setCode(rescode[i]);
							supcode.setCodename(codefirst);
							boolean save = supcode.save();
							log.info(save);
						}
					}
				}else {
					setAttr("content", "证书编号已存在!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		setAttr("message", status ? "保存成功!" : "保存失败!");
		renderJson();
	}
	
	/**
	 * 证书修改
	 */ 
	public void updateViewSon(){
		
		setAttr("certificate", Certificate.dao.queryById(getPara("id")));
		String codes = "";
		String cerid = getPara("id");
		String sql = "select CODE from E_CERTIFICATE_SUPCODE where CERID  = ?";
		List<Record> find = Db.find(sql,cerid);
		if(find.size() !=0){
			for (Record record : find) {
				String code = record.get("CODE");
				codes += code + ",";
			}
			codes = codes.substring(0, codes.length()-1); 
			setAttr("codes",codes);
		}
		setAttr("codes",codes);
		render("save.html");
	}
	
	/**
	 * 证书删除
	 * 
	 */
	public void deleteSon(){
		if(Certificate.dao.deleteById(getPara("id"))){
			
			List<CertificateSupcode> find = CertificateSupcode.dao.find("select ID from E_CERTIFICATE_SUPCODE where CERID = ? ",getPara("id"));
			
			if(find!=null){
				for (CertificateSupcode certificateSupcode : find) {
					CertificateSupcode.dao.deleteById(certificateSupcode.getId());
				}
			}
			
			setAttr("status", 1);
			log.info("****删除ID="+ getPara("id"));
		}
		renderJson(); 
	}
	
	/**
	 * 
	 */
	public void importView(){
		setAttr("supplierId", getPara("supplierIds"));
		render("importView.html");
	}
	
	public void importExcel() throws IOException, ParseException{
		
		UploadFile file = getFile("excel");
		
		List<List<Object>> list = null;
		
		try {
			list = ExcelUtils.readExcel(file.getFile(), 1);
		} catch (IOException e1) {
			
			e1.printStackTrace();
			renderJson("result", "导入失败，请修改后再导入数据"); return; 
			
		}
		/**进行证书编码重复校验*/
		//1.导入excel文件本身的证书编码重复
		 
		String temp = "";
		List<Object> lists = new ArrayList<Object>();
		int a = 2;
		if(list!=null){
			for (List<Object> cerNo : list) {
				if(lists!=null){
					for (Object object : lists) {
						if(cerNo.get(0).equals(object)){
							temp += cerNo.get(0)+",";
						}
						if(temp.length()>0){
							renderJson("result", "第" + a + "行证书编码'"+object+"'在excel已经存在，请修改后再导入数据"); return; 
						}
					}
				}
				  lists.add(cerNo.get(0));
				  a++;
			}
		}
		
		
		//2.导入的excel文件与系统数据库的编码重复
		String temps = "";
		List<Certificate> noList = Certificate.dao.find("select NO from E_CERTIFICATE"); 
		if(noList!=null){
				for (Certificate certificate : noList) {
					String cerno = certificate.getNo();
					if(list!=null){
						for (int l = 0;list.size()>l;l++) { 
							if(list.get(l).get(0).equals(cerno)){
								temps += cerno+",";
							}
							if(temps.length()>0){
								renderJson("result", "第" + (l+2) + "行证书编码'"+cerno+"'在数据库已经存在，请修改后再导入数据"); return; 
							}
							
						}
					}
				}
			}
		
		List<Record> recordList = new LinkedList<Record>();
		
		String[][] fields = {{"NO",".*","编号"}, {"NAME",".*","名称"}, {"CONTENT",".*","内容"}, {"START_TIME","(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)","证书有效期(起始日期)"}, {"END_TIME","(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)","证书有效期(终止日期)"}, {"UNIT",".*","发证单位"}, {"ZSLB",".*","证书类别"}, {"ZSXX",".*","证书信息"} };

		for(int n = 0;n<list.size();n++){
			
			Record r = new Record().set("SUPPLIER_ID", getPara("supplierId"));
			
			for(int i=0; i<fields.length; i++) {
				
				if(fields[i][0] == "NO" && (list.get(n).get(i) == null || list.get(n).get(i) == "")){
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
		
		if(!(Db.batchSave("E_CERTIFICATE", recordList, recordList.size()).length > 0) ) {
			
			renderJson("result", "导入失败，请修改后再导入数据"); return; 
		}
		
		file.getFile().delete();//删除上传的缓存文件
		
		//renderJson();
		renderJson("result", "0"); 
		
		}
	
	/**
	 * 导出证书模板
	 */
	public void export(){
		
		String path = Thread.currentThread().getContextClassLoader().getResource("templates/certificate_template.xlsx").getPath();
		
		File file = new File(path);
		
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
			    if (".png".equals(extension) || ".jpg".equals(extension)
			            || ".gif".equals(extension) || "jpeg".equals(extension)
			            || "bmp".equals(extension) || "JPG".equals(extension)) {
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
}
