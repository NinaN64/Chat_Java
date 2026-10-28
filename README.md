## Chat aplication

### Main goal
A simple multi-client chat room in Java. One server accepts many clients over TCP (default port 2020), and every message a client sends is broadcast to everyone in the room.


### Run the server
java -jar server.jar

### Run a client
java -jar client.jar [host] [port]

(Both arguments are optional. The defaults are localhost and 2020.)