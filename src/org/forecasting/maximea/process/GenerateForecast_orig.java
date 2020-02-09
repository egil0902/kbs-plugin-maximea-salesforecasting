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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_M_Forecast;
import org.compiere.model.I_M_ForecastLine;
import org.compiere.model.I_M_Product;

import org.compiere.model.MForecast;
import org.compiere.model.MForecastLine;
import org.compiere.model.MSequence;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.forecasting.maximea.model.I_PP_ForecastRun;
import org.forecasting.maximea.model.MPPForecastRun;

/**
 * This process allows to generate a forecast based on the calculation of the
 * simulation of a forecast.
 * 
 * @author victor.perez@e-evolution.com, www.e-evolution.com
 */
public class GenerateForecast_orig extends SvrProcess {

	private int p_M_Forecast_ID;
	private int p_PP_ForecastRun_ID;
	private Timestamp p_StartDate;
	private Timestamp p_EndDate;
	private String p_ForecastActionType;
	private String p_ForecastLoadType;
	private int p_DaysAfterDue;
	private int p_M_Product_ID;
	private int p_M_Product_Category_ID;
	private int p_M_Product_Classification_ID;
	private int p_M_Product_Class_ID;
	private int p_M_Product_Group_ID;
	private int p_SalesRep_ID;

