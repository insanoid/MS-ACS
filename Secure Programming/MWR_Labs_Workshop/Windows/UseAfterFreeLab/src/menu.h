#ifndef MENU_H
#define MENU_H
#include <iostream>
class menu
{
private:
    menuActions* actions;
    int getMenuChoice()
    {
        int menuOption = 7;
        if(!(std::cin>>menuOption))
        {
            std::cout<<"error\n";
            std::cin.clear();
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
        }
        return menuOption;
    }
public:
    menu(menuActions* actions)
    {
        this->actions = actions;
    }

    void mainMenu()
    {
        int menuOption = 0;
        do{
            std::cout<<"1) Add user\n";
            std::cout<<"2) Remove user\n";
            std::cout<<"3) Display Stored User\n";
            std::cout<<"4) Edit user\n";
            std::cout<<"5) Display all\n";
            std::cout<<"6) Store User\n";
			std::cout<<"7) Exit\n";
            menuOption = getMenuChoice();
            switch (menuOption)
            {
                case 1:
#ifdef dev
                    std::cout<<"1 pressed\n";
#endif
                    actions->addUser();
                    break;
                case 2:
#ifdef dev
                    std::cout<<"2 pressed\n";
#endif
                    actions->deleteUser();
                    break;
                case 3:
#ifdef dev
                    std::cout<<"3 pressed\n";
#endif
					actions->displaySingleUser();
                    break;
                case 4:
#ifdef dev
                    std::cout<<"4 pressed\n";
#endif
                    actions->editUser();
                    break;
                case 5:
#ifdef dev
                    std::cout<<"5 pressed\n";
#endif
                    actions->displayAllUsers();
                    break;
				case 6:
#ifdef dev
                    std::cout<<"6 pressed\n";
#endif
					actions->storeUser();
                case 7:
#ifdef dev
                    std::cout<<"7 pressed\n";
#endif
                    break;
                default:
					std::cout<<"error reading "<<menuOption<<std::endl;
                    std::cout<<"Not understood\n";
            }
        }while(menuOption != 7);
    }
};

#endif // MENU_H
