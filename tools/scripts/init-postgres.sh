#!/bin/bash

if [ -z $POSTGRES_HOME ]; then
    echo "System variable POSTGRES_HOME has not been set"
    exit 1
fi

PSQL=$POSTGRES_HOME/bin/psql

$PSQL -U postgres -f ../../core/dal/src/main/resources/sql/1.0/postgres/create_database.sql
result=$?
if [ $result -ne 0 ]
then
   echo "Create Database script unsuccessful: " $result
   exit $result
fi

export PGPASSWORD=egoods
$PSQL -U egoods -f ../../core/dal/src/main/resources/sql/1.0/postgres/schema.sql
result=$?
if [ $result -ne 0 ]
then
   echo "Schema creation script unsuccessful: " $result
   exit $result
fi

