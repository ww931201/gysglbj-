###证书类别对应的sql文件

#namespace("cersort")

	###查询单行的证书信息
	#sql("cersortList")
	
	select * from E_CERSORT 
		where 1 = 1
			#if(CERSORTNAME ??)
			
				and CERSORTNAME like '%#(CERSORTNAME)%'
				
			#end
		ORDER BY ID
	#end
	
	
	###添加根据证书类别查找
	
	#sql("findBySortName")
	
		select * from E_CERSORT where CERSORTNAME = ?
		
	#end
	
	### 根据类别ID和类别查询
	
	#sql("findBysortNameAndId")
		select * from E_CERSORT where CERSORTNAME = ? and Id != ?
	#end
	

#end

