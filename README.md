# extractor
## SAP ASE BLOB extractor to files

Usage: `java Extract -u <USER> -p <PASSWORD> -s <SERVER:PORT> -d <DATABASE> -q "<QUERY WHICH RETURNS (FILEPATH, BLOB)>"`
  
  example:
  ```
  java Extract -u sa -p sapass -s ZENBOOK:5000 -d pubs2 -q "select 'c:\tmp\extract\' || au_id || '.' || format_type as fname, pic from au_pix"
  ```
