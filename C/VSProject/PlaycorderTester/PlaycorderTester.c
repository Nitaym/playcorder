// PlaycorderTester.cpp : Defines the entry point for the console application.
//

#include "../../Playcorder.h"

int main()
{
	Playcorder_Initialize_Write();

	Playcorder_SavePacket("PACKET1", 7);
	Sleep(1000);
	Playcorder_SavePacket("PACKET2", 7);
	Sleep(1000);
	Playcorder_SavePacket("PACKET3", 7);
	return 0;
}

