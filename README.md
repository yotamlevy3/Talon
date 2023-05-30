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
