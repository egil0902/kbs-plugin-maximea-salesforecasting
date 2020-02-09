/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * Copyright (C) 2003-2012 e-Evolution,SC. All Rights Reserved.               *
 * Contributor(s): victor.perez@e-evolution.com www.e-evolution.com   		  *
 *****************************************************************************/

package org.forecasting.maximea.process;

import java.util.Collection;
import java.util.logging.Level;


import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.Env;
import org.forecasting.maximea.model.I_PP_ForecastRunResult;
import org.forecasting.maximea.model.MPPForecastRunMaster;
import org.forecasting.maximea.model.MPPForecastRunResult;
import org.forecasting.maximea.model.MPPPeriod;

/**
 * This process allows to generate a Sales Orders based on the calculation of the
 * simulation of a forecast.
 * 
 * @author victor.perez@e-evolution.com, www.e-evolution.com
 *  @author pshepetko Maximea
 */
public class GenerateSaleOrder extends SvrProcess {

	private int p_C_DocType_ID;
	private int p_AD_Org_ID;
	private int p_SalesRep_ID;
//	private String p_ForecastActionType;
//	private String p_ForecastLoadType;
//	private int p_DaysAfterDue;

	/** Order				*/
	private MOrder		m_order = null;
	/** Order Line			*/
	private MOrderLine	m_orderLine = null;
	
	/**
	 * Prepare - e.g., get Parameters.
	 */
	protected void prepare() {
		for (ProcessInfoParameter para : getParameter()) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals("C_DocType_ID"))
				p_C_DocType_ID = para.getParameterAsInt();
			else if (name.equals("AD_Org_ID"))
				p_AD_Org_ID = para.getParameterAsInt();
/*			else if (name.equals("ForecastActionType"))
				p_ForecastActionType = (String) para.getParameter();
			else if (name.equals("ForecastLoadType"))
				p_ForecastLoadType = (String) para.getParameter();
			else if (name.equals("DaysAfterDue"))
				p_DaysAfterDue = para.getParameterAsInt();
*/			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
		}
	} // prepare

	/**
	 * Process
	 * 
	 * @return message
	 * @throws Exception
	 */
	protected String doIt() throws Exception {
		
		int deletedRecord = 0;
		int updatedRecord = 0;
		int createdRecord = 0;
		StringBuilder result = new StringBuilder();
		int count=0;
		
		if (p_SalesRep_ID <= 0)
			p_SalesRep_ID = Env.getAD_User_ID(getCtx());
		/*		
 		if (p_ForecastLoadType==null) p_ForecastLoadType="S";
 		if (p_ForecastActionType==null) p_ForecastActionType="R";

		if ("R".equals(p_ForecastActionType)) {
			deletedRecord = deleteData();
			createdRecord = insertForecast();
		} else if ("M".equals(p_ForecastActionType)) {
			updatedRecord = updateForecast();
			createdRecord = insertForecast();
		}
		 */
				
		String whereClause = " EXISTS (SELECT T_Selection_ID FROM T_Selection WHERE  T_Selection.AD_PInstance_ID=? " +
				" AND T_Selection.T_Selection_ID=PP_ForecastRunResult.PP_ForecastRunResult_ID)";		
		
		Collection <MPPForecastRunResult> FCRunResults = new Query(getCtx(), I_PP_ForecastRunResult.Table_Name, whereClause, get_TrxName())
										.setOrderBy("PP_ForecastRunResult_ID")
										.setClient_ID()
										.setParameters(new Object[]{getAD_PInstance_ID()})
										.list();
		
		for (MPPForecastRunResult FCRunResult : FCRunResults)
		{
			if (FCRunResult.getQtyCalculated().compareTo(Env.ZERO)>0)
			{
				MPPForecastRunMaster frm = new MPPForecastRunMaster(getCtx(), FCRunResult.getPP_ForecastRunMaster_ID(), get_TrxName());
				MPPPeriod pp = new MPPPeriod (getCtx(), FCRunResult.getPP_Period_ID(), get_TrxName());
				
				if (count==0)
				{//create new SO
					m_order = new MOrder(getCtx(), 0, get_TrxName());
					m_order.setAD_Org_ID(p_AD_Org_ID);
					m_order.setM_Warehouse_ID(frm.getM_Warehouse_ID());
	 				m_order.setDatePromised(pp.getStartDate());
					m_order.setIsSOTrx(true);
					
					m_order.setC_DocTypeTarget_ID(p_C_DocType_ID);
	 				m_order.setC_BPartner_ID(frm.getC_BPartner_ID());
					m_order.setM_PriceList_ID(frm.getC_BPartner().getM_PriceList_ID());
	 				m_order.setDescription("Added from Forecalst Simulation!");
					if (!m_order.save())
						throw new IllegalStateException("Could not create Order!");
				}//create lines
				m_orderLine = new MOrderLine(m_order);
				m_orderLine.setAD_Org_ID(p_AD_Org_ID);
				m_orderLine.setDatePromised(pp.getStartDate());
				m_orderLine.setM_Product_ID(frm.getM_Product_ID()); 
				m_orderLine.setM_Warehouse_ID(frm.getM_Warehouse_ID());
				m_orderLine.setQtyEntered(FCRunResult.getQtyCalculated());
				m_orderLine.setQtyOrdered(FCRunResult.getQtyCalculated());
//				m_orderLine.setPrice(Env.ONE);
//				m_orderLine.setPriceActual(Env.ONE);
//				m_orderLine.setPriceList(Env.ONE);
				m_orderLine.setLineNetAmt(FCRunResult.getQtyCalculated());
	 			m_orderLine.setDescription("Added from Forecalst Simulation!");
				m_orderLine.saveEx();	
			}
			count++;
		}
		
		createdRecord=count;
		result.append("@M_Forecast_ID@ # @Deleted@ = ").append(deletedRecord)
				.append(" @Updated@ = ").append(updatedRecord)
				.append(" @Created@ = ").append(createdRecord);
		return result.toString();
	}
		
}
