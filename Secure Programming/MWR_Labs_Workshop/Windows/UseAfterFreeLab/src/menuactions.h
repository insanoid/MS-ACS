#ifndef MENUACTIONS_H
#define MENUACTIONS_H
#include <iostream>
#include <cstdlib>
#include <sstream>

#define devOff

class IUser
{
	virtual std::string getName()=0;
};

class User : IUser{
private:
    
public:
	std::string name;
    User* next;
	int id;	//need 2 ints to get heap to allocate memory correctly. Think of a reason for these ints.
	int age;
    std::string getName()
    {
        return name;
    }
    void setName(std::string name)
    {
        this->name = name;
    }
	void setID(int id)
	{
		this->id = id;
	}
	int getID()
	{
		return id;
	}
};

class menuActions
{
private:
    User* userStore; //all users
	User* storedUser; //this is the user we want to show
    User* first;	//first user
	int id;
    int readInt()
    {
        int value = 0;
        if(!(std::cin>>value))
        {
            std::cout<<"error\n";
            std::cin.clear();
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
        }
        return value;
    }

    std::string readString()
    {
        std::string value = "";
		getline(std::cin, value);
				
		if(!(std::cin>>value))
		{
            std::cin.clear();
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
		}
		return value;
    }

public:
    menuActions():userStore(0), storedUser(0), first(0), id(0)
    {
#ifdef dev
		std::cout<<"size of User: "<<sizeof(User)<<std::endl;
#endif
    }
    void addUser()
    {
        std::cout<<"Add User\n--------\n";
		std::cout<<"Name?\n";
        User* user = new User();
        user->setName(readString());
		user->setID(id++);
        if(userStore == 0)
        {
            userStore = user;
            first = user;
			first->next = 0;
        }
        else
        {
            userStore->next = user;
            userStore = userStore->next;
			userStore->next = 0;
        }
		std::cout<<"User: "<<user->getName()<<" added with ID: "<<user->getID()<<std::endl;


#ifdef dev
        std::cout<<"name is "<<userStore->getName()<<std::endl;
		std::cout<<"obj:"<<(int*)userStore<<std::endl;
#endif
    }

    void displayAllUsers()
    {
        std::cout<<"Showing All Users\n-----------------\n";
        User* userList = first;
        while(userList != 0)
        {
            std::cout<<userList->getName()<<std::endl;	
			std::cout<<userList->getID()<<std::endl;	
            userList = userList->next;
        }
    }

	void displaySingleUser()
	{
		std::cout<<"Show User\n---------\n";
		if(storedUser != 0)
		{
			User* user = storedUser;
			if(user != 0)
			{
				std::cout<<"Name: "<<user->getName()<<std::endl;	
				std::cout<<"ID: "<<user->getID()<<std::endl;
			}
		}
		else
		{
			std::cout<<"Please selected a user from the \"Store User\" menu\n";
		}
	}

    void editUser()
    {
		std::cout<<"Edit User\n---------\n";
        std::cout<<"User ID:\n";
        int id = readInt();
        User* userMod = first;
        for(int i=0; userMod->next != 0 && i<id; userMod = userMod->next,++i);
        if(userMod != 0)
        {
            std::cout<<"modifying :"<<userMod->getName()<<std::endl;
            std::cout<<"Please enter a new name:"<<std::endl;
 			userMod->setName(readString()); //need 40 bytes for allocation to happen in correct place
#ifdef dev
			std::cout<<"address of name:"<<(int*)userMod->name.c_str()<<std::endl;
			std::cout<<"size of string"<<strlen(userMod->name.c_str())<<std::endl;
#endif
        }
    }

    void deleteUser()
    {
		std::cout<<"Delete User\n-----------\n";
        std::cout<<"User ID:\n";
        unsigned int id = readInt();
        User* user = first;
		User* prev = first;
        
		for(user = first; user != 0; user = user->next)
		{
				if(user->id == id)
				{
					if(first == userStore)
					{
						first = userStore = 0;
						this->id = 0;
					}
					else if(first == user)
					{
						first = user->next;
					}
					
					else
					{
						if(!user->next)
						{
							userStore = prev;
						}
						prev->next = user->next;
					}
					delete user;
					break;
				}
				prev = user;
		}
    }

	void storeUser()
	{
		std::cout<<"Store User\n---------\n";
		std::cout<<"user ID:\n";
		int id = readInt();
        User* user = first;
		if(user != 0)
        {
			for(int i=0; user->next != 0 && i<id; user = user->next,++i);

            std::cout<<"Storing user "<<user->getName()<<std::endl;	
			storedUser = user;
		}
	}
};
#endif // MENUACTIONS_H
