REM example: 
REM extract.cmd -u sa -p sapass -s ZENBOOK:5000 -d pubs2 -q "select 'c:\tmp\extract\' || au_id || '.' || format_type as fname, pic from au_pix"
:: COMPILE
:: %SAP_JRE7%\bin\javac -cp %SAP_JRE7%\..\lib\jconn4.jar Extract.java 
:: RUN
set JAVA_HOME=%SAP_JRE7%;%JAVA_HOME%
%SAP_JRE7%\bin\java -cp %SAP_JRE7%\..\lib\jconn4.jar;. Extract %*