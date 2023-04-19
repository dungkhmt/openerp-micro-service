#!/bin/bash
mkdir -p admin-20230413230437
cd admin-20230413230437
cat <<EOF >> main.cpp
int main(){
    int a = 6;
    int b =10;
    return 0;
}
EOF
g++ -w -o main main.cpp
FILE=main
if test -f "$FILE"; then
  echo Successful
else
  echo Compile Error
fi
cd .. 
rm -rf admin-20230413230437 & 
rm -rf admin-20230413230437.sh & 
