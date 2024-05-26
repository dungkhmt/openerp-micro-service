#!/bin/bash
data_path=$(pwd)
echo "all file in $data_path/scraped_data will be deleted, you're sure about that? (y/n)"
read choice
if [[ $choice == "y" ]]; then
    rm $data_path/scraped_data/*
    echo "all file has been deleted"
else 
    echo "command aborted, quit now!"
fi