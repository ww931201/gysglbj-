###证书类别对应的sql文件

#namespace("suppliersort")

	###查询单行的证书信息
	#sql("suppliersortList")
	
	select * from E_SUPPLIERSORT 
		where 1 = 1
			#if(GYSSORTNAME ??)
			
				and GYSSORTNAME like '%#(GYSSORTNAME)%'
				
			#end
		ORDER BY ID
	#end
	
	
	###添加根据证书类别查找
	
	#sql("findByGysSortName")
	
		select * from E_SUPPLIERSORT where GYSSORTNAME = ?
		
	#end
	
	### 根据类别ID和类别查询
	
	#sql("findByGysSortNameAndId")
		select * from E_SUPPLIERSORT where GYSSORTNAME = ? and Id != ?
	#end
	

#end

