awk '{ sub("\r$", ""); print }' retrieve_data.sh > retrieve_data2.sh
mv retrieve_data2.sh retrieve_data.sh
bash retrieve_data.sh
