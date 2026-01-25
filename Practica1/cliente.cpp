#include <iostream>
#include <winsock2.h>
#include <string>
using namespace std;

class Client{
public:
    WSADATA WSAData;
    SOCKET server;
    SOCKADDR_IN addr;
    char buffer[1024];
    Client()
    {
        cout<<"Conectando al servidor"<<"\n \n";
        WSAStartup(MAKEWORD(2,0), &WSAData);
        server = socket(AF_INET, SOCK_STREAM, 0);
        addr.sin_addr.s_addr = inet_addr("192.168.1.109");
        addr.sin_family = AF_INET;
        addr.sin_port = htons(5555);
        connect(server, (SOCKADDR *)&addr, sizeof(addr));
        cout << "Conectado al Servidor\n";
    }
    void Enviar()
    {
        cout<<"Nombre del archivo: ";
        cin>>this->buffer;
        send(server, buffer, sizeof(buffer), 0);
        memset(buffer, 0, sizeof(buffer));
        cout << "Archivo creado en la otra computadora\n";
    }
    void Recibir()
    {
        recv(server, buffer, sizeof(buffer), 0);
        cout << "El servidor dice: " << buffer << "\n";
        memset(buffer, 0, sizeof(buffer));
    }
    void CerrarSocket()
    {
       closesocket(server);
       WSACleanup();
       cout << "Socket cerrado." << endl << endl;
    }
};

int main()
{
	
	//Definiendo variables locales
    Client *Cliente = new Client();
    string nombre;
    int opc=0, conta=0;
    
    
    
    
    
    
	do{
		
		
		cout<< "Elija una opcion \n";
		
		cout<< "1.-Crear archivo \n2.-Salir \n";
		
		cin >> opc;
		
		switch(opc){
			case 1:{
				
				if(conta<3){
					cout<< "Nombre del archivo ";
					cin >> nombre;
					nombre.append(".txt");
					
					FILE *fp;
					fp = fopen(nombre.c_str(),"w");
					fclose(fp);
					conta++;
				}else{
					cout<< "Numero maximo de arhcivos en la computadora";
					Cliente->Enviar();
				}
				
			
				break;
			}
			case 2:{
				
				break;
			}
		}
		
	}while(opc!=2);
    
    
    //Codigo menu del cliente????
    
    
    
    /* Para probar si funciona la comunicacion
    while(true)
    {
        Cliente->Enviar();
        Cliente->Recibir();
    }
    
    */
}

