package com.sinorail.gysglbj;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.sinorail.gysglbj.action.CerSortAction;
import com.sinorail.gysglbj.action.CerSuppliestemplateAction;
import com.sinorail.gysglbj.action.CertificateAction;
import com.sinorail.gysglbj.action.GysglAction;
import com.sinorail.gysglbj.action.IndexAction;
import com.sinorail.gysglbj.action.JoinlistAction;
import com.sinorail.gysglbj.action.LoginAction;
import com.sinorail.gysglbj.action.PopedomAction;
import com.sinorail.gysglbj.action.PopedomMatchAction;
import com.sinorail.gysglbj.action.ProjectAction;
import com.sinorail.gysglbj.action.QuoteAction;
import com.sinorail.gysglbj.action.RoleAction;
import com.sinorail.gysglbj.action.SupplierGradeAction;
import com.sinorail.gysglbj.action.SupplierSortAction;
import com.sinorail.gysglbj.action.SuppliestemplateAction;
import com.sinorail.gysglbj.action.UserAction;
import com.sinorail.gysglbj.aop.LoginInterceptor;
import com.sinorail.gysglbj.constant.Const;
import com.sinorail.gysglbj.model._MappingKit;
import com.sinorail.gysglbj.util.FileUtils;

public class MainConfig extends JFinalConfig {
	
	/**
	 * 配置JFinal常量
	 */
	@Override
	public void configConstant(Constants me) {
		//读取数据库配置文件
		PropKit.use(Const.file_jdbc);
		//设置当前是否为开发模式
		me.setDevMode(PropKit.getBoolean("devMode"));
		//设置默认上传文件保存路径 getFile等使用
		me.setBaseUploadPath("upload/temp/");
		//设置上传最大限制尺寸
		//me.setMaxPostSize(1024*1024*10);
		//设置默认下载文件路径 renderFile使用
		me.setBaseDownloadPath("download");
		//设置默认视图类型
		me.setViewType(ViewType.JFINAL_TEMPLATE);
		//设置404渲染视图
		me.setError404View("/static/errorPage/404.html");
		
	}
	
	/**
	 * 配置JFinal路由映射
	 */
	@Override
	public void configRoute(Routes me) {
		
		me.add("/", IndexAction.class, "WEB-INF/view");
		me.add("/login", LoginAction.class, "WEB-INF/view");
		me.add("/user", UserAction.class, "WEB-INF/view/system/user");
		me.add("/role", RoleAction.class, "WEB-INF/view/system/role");
		me.add("/popedom", PopedomAction.class, "WEB-INF/view/system/popedom");
		me.add("/popedomMatch", PopedomMatchAction.class, "WEB-INF/view/system/popedomMatch");
		me.add("/gys", GysglAction.class, "WEB-INF/view/gl");
		me.add("/project", ProjectAction.class, "WEB-INF/view/base/project");
		me.add("/quote", QuoteAction.class, "WEB-INF/view/base/quote");
		me.add("/certificate", CertificateAction.class, "WEB-INF/view/zzzs");
		me.add("/cersort", CerSortAction.class, "WEB-INF/view/zzzslb");
		me.add("/suppliergrade", SupplierGradeAction.class, "WEB-INF/view/gysdj");
		me.add("/suppliersort", SupplierSortAction.class, "WEB-INF/view/gysfl");
		me.add("/suppliestemplate", SuppliestemplateAction.class, "WEB-INF/view/wzk");
		me.add("/cersuppliestemplate", CerSuppliestemplateAction.class, "WEB-INF/view/zswzk");  
		me.add("/joinlist", JoinlistAction.class, "WEB-INF/view/zhcx");
	} 
	
	/**
	 * 配置JFinal插件
	 * 数据库连接池
	 * ORM
	 * 缓存等插件
	 * 自定义插件
	 */
	@Override
	public void configPlugin(Plugins me) {
		//配置数据库连接池插件
		DruidPlugin dbPlugin=new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password"));
		dbPlugin.setValidationQuery("select 1 from dual");
		dbPlugin.addFilter(new StatFilter());
		
		WallFilter wall = new WallFilter();
		wall.setDbType("oracle");
		
		dbPlugin.addFilter(wall);
		
		dbPlugin.setDriverClass(Const.db_driver);
		//添加到插件列表中
		me.add(dbPlugin);
		
		//orm映射 配置ActiveRecord插件
		ActiveRecordPlugin arp=new ActiveRecordPlugin(dbPlugin);
		//arp.setContainerFactory(new CaseInsensitiveContainerFactory(false));
		arp.setShowSql(PropKit.getBoolean("devMode"));
		arp.setDialect(new OracleDialect());
		
		//添加sql文件映射
		arp.setBaseSqlTemplatePath(PathKit.getRootClassPath());
		
		/********在此添加dao层sql文件 *********/
		arp.addSqlTemplate("sql/user.sql");
		arp.addSqlTemplate("sql/role.sql");
		arp.addSqlTemplate("sql/popedom.sql");
		arp.addSqlTemplate("sql/supplier.sql");
		arp.addSqlTemplate("sql/project.sql");
		arp.addSqlTemplate("sql/quote.sql");
		arp.addSqlTemplate("sql/certificate.sql");
		arp.addSqlTemplate("sql/certificatesupcode.sql");
		arp.addSqlTemplate("sql/cersort.sql");
		arp.addSqlTemplate("sql/suppliergrade.sql");
		arp.addSqlTemplate("sql/suppliersort.sql");
		arp.addSqlTemplate("sql/suppliestemplate.sql");
		arp.addSqlTemplate("sql/cersuppliestemplate.sql");
		/********在此添加数据库 表-Model 映射*********/
		_MappingKit.mapping(arp);
		
		me.add(arp);
	}
	/**
	 * 配置全局拦截器
	 */
	@Override
	public void configInterceptor(Interceptors me) {
		me.addGlobalActionInterceptor(new LoginInterceptor());
		//me.addGlobalActionInterceptor(new ThemeGlobalInterceptor());
		
	}
	/**
	 * 配置全局处理器
	 */
	@Override
	public void configHandler(Handlers me) {

	}
	
	/**
	 * 配置模板引擎 
	 */
	@Override
	public void configEngine(Engine me) {
		//这里只有选择JFinal TPL的时候才用
		//配置共享函数模板
		//me.addSharedFunction("/view/common/layout.html")
	}
	
	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 80, "/", 5);
	}

	@Override
	public void afterJFinalStart() {
		FileUtils.createDir(Const.temp_file_path);
	}
	
}
