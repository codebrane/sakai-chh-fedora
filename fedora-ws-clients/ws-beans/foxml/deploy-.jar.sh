mvn deploy:deploy-file \
    -Dfile=target/fedora-foxml-1.1.jar \
    -DgroupId=fedora \
    -DartifactId=fedora-foxml \
    -Dversion=1.1 \
    -Dpackaging=jar \
    -DgeneratePom=true \
    -DrepositoryId=source.uhi.ac.uk-repository \
    -Durl=dav:http://source.uhi.ac.uk/mvn
