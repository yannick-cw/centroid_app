# the server to the centroid app

## install

* install maven on your local machine
* set the following global variables

```
  CENTROID_SERVER_GPS_DATA_FILE = path to a save file
  CENTROID_SERVER_USER_LIST = path to a save file  
```
* go to the parent directory of the server
* write in your console:
```
mvn tomcat7:run 2>&1 | tee catalina.out
```
* Run with screen

```
ctrl-a + d
to detach screen
screen -r 
to reattach
```

* you find the log in catalina.out
* enjoy!
