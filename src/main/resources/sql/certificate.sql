###证书对应的sql文件

#namespace("certificate")

	###查询单行的证书信息
	#sql("selectById")
	
	select * from E_CERTIFICATE where ID = ?
	
	#end
	
	
	###根据证书编号查询证书
	#sql("findByZsbh")
	
	select * from E_CERTIFICATE where NO = ?
	
	#end
	
	###根据证书编号和ID修改
	#sql("findByZsbhAndId")
	
	select * from E_CERTIFICATE where NO = ? and ID != ?
	
	#end
#end