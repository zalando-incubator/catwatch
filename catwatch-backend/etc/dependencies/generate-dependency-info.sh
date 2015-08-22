
rm -f generated-runtime-deps-list.txt
rm -f generated-runtime-deps-tree.txt

rm -f generated-test-deps-list.txt
rm -f generated-test-deps-tree.txt

cd ../..

mvn dependency:list -DincludeScope=runtime -DoutputFile=etc/dependencies/generated-runtime-deps-list.txt -Dsort=true
mvn dependency:tree -Dscope=runtime -DoutputFile=etc/dependencies/generated-runtime-deps-tree.txt

mvn dependency:list -DincludeScope=test -DoutputFile=etc/dependencies/generated-test-deps-list.txt -Dsort=true
mvn dependency:tree -Dscope=test -DoutputFile=etc/dependencies/generated-test-deps-tree.txt
