// vtablePointerDemo.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <iostream>

using namespace std;

class BaseClass{
public:
	BaseClass():a(1),b(2){}
	virtual void function1(){std::cout<<"BaseClass function 1\n";}
	virtual void function2(){std::cout<<"BaseClass function 2\n";}
	virtual ~BaseClass(){}
private:
	int a;
	int b;
};

class SubClass : public BaseClass{
public:
	SubClass():a(3),b(4){}
	void function1(){std::cout<<"SubClass function 1\n";}
private:
	int a;
	int b;
};

class Class{
public:
	Class():a(0xdeadc0de){}
	void function1(){std::cout<<"Class function 1\n";}
private:
	int a;
};

int _tmain(int argc, _TCHAR* argv[])
{
	cout<<"********BaseClass b = BaseClass()************\n";
	BaseClass* b = new BaseClass();
	cout<<"Address of Object =  "<<(int*)b<<endl;
	for(int i=0;i<sizeof(BaseClass)/4;++i)
		cout<<"Memory["<<i<<"] "<<*((int**)(b)+i)<<endl;

	cout<<"Address of VTable = "<<*(int**)(b)<<endl;
	for(int i=0;i<3;++i)
		cout<<"Memory["<<i<<"] "<<*(*(int***)(b)+i)<<endl;
	
	cout<<"Address of function1 = "<<**(int***)(b)<<endl;
	cout<<"Address of function2 = "<<*(*(int***)(b)+1)<<endl;
	b->function1();
	b->function2();

	cout<<"\n********SubClass s = SubClass()************\n";
	SubClass* s = new SubClass();
	//SubClass* s = dynamic_cast<SubClass*>(b1);
	cout<<"Address of Object =  "<<(int*)s<<endl;
	for(int i=0;i<sizeof(SubClass)/4;++i)
		cout<<"Memory["<<i<<"] "<<*((int**)(s)+i)<<endl;

	cout<<"Address of VTable = "<<*(int**)(s)<<endl;
	for(int i=0;i<3;++i)
		cout<<"Memory["<<i<<"] "<<*(*(int***)(s)+i)<<endl;
	
	cout<<"Address of function1 = "<<**(int***)(s)<<endl;
	cout<<"Address of function2 = "<<*(*(int***)(s)+1)<<endl;

	s->function1();
	s->function2();

	cout<<"\n********Class c = Class()************\n";
	Class* c = new Class();
	cout<<"Address of object: "<<(int*)c<<endl;
	//cout<<"Memory[0]: "<<*(int**)(c)<<endl;
	for(int i=0;i<sizeof(Class)/4;++i)
		cout<<"Memory["<<i<<"] "<<*((int**)(c)+i)<<endl;
	c->function1();
	
	return 0;
}

