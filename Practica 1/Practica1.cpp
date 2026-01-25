#include<iostream>
#include <string>
#include<fstream>
using namespace std;

//Declaracion de funciones
void arCre(string nombre);

//Funcion principal
int main(){

    //Definiendo las variables 
    string nombre; //Nombre del archivo que se va a crear
    int opc = 0,conta= 0,max=1;
	

    //Creando menu del programa 
    
    do{
        cout <<"Seleccione una opcion\n";
        
		cout <<"1.- Crear archivo\n";
		cout <<"2.- Salir\n";
		cin >>opc;
        switch(opc){
        	case 1:{
        		conta++;
        		cout <<"Ingrese el nombre del archivo: ";
        		cin >> nombre;
        		
        		
        			if(conta>max){
						cout << "Numero maximo de archivos alcazando en este disco\n";
			
						cout << "Enviando el archivo a otra computadora\n";
					}else{
						arCre(nombre);
					}

        		
        		
        		
				break;
			}
			case 2:{
				cout << "Adios! \n";
				break;
			}
			default: cout << "Elija una opcion disponible \n";
		}
        
        cout << "Numero de archivos creados: " << conta <<"\n";
	
	
		cout <<"--------------------------------------------\n";
		
	




    }while(opc!=2);



    return 0;

}

//Creacion de funciones

//Funcion para crear archivos

void arCre(string nombre){//Requeire el nombre del archivo a crear
	
	nombre.append(".txt");
	ofstream archivo;
	archivo.open(nombre.c_str());
	 
	if(!archivo){
		cout <<"Error archivo" << nombre<< " no creado\n";
	}else{
		cout << "Archivo " << nombre << " creado correctamente\n";
		archivo.close(); //Cerrando la conexion para evitar problemas
	}
	
	
	
	

}


