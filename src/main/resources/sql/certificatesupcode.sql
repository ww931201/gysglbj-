#namespace("certificatesupcode")
	
###查询单行的证书信息
	#sql("selectById")
	
	select * from E_CERTIFICATE_SUPCODE where CERID = ?
	
	#end
	
#end