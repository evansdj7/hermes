/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws.data;

/** 
 * The <code>EBMSAdminData</code> is data structure for holding the administration data for EbMS plugin.
 *
 * @author 	Twinsen Tsang 
 * @version 1.0.0
 * @since   H2O 0908
 */
public class EBMSAdminData extends AdminData 
{
	/**
	 * This is the configuration prefix for serialization / de-serialization.<br/><br/>
	 */
	public static final String CONFIG_PREFIX = "ebms-admin-request/config";
	
	/**
	 * This is the parameter prefix for serialization / de-serialization.<br/><br/>
	 */
	public static final String PARAM_PREFIX  = "ebms-admin-request/param";	
	
	/**
	 * Create an instance of <code>EBMSAdminData</code> with default value.
	 * <br/><br/>
	 * <ol>
	 * 	<li>User-name: corvus</li>
	 * 	<li>Password : corvus</li>
	 * 	<li>Manage Partnership End-point : http://localhost:8080/corvus/admin/ebms/partnership</li>
	 * 	<li>Envelop Query End-point 	 : http://localhost:8080/corvus/admin/ebms/repository</li>
	 * 	<li>Partnership Operation : 1</li>
	 * 	<li>Message Box Criteria: INBOX</li>
	 * 	<li>Message ID Criteria: changme-messageid</li>
	 * </ol> 
	 */
	public EBMSAdminData(){
		super(CONFIG_KEY_SET.length);
	}
}
