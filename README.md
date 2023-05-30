# Talon
note that the web server I wrote support two way testing to your convenience.

if you wanna test manually do the following:
First, run WireguardServer in your env./IDE

Then,
you can test manually with postman like this: POST: http://localhost:8080/wireguard-manual/add  
requestBody: {
    "tenantId": "1245",
    "peerId": "4",
    "overlayNetworkId":"12",
    "publicKey": "55"
}

you can also test removal manually with postman like this: DELETE: http://localhost:8080/wireguard-manual/remove  
requestBody: {
    "tenantId": "1245",
    "peerId": "4"
}

IMPOETANT NOTE: you must have Redis installed on your machine to run it successfully
if you don't have it please open terminal and run commands:
1. brew install redis
2. redis-server
