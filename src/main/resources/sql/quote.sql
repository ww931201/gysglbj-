#namespace("quote")
	
	### 查询所有的项目信息
	#sql("paginateList")
		select * from VIEW_QUOTE WHERE 1=1
		#if(WZBM ??)
			and upper(WZBM) like upper('%#(WZBM)%')
		#end
		#if(WZMC ??)
			and upper(WZMC) like  upper('%#(WZMC)%')
		#end
		#if(GGXH ??)
			and upper(GGXH) like upper('%#(GGXH)%')
		#end
		#if(QYMC ??)
			and QYMC like '%#(QYMC)%'
		#end
		#if(GYSBH ??)
			and GYSBH = '#(GYSBH)'
		#end
		#if(PROJECT_ID ??)
			and PROJECT_ID = '#(PROJECT_ID)'
		#end
		#if(SFYXBJ ??)
			and SFYXBJ = '#(SFYXBJ)'
		#end
		ORDER BY GYSBH, BJH ASC
	#end
	
	### 查询过滤项目信息(竞价分包结果VIEW_QUOTE_RESULT)
	#sql("filterList")
		SELECT a.* FROM VIEW_QUOTE_RESULT a WHERE 1=1 
		#if(WZBM ??)
			and upper(WZBM) like upper('%#(WZBM)%')
		#end
		#if(WZMC ??)
			and upper(WZMC) like  upper('%#(WZMC)%')
		#end
		#if(GGXH ??)
			and upper(GGXH) like upper('%#(GGXH)%')
		#end
		#if(PROJECT_ID ??)
			and PROJECT_ID = '#(PROJECT_ID)'
		#end
		#if(SFYXBJ ??)
			and SFYXBJ = '#(SFYXBJ)'
		#end
		#if(QYMC ??)
			and QYMC like '%#(QYMC)%'
		#end
		#if(GYSBH ??)
			and GYSBH like '%#(GYSBH)%'
		#end
		#if(XH ??)
			and XH like '%#(XH)%'
		#end
	    ORDER BY a.PROJECT_ID,a.BJH,a.WZBM,a.CSBZXJ_BHS
	#end
	
		### 查询过滤项目信息(竞价分包结果)
	#sql("filterList2")
		SELECT a.* FROM VIEW_QUOTE a WHERE 1=1 
		#if(WZBM ??)
			and upper(WZBM) like upper('%#(WZBM)%')
		#end
		#if(WZMC ??)
			and upper(WZMC) like  upper('%#(WZMC)%')
		#end
		#if(GGXH ??)
			and upper(GGXH) like upper('%#(GGXH)%')
		#end
		#if(PROJECT_ID ??)
			and PROJECT_ID = '#(PROJECT_ID)'
		#end
		#if(SFYXBJ ??)
			and SFYXBJ = '#(SFYXBJ)'
		#end
		#if(QYMC ??)
			and QYMC like '%#(QYMC)%'
		#end
		#if(GYSBH ??)
			and GYSBH like '%#(GYSBH)%'
		#end
		#if(XH ??)
			and XH like '%#(XH)%'
		#end
	    ORDER BY GYSBH, BJH, ENTRY_TIME DESC
	#end
	
	### 查询过滤项目信息(竞买分包结果)
	#sql("filterJJList")
		SELECT gysbh, qymc, sum(zxj_bhs) zxj_bhs, sum(CSBZXJ_BHS) CSBZXJ_BHS,count(CSBZXJ_BHS) SFYXBJ  FROM VIEW_QUOTE where 1=1
		#if(WZBM ??)
			and upper(WZBM) like upper('%#(WZBM)%')
		#end
		#if(WZMC ??)
			and upper(WZMC) like upper('%#(WZMC)%')
		#end
		#if(GGXH ??)
			and upper(GGXH) like upper('%#(GGXH)%')
		#end
		#if(PROJECT_ID ??)
			and PROJECT_ID = '#(PROJECT_ID)'
		#end
		#if(SFYXBJ ??)
			and SFYXBJ = '#(SFYXBJ)'
		#end
		#if(QYMC ??)
			and QYMC like '%#(QYMC)%'
		#end
		#if(GYSBH ??)
			and GYSBH like '%#(GYSBH)%'
		#end
		and CSBZXJ_BHS！=0 and CSBZXJ_BHS is not null
	    GROUP BY gysbh, qymc ORDER BY CSBZXJ_BHS
	#end

#end