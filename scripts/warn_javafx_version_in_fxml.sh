#!/bin/bash

cd ../src/main/resources/fxml || exit 1

# Error if we dont find version 21 in the file
# check 3 levels
for fxml_file in *.fxml */*.fxml */*/*.fxml
do
  grep -E -i "https?://javafx.com/javafx/21" $fxml_file
  if [[ $? -ne 0 ]]
  then
    echo File contains invalid javafx version: $fxml_file
    exit 1
  fi
done