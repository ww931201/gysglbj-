###证书类别对应的sql文件

#namespace("suppliestemplate")

	###查询单行的证书信息
	#sql("suppliestemplateList")
	
	select * from E_SUPPLIESTEMPLATE 
		where 1 = 1
			#if(WZBM ??)
				and WZBM = '#(WZBM)'
			#end
			#if(WZMC ??)
				and WZMC like '%#(WZMC)%'
			#end
			#if(QYMC2 ??)
				and QYMC2 = '#(QYMC2)'
			#end
		ORDER BY ID
	#end
	
	
	###添加根据证书类别查找
	
	#sql("findByWzbm")
	
		select * from E_SUPPLIESTEMPLATE where WZBM = ?
		
	#end
	
	### 根据类别ID和类别查询
	
	#sql("findByWzbmAndId")
		select * from E_SUPPLIESTEMPLATE where WZBM = ? and Id != ?
	#end
	

#end

