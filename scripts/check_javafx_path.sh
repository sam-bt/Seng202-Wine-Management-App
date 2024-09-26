#!/bin/bash
cd ../src/main/resources/fxml || exit 1

# Sometimes scenebuilder makes a path that doesnt exist when built
# check 3 levels
for fxml_file in *.fxml */*.fxml */*/*.fxml
do
  grep -E -i "@../../../../build" $fxml_file
  if [[ $? -eq 0 ]]
  then
    echo JavaFX file contains an path that may break when built as jar: $fxml_file
    echo consider renaming to /src/main/...
    exit 1
  fi
done