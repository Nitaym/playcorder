#ifdef WIN32
#include <Windows.h>
#else
#include <stdbool.h>
#endif

typedef void(*PlaycorderDataReadyCallback)(unsigned char* data, size_t size);


BOOL Playcorder_Initialize_Write();
BOOL Playcorder_Initialize_Read(char* filename, BOOL write);

void Playcorder_SavePacket(void* buffer, size_t size);

void Playcorder_Play(PlaycorderDataReadyCallback callback);