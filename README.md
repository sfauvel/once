once
====
Version g�rer via GitHub/CloudBees

Ce programme permet de chercher les redondances dans un programme.
Il ne cherche pas les copier/coller mais les structures de programme identique. 
Des codes seront consid�r�s identiques m�me si les nom de variables, m�thodes ou classes sont diff�rents. 

Cette version est fonctionnelle mais est pour l'instant tr�s rudimentaire. N'h�sitez pas � donner votre feedback. 

Pour constuire le programme, ex�cuter la commande:
mvn compile assembly:single

Ex�cuter le programme avec la commande (apr�s avoir renommer le jar pr�sent dans target):
java -jar ./once-0.0.1-beta.jar %SRC_DIR%

La variable %SRC_DIR% correspond au r�pertoire contenant les sources � analyser.
Un r�pertoire result est cr�� et contient un fichier once.txt contenant le r�sultat.
Le format de sortie est :

Taille:317 Longueur:2 Substitutions:1
  33% 207 lignes(ReportingImpl.java:1406) <-> (ReportingImpl.java:1476) display(1377 <-> 1584) 
  32% 207 lignes(ReportingImpl.java:1478) <-> (ReportingImpl.java:1545) display(1377 <-> 1584) 
    2 valeurs: else, conn

Premi�re ligne:
	Taille: nombre d'�l�ment unitaire qui sont identiques
	Longueur: nombre de zone contenant la m�me structure
	Substitution: nombre d'�l�ments qui ont fait l'objet d'une substitution. O correspond � un copi�/coller

Liste des zones dupliqu�es
	Pourcentage: pourcentage de la m�thode qui est dupliqu�
	Nombre de lignes: Le nombre de lignes dupliqu�es
	Emplacement de d�but: Nom du fichier et de la ligne de d�but
	Emplacement de fin: Nom du fichier et de la ligne de fin
	Nom de la m�thode avec la ligne de d�but et de fin
	
Liste des substitutions
	Nombre de valeurs dff�rentes
	Liste des valeurs qui ont fait l'objet d'une substitution

====
Reste � faire

- Param�trer les crit�res de pertinence incluant les substitutions, le taux de redondance.
- Calculer le taux de duplication entre 2 m�thodes en comptant l'ensemble des redondance m�mes non cons�cutives 
- Lorsqu'une variable porte le nom d'une m�thode, on consid�re qu'il s'agit du m�me �l�ment. Cela entraine une coupure dans la recherche !!!
- Faire la partie de parsing des fichier en multithread�
- Passer � slf4J / logback
- G�rer les param�tres d'entr�e du programme

 