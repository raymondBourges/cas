# Notes RBO

## Start
* ./gradlew build --parallel -x test -DskipCheckstyle=true -x javadoc -DskipFindbugs=true
* java -jar ./webapp/cas-server-webapp/build/libs/cas-server-webapp-5.1.0-RC2-SNAPSHOT.war
* https://cas.cocktail.fr:8443/cas avec casuser/Mellon

## test implicit flow

* https://cas.cocktail.fr:8443/cas/oidc/authorize?response_type=id_token%20token&client_id=servimplicit&redirect_uri=https://localhost:8443/test&scope=openid%20profile&state=af0ifjsldkj&nonce=n-0S6_WzA2Mj

* --> Unsupported response type: id_token token --> :-(

## Conf services
* Copie des fichiers de config : "cp etc/cas/* /etc/cas/."

## Conf https

### https du serveur cas
* Ajout dans /etc/hosts de "127.0.0.1	cas.cocktail.fr"

* Génération du keystore "keytool -genkey -keyalg RSA -alias cas.cocktail.fr -keystore /etc/cas/thekeystore -storepass changeit -validity 360 -keysize 2048"

Quels sont vos nom et prénom ? --> cas.cocktail.fr

Quel est le nom de votre unité organisationnelle ? --> dev

Quel est le nom de votre entreprise ? --> cocktail

Quel est le nom de votre ville de résidence ? --> Rennes

Quel est le nom de votre état ou province ? --> Bretagne

Quel est le code pays à deux lettres pour cette unité ? --> FR

Est-ce CN=cas.cocktail.fr, OU=dev, O=cocktail, L=Rennes, ST=Bretagne, C=FR ? --> oui

Entrez le mot de passe de la clé pour <cas.cocktail.fr>
	(appuyez sur Entrée s'il s'agit du mot de passe du fichier de clés) : --> changeit

Ressaisissez le nouveau mot de passe : --> changeit

### enregistrement du certificat du https cas dans la JVM

* cd /tmp
* openssl s_client -connect cas.cocktail.fr:8443/cas -showcerts </dev/null 2>/dev/null|openssl x509 -outform PEM > cas.pem
* keytool -delete -alias cas -keystore /opt/bin/jdk/jdk1.8.0/jre/lib/security/cacerts -storepass changeit
* keytool -import -v -trustcacerts -alias cas -file cas.pem -keystore /opt/bin/jdk/jdk1.8.0/jre/lib/security/cacerts -storepass changeit
