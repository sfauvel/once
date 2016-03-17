once
====

Vous chercher des duplications dans votre code ? Ce projet est pour vous.

* Aller dans le répertoire des releases: https://github.com/sfauvel/once/releases/latest/
* Télécharger le fichier once-ihm-v[VERSION].jar
* Exécuter le programme avec la commande: java -jar once-ihm-v[VERSION].jar


Il ne cherche pas les copier/coller mais les structures de programme identique. 
Des codes seront considérés identiques même si les nom de variables, méthodes ou classes sont différents. 

Cette version est fonctionnelle mais elle est pour l'instant très rudimentaire. N'hésitez pas à donner votre feedback. 

Pour constuire le programme, exécuter la commande:
mvn compile assembly:single

Exécuter le programme avec la commande (après avoir renommer le jar présent dans target):
java -jar ./once-<version>.jar %SRC_DIR%

La variable %SRC_DIR% correspond au répertoire contenant les sources à analyser.
Un répertoire result est créé et contient un fichier once.txt contenant le résultat.
Le format de sortie est :

Taille:317 Longueur:2 Substitutions:1
  33% 207 lignes(ReportingImpl.java:1406) <-> (ReportingImpl.java:1476) display(1377 <-> 1584) 
  32% 207 lignes(ReportingImpl.java:1478) <-> (ReportingImpl.java:1545) display(1377 <-> 1584) 
    2 valeurs: else, conn
    
Tokens number:325 Duplications number:2 Substitutions number:2
  100% (61 of 61 lines)com.once.Class.method from line 375 to 436 (method from line 375 to 436)
  100% (63 of 63 lines)com.once.Other.method from line 161 to 224 (method from line 161 to 224)
    2 values: Value, value
    2 values: getValue, getMax
     
Première ligne:
	Tokens number: nombre d'élément unitaire qui sont identiques
	Duplication number: nombre de zone contenant la même structure
	Substitution number: nombre d'éléments qui ont fait l'objet d'une substitution. O correspond à un copié/coller

Liste des zones dupliquées
	Pourcentage: pourcentage de la méthode qui est dupliqué
	Nombre de lignes: Le nombre de lignes dupliquées sur le nombre de lignes de la méthode
	Nom de la méthode avec la ligne de début et de fin
	Emplacement de la ligne de début de la redondance
	Emplacement de la ligne de fin de la redondance
	Emplacement de début de la méthode:
	Emplacement de fin de la méthode:
	
Liste des substitutions
	Nombre de valeurs dfférentes
	Liste des valeurs qui ont fait l'objet d'une substitution

====
Reste à faire

- Paramétrer les critères de pertinence incluant les substitutions, le taux de redondance.
- Calculer le taux de duplication entre 2 méthodes en comptant l'ensemble des redondance mêmes non consécutives 
- Lorsqu'une variable porte le nom d'une méthode, on considère qu'il s'agit du même élément. Cela entraine une coupure dans la recherche !!!
- Faire la partie de parsing des fichier en multithreadé
- Passer à slf4J / logback
- Gérer les paramètres d'entrée du programme

 