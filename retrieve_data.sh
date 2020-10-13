server='ironlady'
folder_name='2020-10-13__18.10.43__2'
file_names=(
	"2020-10-13__18.10.43__general2__data_ingestion.xml"
	"2020-10-13__18.10.58__test_221__data_ingestion.xml"
	"2020-10-13__18.11.51__test_232__data_ingestion.xml"
	"2020-10-13__18.10.43__test_211__data_ingestion.xml"
	"2020-10-13__18.10.59__test_222__data_ingestion.xml"
	"2020-10-13__18.12.04__test_233__data_ingestion.xml"
	"2020-10-13__18.10.48__test_212__data_ingestion.xml"
	"2020-10-13__18.11.00__test_223__data_ingestion.xml"
	"2020-10-13__18.10.53__test_213__data_ingestion.xml"
	"2020-10-13__18.11.01__test_231__data_ingestion.xml"
)
mkdir logs/from_${server}/${folder_name}
for i in "${file_names[@]}"
do
	echo $i
  pscp sfracalossi@${server}.inf.unibz.it:/data/sfracalossi/standalone_ingestion/logs/${folder_name}/${i} logs/from_${server}/${folder_name}/${i}
done
