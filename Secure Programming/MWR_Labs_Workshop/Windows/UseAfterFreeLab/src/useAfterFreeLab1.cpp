// useAfterFreeLab1.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <iostream>
#include <limits>
#include "menuactions.h"
#include "menu.h"

int _tmain(int argc, _TCHAR* argv[])
{
	menuActions action = menuActions();
	menu m = menu(&action);
	m.mainMenu();
	return 0;
}