	/**
	 * Prepare - e.g., get Parameters.
	 */
	protected void prepare() {
		for (ProcessInfoParameter para : getParameter()) {
			String name = para.getParameterName();
			if (para.getParameter() == null)
				;
			else if (name.equals(I_M_Forecast.COLUMNNAME_M_Forecast_ID))
				p_M_Forecast_ID = para.getParameterAsInt();
			else if (name.equals(I_PP_ForecastRun.COLUMNNAME_PP_ForecastRun_ID))
				p_PP_ForecastRun_ID = para.getParameterAsInt();
			else if (name.equals(I_M_ForecastLine.COLUMNNAME_DatePromised)) {
				p_StartDate = (Timestamp) para.getParameter();
				p_EndDate = (Timestamp) para.getParameter_To();
			} else if (name.equals("ForecastActionType"))
				p_ForecastActionType = (String) para.getParameter();
			else if (name.equals("ForecastLoadType"))
				p_ForecastLoadType = (String) para.getParameter();
			else if (name.equals("DaysAfterDue"))
				p_DaysAfterDue = para.getParameterAsInt();
			else if (name.equals(I_M_Product.COLUMNNAME_M_Product_ID))
				p_M_Product_ID = para.getParameterAsInt();
			else if (name.equals(I_M_Product.COLUMNNAME_M_Product_Category_ID))
				p_M_Product_Category_ID = para.getParameterAsInt();
			else if (name.equals(I_M_ForecastLine.COLUMNNAME_SalesRep_ID))
				p_SalesRep_ID = para.getParameterAsInt();
			else
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

		if (p_ForecastLoadType==null) p_ForecastLoadType="S";
		if (p_ForecastActionType==null) p_ForecastActionType="R";
		
		StringBuilder result = new StringBuilder();
		result.append("!!! ");
		// Validate Data
		MForecast forecast = new MForecast(getCtx(), p_M_Forecast_ID,
				get_TrxName());
		MPPForecastRun fr = new MPPForecastRun(getCtx(), p_PP_ForecastRun_ID,
				get_TrxName());

		if (fr.getAD_Org_ID() != forecast.getAD_Org_ID())
		{
//			result.append("@AD_Org_ID@  @NotMatched@ @M_Forecast_ID@");
//			throw new AdempiereException(
//					"@AD_Org_ID@  @NotMatched@ @M_Forecast_ID@");
		}
		
//		if (getC_Calendar_ID(fr.getPP_Calendar_ID()) != forecast.getC_Calendar_ID())
//		{
//			result.append("<"+fr.getPP_Calendar_ID()+">"+getC_Calendar_ID(fr.getPP_Calendar_ID()) +"|@PP_Calendar_ID@  @NotMatched@ @M_Forecast_ID@|"+forecast.getC_Calendar_ID());
//			throw new AdempiereException(
//					"@PP_Calendar_ID@  @NotMatched@ @M_Forecast_ID@");
//		}
		
		if (getC_Year_ID(fr.getPP_PeriodDefinition_ID()) != forecast.getC_Year_ID())
//				.getC_PeriodDefinition_ID())
		{
			result.append("@PP_PeriodDefinition_ID@ @NotMatched@ @M_Forecast_ID@");
//			throw new AdempiereException(
//					"@PP_PeriodDefinition_ID@ @NotMatched@ @M_Forecast_ID@");
		}

		int deletedRecord = 0;
		int updatedRecord = 0;
		int createdRecord = 0;

		if (p_SalesRep_ID <= 0)
			p_SalesRep_ID = Env.getAD_User_ID(getCtx());

		if ("R".equals(p_ForecastActionType)) {
			deletedRecord = deleteData();
			createdRecord = insertForecast();
		} else if ("M".equals(p_ForecastActionType)) {
			updatedRecord = updateForecast();
			createdRecord = insertForecast();
		}

		String desc = fr.getDocumentNo() + " " + fr.getDescription();
		forecast.setDescription(forecast.getDescription() != null ? forecast
				.getDescription() + " " + desc : desc);
		forecast.saveEx();
		fr.setProcessed(true);
		fr.saveEx();

		result.append("@M_Forecast_ID@ # @Deleted@ = ").append(deletedRecord)
				.append(" @Updated@ = ").append(updatedRecord)
				.append(" @Created@ = ").append(createdRecord);
		return result.toString();
	}

	/**
	 * Insert the forecast line based on forecast simulation
	 * 
	 * @return records inserted
	 */
	private int insertForecast() {
		List<Object> parameters = new ArrayList<Object>();
		StringBuilder insert = new StringBuilder();
		insert.append("INSERT INTO ").append(MForecastLine.Table_Name)
				.append(" (");
		insert.append(MForecastLine.COLUMNNAME_M_ForecastLine_ID).append(",");//1
		insert.append(MForecastLine.COLUMNNAME_M_Forecast_ID).append(",");//2
		insert.append(MForecastLine.COLUMNNAME_AD_Client_ID).append(",");//3
		insert.append(MForecastLine.COLUMNNAME_AD_Org_ID).append(",");//4
		insert.append(MForecastLine.COLUMNNAME_M_Warehouse_ID).append(",");//5
		insert.append(MForecastLine.COLUMNNAME_M_Product_ID).append(",");//6
		insert.append(MForecastLine.COLUMNNAME_QtyCalculated).append(",");//7
		insert.append(MForecastLine.COLUMNNAME_Qty).append(",");//8
		insert.append(MForecastLine.COLUMNNAME_DatePromised).append(",");//9
		insert.append(MForecastLine.COLUMNNAME_C_Period_ID).append(",");//10
		insert.append(MForecastLine.COLUMNNAME_SalesRep_ID).append(",");//11
		insert.append(MForecastLine.COLUMNNAME_Created).append(",");//12
		insert.append(MForecastLine.COLUMNNAME_CreatedBy).append(",");//13
		insert.append(MForecastLine.COLUMNNAME_Updated).append(",");//14
		insert.append(MForecastLine.COLUMNNAME_UpdatedBy).append(")");//15

		insert.append(" SELECT ");
		insert.append("nextidfunc(")
				.append(MSequence.get(getCtx(), MForecastLine.Table_Name)//1
						.get_ID()).append(",'N')").append(",");
		insert.append(p_M_Forecast_ID).append(",");//2
		insert.append("frm.AD_Client_ID,");//3
		insert.append("frm.AD_Org_ID,");//4
		insert.append("frm.M_Warehouse_ID,");//5
		insert.append("frm.M_Product_ID,");//6
		insert.append("frr.QtyCalculated,");//7
		insert.append("NVL(frr.QtyPlan,0) + NVL(frr.QtyAbnormal,0),");//8
		if (p_ForecastLoadType.equals("S"))
			insert.append("adddays(fp.StartDate,").append(p_DaysAfterDue)//9
					.append("),");
		if (p_ForecastLoadType.equals("E"))
			insert.append("adddays(fp.EndDate,").append(p_DaysAfterDue)//9
					.append("),");
		
		insert.append(" (SELECT cp.C_Period_ID FROM C_Period cp WHERE (SELECT  pp1.startdate FROM PP_Period pp1"
				+ " WHERE pp1.PP_Period_ID=frr.PP_Period_ID)  BETWEEN   cp.startdate AND cp.enddate), ");//10		
		
//		insert.append("frr.PP_Period_ID,");//10
		insert.append(p_SalesRep_ID).append(",");//11
		insert.append("SYSDATE").append(",");//12
		insert.append(Env.getAD_User_ID(getCtx())).append(",");//13
		insert.append("SYSDATE").append(",");//14
		insert.append(Env.getAD_User_ID(getCtx()));//15
		insert.append(" FROM PP_ForecastRunMaster frm ");
		insert.append(" INNER JOIN PP_ForecastRunResult frr ON (frr.PP_ForecastRunMaster_ID=frm.PP_ForecastRunMaster_ID) ");
		insert.append(" INNER JOIN PP_Period fp ON (fp.PP_Period_ID=frr.PP_Period_ID) ");
		insert.append(" INNER JOIN M_Product p ON (p.M_Product_ID=frm.M_Product_ID) ");
		insert.append(" INNER JOIN M_Product_Category pc ON (pc.M_Product_Category_ID=p.M_Product_Category_ID)");

		StringBuilder whereClause = new StringBuilder(
				" WHERE frr.QtyCalculated  > 0 AND NOT EXISTS (SELECT 1 FROM M_ForecastLine fl WHERE fl.M_Forecast_ID=? AND ");
		whereClause.append("fl.M_Product_ID=frm.M_Product_ID AND ");
		whereClause.append("fl.M_Warehouse_ID=frm.M_Warehouse_ID AND ");
 		whereClause.append("fl.C_Period_ID=frr.PP_Period_ID) AND ");
		parameters.add(p_M_Forecast_ID);

		insert.append(getWhere(whereClause.toString(), parameters)).append(
				" ORDER BY fp.PeriodNo");
		return DB.executeUpdateEx(insert.toString(), parameters.toArray(),
				get_TrxName());
	}

	/**
	 * Update the forecast line from forecast simulation
	 * 
	 * @return records updated
	 */
	private int updateForecast() {
		List<Object> parameters = new ArrayList<Object>();
		StringBuilder update = new StringBuilder();

		update.append("UPDATE ").append(MForecastLine.Table_Name);
		update.append(" SET ");
		update.append(" QtyCalculated = NVL(M_ForecastLine.QtyCalculated,0) + NVL(frr.QtyCalculated,0),");
		update.append(" Qty =  NVL(M_ForecastLine.Qty,0) + NVL(frr.QtyPlan,0) + NVL(frr.QtyAbnormal,0)");
		update.append(" FROM PP_ForecastRunMaster frm ");
		update.append(" INNER JOIN PP_ForecastRunResult frr ON (frr.PP_ForecastRunMaster_ID=frm.PP_ForecastRunMaster_ID) ");
		update.append(" INNER JOIN PP_Period fp ON (fp.PP_Period_ID=frr.PP_Period_ID) ");
		update.append(" INNER JOIN M_Product p ON (p.M_Product_ID=frm.M_Product_ID) ");
		update.append(" INNER JOIN M_Product_Category pc ON (pc.M_Product_Category_ID=p.M_Product_Category_ID)");
		update.append(" LEFT  JOIN M_Product_Classification pcl ON (pcl.value=p.Classification) ");
		update.append(" LEFT  JOIN M_Product_Class pclass ON (pclass.value=p.Group1) ");
		update.append(" LEFT  JOIN M_Product_Group pg ON (p.value=p.Group2) ");

		StringBuilder whereClause = new StringBuilder(
				" WHERE M_ForecastLine.M_Forecast_ID=?  AND ");
		whereClause
				.append(" M_ForecastLine.M_Product_ID = frm.M_Product_ID AND ");
		whereClause
				.append(" M_ForecastLine.M_Warehouse_ID=frm.M_Warehouse_ID AND ");
		whereClause
				.append(" M_ForecastLine.PP_Period_ID=frr.PP_Period_ID AND ");
		parameters.add(p_M_Forecast_ID);

		update.append(getWhere(whereClause.toString(), parameters));
		return DB.executeUpdateEx(update.toString(), parameters.toArray(),
				get_TrxName());

	}

	/**
	 * Get Sql based on the parameters
	 * 
	 * @param where
	 * @param parameters
	 * @return SQL with apply the parameters
	 */
	private String getWhere(String where, List<Object> parameters) {
		StringBuilder whereClause = new StringBuilder(where);

		whereClause.append("frm.PP_ForecastRun_ID=? AND frm.AD_Client_ID=? ");
		parameters.add(p_PP_ForecastRun_ID);
		parameters.add(getAD_Client_ID());

		if (p_ForecastLoadType.equals("S"))
			whereClause.append("AND adddays(fp.StartDate,")
					.append(p_DaysAfterDue).append(") BETWEEN ?  AND ?  ");
		if (p_ForecastLoadType.equals("E"))
			whereClause.append("AND adddays(fp.EndDate,")
					.append(p_DaysAfterDue).append(")  BETWEEN ?  AND ?  ");

		parameters.add(p_StartDate);
		parameters.add(p_EndDate);

		// Product Dimension
		if (p_M_Product_ID > 0) {
			whereClause.append("AND frm.M_Product_ID=? ");
			parameters.add(p_M_Product_ID);
		}

		if (p_M_Product_Category_ID > 0) {
			whereClause.append("AND pc.M_Product_Category_ID=? ");
			parameters.add(p_M_Product_Category_ID);
		}

		if (p_M_Product_Classification_ID > 0) {
			whereClause.append("AND pcl.M_Product_Classification_ID=? ");
			parameters.add(p_M_Product_Classification_ID);
		}

		if (p_M_Product_Class_ID > 0) {
			whereClause.append("AND pclass.M_Product_Class_ID=? ");
			parameters.add(p_M_Product_Class_ID);
		}

		if (p_M_Product_Group_ID > 0) {
			whereClause.append("AND pg.M_Product_Group_ID=? ");
			parameters.add(p_M_Product_Group_ID);
		}
		return whereClause.toString();
	}

	/**
	 * Delete forecast line
	 * 
	 * @return no records deleted
	 */
	private int deleteData() {
		StringBuilder updateSQL = new StringBuilder();
		updateSQL.append("DELETE FROM M_ForecastLine WHERE M_Forecast_ID=")
				.append(p_M_Forecast_ID);
		return DB.executeUpdateEx(updateSQL.toString(), get_TrxName());

	}
	
	/**
	 * get C_Year_ID
	 * @return C_Year_ID
	 */
	private int getC_Year_ID(int PP_Year_ID) {
		return DB.getSQLValue (get_TrxName(), "SELECT cy.C_Year_ID FROM C_Year cy WHERE cy.isActive='Y' AND cy.fiscalyear IN "
				+ " (SELECT  MAX(ppd.CalendarYear) FROM PP_PeriodDefinition ppd WHERE ppd.PP_PeriodDefinition_ID="+PP_Year_ID+");");
	}	
		
}
