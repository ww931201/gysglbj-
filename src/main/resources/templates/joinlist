#综合查询
	
	#sql("joinList")
		/*select * from E_SUPPLIER a left join 
		E_CERTIFICATE b on a.ID=b.SUPPLIER_ID
		left join E_CERTIFICATE_SUPCODE c on c.CERID=b.ID*/
	
		/*select * from E_SUPPLIER a left join 
		E_CERTIFICATE b on a.ID=b.SUPPLIER_ID
		left join E_CERTIFICATE_SUPCODE c on c.CERID=b.ID  left join E_SUPPLIESTEMPLATE d on d.WZBM = c.CODE*/
	
		select * from
		(select * from E_SUPPLIER a left join 
		E_CERTIFICATE b on a.ID=b.SUPPLIER_ID
		left join E_CERTIFICATE_SUPCODE c on c.CERID=b.ID) e left join E_SUPPLIESTEMPLATE d on d.WZBM = e.CODE
		where 1 = 1
		#if(GYSBH ??)
			and upper(GYSBH) like upper('%#(GYSBH)%')
		#end
		#if(QYMC ??)
			and QYMC like '%#(QYMC)%'
		#end
		#if(CODE ??)
			and CODE like '%#(CODE)%'
		#end
		#if(NO ??)
			and NO like '%#(NO)%'
		#end
		#if(NAME ??)
			and NAME like '%#(NAME)%'
		#end
		ORDER BY GYSBH DESC
	#end