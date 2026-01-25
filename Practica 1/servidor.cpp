#include <iostream>
#include <winsock2.h>
#include <string>

#include <fstream>
using namespace std;


//Declarando funciones

void crear(string nombre);

//Clase servidor 
class Server{
public:
    WSADATA WSAData;
    SOCKET server, client;
    SOCKADDR_IN serverAddr, clientAddr;
    char buffer[1024];
    Server()
    {
        WSAStartup(MAKEWORD(2,0), &WSAData);
        server = socket(AF_INET, SOCK_STREAM, 0);

        serverAddr.sin_addr.s_addr = INADDR_ANY;
        serverAddr.sin_family = AF_INET;
        serverAddr.sin_port = htons(5555);

        bind(server, (SOCKADDR *)&serverAddr, sizeof(serverAddr));
        listen(server, 0);

        cout << "Escuchando para conexiones entrantes\n";
        int clientAddrSize = sizeof(clientAddr);
        if((client = accept(server, (SOCKADDR *)&clientAddr, &clientAddrSize)) != INVALID_SOCKET)
        {
            cout << "Cliente conectado\n";
        }
    }

    string Recibir()
    {
      recv(client, buffer, sizeof(buffer), 0);
      cout << "El cliente dice: " << buffer << "\n";
      crear(buffer);
      memset(buffer, 0, sizeof(buffer));
    }
    void Enviar()
    {
        cout<<"Escribe el mensaje a enviar: ";
        //cin>>this->buffer;
        //buffer="Archivo guardado externamente correctamente";
        string res = "Archivo guardado externamente correctamente";
        strcpy(buffer,res.c_str());
        send(client, buffer, sizeof(buffer), 0);
        memset(buffer, 0, sizeof(buffer));
        cout << "Mensaje enviado" << endl;
    }
    void CerrarSocket()
    {
        closesocket(client);
        cout << "Socket cerrado, cliente desconectado." << endl;
    }
};


int main()
{
  Server *Servidor = new Server();
  while(true)
  {
     Servidor->Recibir();
     Servidor->Enviar();
  }
}



//Crenafo funciones:

void crear(string nombre){
	
	
	//cout<< nombre <<"Desde funcion"; Prueba
	
	nombre.append(".txt");
	//Creando Archivo
	FILE *fp;
	fp  = fopen (nombre.c_str(), "w");
	fclose (fp);
	
	
	
	
	
	
}
