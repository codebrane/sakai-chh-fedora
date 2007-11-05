mvn deploy:deploy-file \
    -Dfile=/Users/alistair/.m2/repository/fedora/fedora-api-a-client/2.2.1/fedora-api-a-client-2.2.1.jar \
    -DgroupId=fedora \
    -DartifactId=fedora-api-a-client \
    -Dversion=2.2.1 \
    -Dpackaging=jar \
    -DgeneratePom=true \
    -DrepositoryId=source.uhi.ac.uk-repository \
    -Durl=dav:http://source.uhi.ac.uk/mvn

mvn deploy:deploy-file \
    -Dfile=/Users/alistair/.m2/repository/fedora/fedora-api-m-client/2.2.1/fedora-api-m-client-2.2.1.jar \
    -DgroupId=fedora \
    -DartifactId=fedora-api-m-client \
    -Dversion=2.2.1 \
    -Dpackaging=jar \
    -DgeneratePom=true \
    -DrepositoryId=source.uhi.ac.uk-repository \
    -Durl=dav:http://source.uhi.ac.uk/mvn
