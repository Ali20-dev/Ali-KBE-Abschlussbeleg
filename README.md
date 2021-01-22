# ALIS

## Anleitung für das bauen vom Dockerfile + deployment auf der derzeitigen Maschine

Voraussetzungen:
- docker, maven installiert
- Repo gecloned, git checkout songsWSb (damit der derzeitige Stand drauf ist!)

### Schritte
```
cd ALIS-KBE
eventuell: (sudo) chmod +x build_and_deploy.sh
./build_and_deploy.sh
```

## TODO
1. Ersetzen Sie die Überschrift mit `TEAMNAME`
2. Geben Sie Ihre Teammitglieder in die Tabelle ein
3. Folge den Anweisungen in der pom.xml
4. Folge **KBE Repository einrichten**
5. Erstelle die Branches (Groß- und Kleinschreibung beachten):
   - `runmerunner`
   - `songsservlet`
   - `songsWSa`
   - `songsWSb`

ALIS-KBE

| Name               | Matrikelnummer  |
| :------------      | --------------- |
| Ali Aslan          |     559932      |
| Muhammed Ali Geldi |     567021      |



## KBE Repository einrichten

Erstellen Sie ein leeres GitHub-Repository (kein `.gitignore`, `README.md` und `LICSENSE`) mit `TEAMNAME` als Repository-Name. Übertragen Sie dann den Inhalt des Template-Repository in Ihr erstelltes Repository.

```bash
git init
git add -A
git commit -m "init"
git remote add origin <YOUR GIT CLONE LINK>
git push -u origin master
```

## runmerunner clean, bauen, testen & verpacken
```
mvn -pl runmerunner clean package 
```

ODER

```
cd runmerunner
mvn clean package 
```



## songsservlet clean, bauen, testen & verpacken

```
mvn -pl songsservlet clean package 
```

ODER

```
cd songsservlet
mvn clean package 
```



## songsWS clean, bauen, testen & verpacken
```
mvn -pl songsWS clean package 
```

ODER

```
cd songsWS 
mvn clean package
```



## Alle Projekte clean, bauen, testen & verpacken: > mvn clean package

```
mvn clean package
```






