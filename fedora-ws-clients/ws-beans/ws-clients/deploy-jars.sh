mvn deploy:deploy-file \
    -Dfile=build/api-a/client/3.0-b1/dist/fedora-api-a-client-3.0-b1.jar \
    -DgroupId=fedora \
    -DartifactId=fedora-api-a-client \
    -Dversion=3.0-b1 \
    -Dpackaging=jar \
    -DgeneratePom=true \
    -DrepositoryId=source.uhi.ac.uk-repository \
    -Durl=dav:http://source.uhi.ac.uk/mvn

mvn deploy:deploy-file \
    -Dfile=build/api-m/client/3.0-b1/dist/fedora-api-m-client-3.0-b1.jar \
    -DgroupId=fedora \
    -DartifactId=fedora-api-m-client \
    -Dversion=3.0-b1 \
    -Dpackaging=jar \
    -DgeneratePom=true \
    -DrepositoryId=source.uhi.ac.uk-repository \
    -Durl=dav:http://source.uhi.ac.uk/mvn
