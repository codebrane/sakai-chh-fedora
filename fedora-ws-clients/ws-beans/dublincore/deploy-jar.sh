mvn deploy:deploy-file \
    -Dfile=/Users/alistair/.m2/repository/dublincore/dublincore/2002-12-12/dublincore-2002-12-12.jar \
    -DgroupId=dublincore \
    -DartifactId=dublincore \
    -Dversion=2002-12-12 \
    -Dpackaging=jar \
    -DgeneratePom=true \
    -DrepositoryId=source.uhi.ac.uk-repository \
    -Durl=dav:http://source.uhi.ac.uk/mvn
