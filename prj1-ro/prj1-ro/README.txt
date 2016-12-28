-------------------
Description : Projet de création d'album photo automatique par optimisation
Auteur      : S. Verel
Date        : 8/11/2015


-------------------
Liste des fichiers :

- buildAlbum.py               : code python permettant de créer les pages web avec l'album à partir d'un fichier de solution
- html                        : dossier pour recevoir les pages web avec l'album
- html/img/*.jpg              : les 55 photos au format jpg de l'album photo
- html/styleAlbum.css         : feuille de style associée aux pages web de l'album
- data/info-photo.json        : information sur les 55 photos au format json
- data/info-album.json        : information sur les 9 pages de l'album
- data/chronologic-order.sol  : fichier contenant une solution de disposition des photos de l'album (par ordre chronologique)
- java : dossier du code java donnant un exemple de lecture des données et de fonction d'évaluation
- cpp  : dossier du code c++ donnant un exemple de lecture des données et de fonction d'évaluation


-------------------
Création des pages :

python code/buildAlbum.py fichier.sol
où fichier.sol est le fichier contenant une solution de disposition

python code/buildAlbum.py
utilise par défaut le fichier data/chronologic-order.sol


-------------------
Exemple en java :

Pour compiler en ligne de commande depuis le dossier java :
javac -d bin -cp bin:src/jar/json-simple-1.1.1.jar -sourcepath src src/*.java

Pour exécuter :
java -cp bin:src/jar/json-simple-1.1.1.jar Main 


-------------------
Exemple en c++ :

Pour compiler en ligne de commande depuis le dossier cpp :
g++ -o build/main src/main.cpp

Pour exécuter :
./build/main
