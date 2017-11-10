###供应商等级对应的sql文件

#namespace("suppliergrade")

	###查询供应商等级信息
	#sql("suppliergradeList")
	
	select * from E_SUPPLIERGRADE 
		where 1 = 1
			#if(GRADEPJ ??)
				and GRADEPJ like '%#(GRADEPJ)%'
			#end
		ORDER BY GRADEPJ
		
	#end
	
	
	###添加根据供应商等级查找
	
	#sql("findByGradepj")
	
		select * from E_SUPPLIERGRADE where GRADEPJ = ?
		
	#end
	
	### 根据等级ID和等级查询
	
	#sql("findByGradepjAndId")
		select * from E_SUPPLIERGRADE where GRADEPJ = ? and Id != ?
	#end
	

#end

