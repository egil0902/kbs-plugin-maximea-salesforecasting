/**
 * This file is part of iDempiere ERP <http://www.idempiere.org>.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Copyright (c) 2016 Saúl Piña <sauljabin@gmail.com>.
 */

package org.forecasting.maximea.component;

import org.forecasting.maximea.base.CustomModelFactory;

/**
 * Model Factory
 */
public class ModelFactory extends CustomModelFactory {

	/**
	 * For initialize class. Register the models to build
	 * 
	 * <pre>
	 * protected void initialize() {
	 * 	registerTableModel(MTableExample.Table_Name, MTableExample.class);
	 * }
	 * </pre>
	 */
	@Override
	protected void initialize() {
 		registerTableModel(org.forecasting.maximea.model.MSalesHistory.Table_Name, org.forecasting.maximea.model.MSalesHistory.class);
 		
 		registerTableModel(org.forecasting.maximea.model.MPPCalendar.Table_Name, org.forecasting.maximea.model.MPPCalendar.class);
 		registerTableModel(org.forecasting.maximea.model.MPPPeriodDefinition.Table_Name, org.forecasting.maximea.model.MPPPeriodDefinition.class);
 		registerTableModel(org.forecasting.maximea.model.MPPPeriod.Table_Name, org.forecasting.maximea.model.MPPPeriod.class);
 		
 		registerTableModel(org.forecasting.maximea.model.MPPForecastRule.Table_Name, org.forecasting.maximea.model.MPPForecastRule.class);
 		
 		registerTableModel(org.forecasting.maximea.model.MPPForecastDefinition.Table_Name, org.forecasting.maximea.model.MPPForecastDefinition.class);
 		registerTableModel(org.forecasting.maximea.model.MPPForecastDefinitionLine.Table_Name, org.forecasting.maximea.model.MPPForecastDefinitionLine.class);
 		
 		registerTableModel(org.forecasting.maximea.model.MPPForecastRun.Table_Name, org.forecasting.maximea.model.MPPForecastRun.class);
 		registerTableModel(org.forecasting.maximea.model.MPPForecastRunDetail.Table_Name, org.forecasting.maximea.model.MPPForecastRunDetail.class);
 		registerTableModel(org.forecasting.maximea.model.MPPForecastRunLine.Table_Name, org.forecasting.maximea.model.MPPForecastRunLine.class);
 		registerTableModel(org.forecasting.maximea.model.MPPForecastRunMaster.Table_Name, org.forecasting.maximea.model.MPPForecastRunMaster.class);
 		registerTableModel(org.forecasting.maximea.model.MPPForecastRunResult.Table_Name, org.forecasting.maximea.model.MPPForecastRunResult.class);

	}

}
