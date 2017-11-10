package com.sinorail.gysglbj.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {

	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("DIC_POPEDOM", "ID", DicPopedom.class);
		arp.addMapping("DIC_ROLE", "ID", DicRole.class);
		arp.addMapping("DIC_ROLE_POPEDOM", "ID", DicRolePopedom.class);
		arp.addMapping("DIC_USER", "ID", DicUser.class);
		arp.addMapping("E_CERSORT", "ID", Cersort.class);
		arp.addMapping("E_CERSUPPLIESTEMPLATE", "ID", Cersuppliestemplate.class);
		arp.addMapping("E_CERTIFICATE", "ID", Certificate.class);
		arp.addMapping("E_CERTIFICATE_SUPCODE", "ID", CertificateSupcode.class);
		arp.addMapping("E_PROJECT", "ID", Project.class);
		arp.addMapping("E_QUOTE", "ID", Quote.class);
		arp.addMapping("E_SUPPLIER", "ID", Supplier.class);
		arp.addMapping("E_SUPPLIERGRADE", "ID", Suppliergrade.class);
		arp.addMapping("E_SUPPLIERSORT", "ID", Suppliersort.class);
		arp.addMapping("E_SUPPLIESTEMPLATE", "ID", Suppliestemplate.class);
	}
}

