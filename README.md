# KBS Plugin : Maximea Sales Forecasting

The fork from https://bitbucket.org/maximeaerp/org.maximea.forecasting, and converted to be installed via **KBS ObjectData Tool** 

Please refer to https://wiki.idempiere.org/en/Plugin:_Sales_Forecasting

## How to install

1. Install **KBS ObjectData Tool** (refer to http://wiki.idempiere.org/en/Plugin:_ObjectDataTool)

2. Install the plugin via Apache Felix Web Console

## How to use

The records for pp_forecastrule have to be inserted manually, check SQLStatement in PackOut.xml. (Why can't export by 2Pack in Window **Forecast Rule** ?)

## Fixing

Error from 2Pack:
```
09:53:54.938===========> MColumn.saveError: FillMandatory - Reference Key [127],
09:53:54.939-----------> MColumn.save: beforeSave failed - MColumn[0-PP_ForecastDefinition_ID] [127],
09:53:54.939 Trx.rollback: **** PipoDS_fde288a3-1152-48ac-89bf-762a9da3d378 [127],
09:53:54.960===========> PackIn.importXML: importXML: [127],
org.adempiere.pipo2.exception.POSaveFailedException: Failed to save column Forecast Definition,
```

Workaround:

Change `<AD_Reference_ID reference="id">18</AD_Reference_ID>` to `<AD_Reference_ID reference="id">19</AD_Reference_ID>` 

18 (Reference Table) is not working if without Reference Key Setup, change to 19 (Reference TableDirect). 2Pack issue for Column Handler also?

## TODO
