#!/bin/bash

if [ -z $POSTGRES_HOME ]; then
    echo "System variable POSTGRES_HOME has not been set"
    exit 1
fi

PSQL=$POSTGRES_HOME/bin/psql

$PSQL -U postgres -f ../../core/dal/src/main/resources/sql/1.0/postgres/drop_create_database.sql
result=$?
if [ $result -ne 0 ]
then
   echo "Re-create Database script unsuccessful: " $result
   exit $result
fi

temp_dir="generated"
if [ ! -d "$temp_dir" ]
then
    mkdir "$temp_dir"
fi

export PGPASSWORD=egoods

generate-sql.sh $temp_dir egoods
$PSQL -U egoods -f $temp_dir/egoods.sql
result=$?
if [ $result -ne 0 ]
then
   echo "Schema recreation script unsuccessful: " $result
   exit $result
fi
rm $temp_dir/egoods.sql

generate-sql.sh $temp_dir test
$PSQL -U egoods -f $temp_dir/test.sql
result=$?
if [ $result -ne 0 ]
then
   echo "Schema recreation script unsuccessful: " $result
   exit $result
fi
rm $temp_dir/test.sql
rmdir "$temp_dir"