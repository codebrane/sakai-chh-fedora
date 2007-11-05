mvn deploy:deploy-file \
    -Dfile=/Users/alistair/.m2/repository/fedora/fedora-foxml/1.0/fedora-foxml-1.0.jar \
    -DgroupId=fedora \
    -DartifactId=fedora-foxml \
    -Dversion=1.0 \
    -Dpackaging=jar \
    -DgeneratePom=true \
    -DrepositoryId=source.uhi.ac.uk-repository \
    -Durl=dav:http://source.uhi.ac.uk/mvn
