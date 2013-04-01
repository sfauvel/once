once
====
Ce programme permet de chercher les redondances dans un programme.

====
Reste à faire

- supprimer les redondances qui se chevauche (un bout de code ne peut pas être redondant avec lui même)
- Paramétrer les critères de pertinence incluant les substitutions, le taux de redondance.
- Calculer le taux de duplication entre 2 méthodes en comptant l'ensemble des redondance mêmes non consécutives 
- Lorsqu'une variable porte le nom d'une méthode, on considère qu'il s'agit du même élément. Cela entraine une coupure dans la recherche !!!
- Gérer les redondances sans substitution sauf pour les String
- Faire la partie de parsing des fichier en multithreadé
- Passer à slf4J / logback
- Gérer les paramètres d'entrée du programme

 